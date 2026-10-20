/*
 * Copyright 2025 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.reportportal.extension.template;

import static com.epam.reportportal.extension.util.PluginManifestUtils.readPluginIdFromManifest;

import com.epam.reportportal.base.core.events.domain.PluginDeletedEvent;
import com.epam.reportportal.base.core.events.domain.PluginUploadedEvent;
import com.epam.reportportal.base.infrastructure.persistence.dao.IntegrationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.IntegrationTypeRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.TicketRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationRepositoryCustom;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationUserRepository;
import com.epam.reportportal.extension.CommonPluginCommand;
import com.epam.reportportal.extension.PluginCommand;
import com.epam.reportportal.extension.ReportPortalExtensionPoint;
import com.epam.reportportal.extension.command.ExtensionCommand;
import com.epam.reportportal.extension.template.command.bts.GetIssueCommand;
import com.epam.reportportal.extension.template.command.bts.GetIssueFieldsCommand;
import com.epam.reportportal.extension.template.command.bts.GetIssueTypesCommand;
import com.epam.reportportal.extension.template.command.bts.PostTicketCommand;
import com.epam.reportportal.extension.template.command.test.TestConnectionCommand;
import com.epam.reportportal.extension.template.event.handler.PluginDeletedEventHandler;
import com.epam.reportportal.extension.template.event.handler.PluginLoadedEventHandler;
import com.epam.reportportal.extension.template.event.listener.PluginDeletedEventListener;
import com.epam.reportportal.extension.template.event.listener.PluginLoadedEventListener;
import com.epam.reportportal.extension.util.MemoizingSupplier;
import com.epam.reportportal.extension.util.RequestEntityConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Use this extension if you need to handle events and create a default integration.
 *
 * <p>This extension registers an application listener to handle {@code PluginUploadedEvent}
 * for the plugin and executes optional SQL migration scripts located in `resources/schema` on startup.
 *
 * <p>If your plugin does not require explicit lifecycle control (for example, you don't need to
 * remove registered listeners on shutdown or perform other cleanup), you may remove the
 * {@link org.springframework.beans.factory.DisposableBean} interface from the class declaration. Keeping
 * {@code DisposableBean} allows the extension to clean up registered resources (listeners, migrations, etc.) when the
 * Spring bean is destroyed.
 *
 * @author Andrei Piankouski
 */
@Extension
@Slf4j
public class TemplatePluginExtension implements ReportPortalExtensionPoint, DisposableBean {

  private static final String DEFAULT_PLUGIN_ID = "template";
  public final String pluginId;

  private final Supplier<Map<String, ExtensionCommand<?>>> commonExtensionCommandMapping =
    new MemoizingSupplier<>(this::getCommonExtensionCommands);

  private final Supplier<Map<String, ExtensionCommand<?>>> integrationExtensionCommandMapping =
    new MemoizingSupplier<>(this::getIntegrationExtensionCommands);

  private final Supplier<ApplicationListener<PluginUploadedEvent>> pluginLoadedListener;
  private final Supplier<ApplicationListener<PluginDeletedEvent>> pluginDeletedListener;

  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private IntegrationTypeRepository integrationTypeRepository;
  @Autowired
  private IntegrationRepository integrationRepository;
  @Autowired
  private OrganizationRepositoryCustom organizationRepositoryCustom;
  @Autowired
  private OrganizationRepository organizationRepository;
  @Autowired
  private OrganizationUserRepository organizationUserRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectUserRepository projectUserRepository;
  @Autowired
  private TicketRepository ticketRepository;
  @Autowired
  private DataSource dataSource;

  @Autowired
  @Lazy
  private ObjectMapper objectMapper;


  /**
   * Creates a new instance of the extension.
   *
   * <p>Reads the plugin id from the plugin manifest (falling back to {@code DEFAULT_PLUGIN_ID})
   * and initializes a memorizing supplier for the {@link PluginLoadedEventListener}. The actual repositories are
   * injected by Spring and will be used when the supplier is first invoked. Remove the PluginLoadedEventListener
   * initialization if the plugin does not require processing plugin events
   */
  public TemplatePluginExtension() {
    this.pluginId = readPluginIdFromManifest(this.getClass(), DEFAULT_PLUGIN_ID);
    pluginLoadedListener = new MemoizingSupplier<>(() -> new PluginLoadedEventListener(
      pluginId, new PluginLoadedEventHandler(integrationTypeRepository, integrationRepository)
    ));
    pluginDeletedListener = new MemoizingSupplier<>(() -> new PluginDeletedEventListener(
      pluginId,
      new PluginDeletedEventHandler(integrationTypeRepository, integrationRepository)
    ));
  }

  /**
   * Initializes the plugin by registering listeners and executing migration scripts.
   */
  @PostConstruct
  public void initializePlugin() throws IOException {
    initListeners();
    executeMigrationScripts();
  }


  /**
   * Execute SQL migration scripts from `schema` on startup. Remove this call if the plugin does not require database
   * schema initialization.
   */
  private void executeMigrationScripts() throws IOException {
    try {
      PathMatchingResourcePatternResolver resolver =
        new org.springframework.core.io.support.PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
      Resource[] resources = resolver.getResources("classpath:resources/schema/*.sql");
      if (resources.length == 0) {
        log.warn("No SQL migration scripts found in classpath:resources/schema/*.sql");
        return;
      }
      ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(resources);
      resourceDatabasePopulator.execute(dataSource);
    } catch (Exception e) {
      throw new IOException("Failed to execute migration scripts", e);
    }
  }

  /**
   * Initialize and register plugin-related application listeners.
   * <p>
   * Registers a listener that handles {@code PluginUploadedEvent} for this plugin using the application context's
   * {@link ApplicationEventMulticaster}.
   */
  private void initListeners() {
    ApplicationEventMulticaster applicationEventMulticaster = applicationContext.getBean(
      AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
      ApplicationEventMulticaster.class
    );
    applicationEventMulticaster.addApplicationListener(pluginLoadedListener.get());
    applicationEventMulticaster.addApplicationListener(pluginDeletedListener.get());
  }


  /**
   * Invoked when the Spring bean is being destroyed. Removes registered application listeners to avoid memory leaks and
   * cleanup plugin-related resources.
   */
  @Override
  public void destroy() {
    removeListeners();
  }

  /**
   * Remove the application listeners registered by {@link #initListeners()} from the application context's
   * {@link ApplicationEventMulticaster}.
   */
  private void removeListeners() {
    ApplicationEventMulticaster applicationEventMulticaster = applicationContext.getBean(
      AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
      ApplicationEventMulticaster.class
    );
    applicationEventMulticaster.removeApplicationListener(pluginLoadedListener.get());
    applicationEventMulticaster.removeApplicationListener(pluginDeletedListener.get());
  }

  /**
   * Retrieves a map of plugin parameters.
   *
   * @return A map containing allowed commands and common commands {@code ExtensionCommand} APIs.
   */
  @Override
  public Map<String, ?> getPluginParams() {
    Map<String, Object> params = new HashMap<>();

    List<String> allowedCommands = new ArrayList<>(integrationExtensionCommandMapping.get().keySet());
    params.put(ALLOWED_COMMANDS, allowedCommands);

    List<String> commonCommands = new ArrayList<>(commonExtensionCommandMapping.get().keySet());
    params.put(COMMON_COMMANDS, commonCommands);
    return params;
  }

  /**
   * Retrieves a common plugin command by its name.
   *
   * @param commandName The name of the command.
   * @return The corresponding CommonPluginCommand, or null if not found.
   */
  @Override
  @Deprecated(forRemoval = true)
  public CommonPluginCommand<?> getCommonCommand(String commandName) {
    return null;
  }

  /**
   * Retrieves an integration command by its name.
   *
   * @param commandName The name of the command.
   * @return The corresponding PluginCommand, or null if not found.
   */
  @Override
  @Deprecated(forRemoval = true)
  public PluginCommand<?> getIntegrationCommand(String commandName) {
    return null;
  }

  /**
   * Common (not integration-scoped) commands registered through the {@code ExtensionCommand} API.
   */
  @Override
  public Map<String, ExtensionCommand<?>> getCommonExtensionCommands() {
    List<ExtensionCommand<?>> commands = new ArrayList<>();

    commands.add(
      new GetIssueCommand(ticketRepository,
        integrationRepository,
        projectRepository,
        organizationUserRepository,
        organizationRepository,
        projectUserRepository,
        objectMapper));

    return commands.stream()
      .collect(Collectors.toMap(ExtensionCommand::getName, it -> it));
  }

  /**
   * Integration-scoped commands registered through the {@code ExtensionCommand} API.
   */
  @Override
  public Map<String, ExtensionCommand<?>> getIntegrationExtensionCommands() {
    List<ExtensionCommand<?>> commands = new ArrayList<>();
    commands.add(new TestConnectionCommand(projectRepository,
      organizationUserRepository,
      organizationRepository,
      projectUserRepository,
      objectMapper));
    commands.add(new GetIssueFieldsCommand(projectRepository,
      organizationUserRepository,
      organizationRepository,
      projectUserRepository,
      objectMapper));
    commands.add(new GetIssueTypesCommand(projectRepository,
      organizationUserRepository,
      organizationRepository,
      projectUserRepository,
      objectMapper));
    commands.add(new PostTicketCommand(projectRepository,
      new RequestEntityConverter(objectMapper),
      organizationUserRepository,
      organizationRepository,
      projectUserRepository,
      objectMapper));
    return commands.stream().collect(Collectors.toMap(ExtensionCommand::getName, it -> it));
  }

}
