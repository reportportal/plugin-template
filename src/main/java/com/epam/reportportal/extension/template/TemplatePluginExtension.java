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

import com.epam.reportportal.core.events.domain.PluginUploadedEvent;
import com.epam.reportportal.extension.CommonPluginCommand;
import com.epam.reportportal.extension.PluginCommand;
import com.epam.reportportal.extension.ReportPortalExtensionPoint;
import com.epam.reportportal.extension.template.command.TemplateCommand;
import com.epam.reportportal.extension.template.event.plugin.PluginEventHandlerFactory;
import com.epam.reportportal.extension.template.event.plugin.PluginEventListener;
import com.epam.reportportal.extension.template.utils.MemoizingSupplier;
import com.epam.reportportal.infrastructure.persistence.dao.IntegrationRepository;
import com.epam.reportportal.infrastructure.persistence.dao.IntegrationTypeRepository;
import com.epam.reportportal.infrastructure.persistence.dao.organization.OrganizationRepositoryCustom;
import com.epam.reportportal.infrastructure.persistence.dao.organization.OrganizationUserRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

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
@Service
@Slf4j
public class TemplatePluginExtension implements ReportPortalExtensionPoint, DisposableBean {

  private static final String DEFAULT_PLUGIN_ID = "template";
  public final String pluginId;

  private final Supplier<Map<String, PluginCommand<?>>> pluginCommandMapping =
      new MemoizingSupplier<>(this::getCommands);

  private final Supplier<Map<String, CommonPluginCommand<?>>> commonPluginCommandMapping =
      new MemoizingSupplier<>(this::getCommonCommands);
  private final Supplier<ApplicationListener<PluginUploadedEvent>> pluginLoadedListener;

  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private IntegrationTypeRepository integrationTypeRepository;
  @Autowired
  private IntegrationRepository integrationRepository;
  @Autowired
  private OrganizationRepositoryCustom organizationRepository;
  @Autowired
  private OrganizationUserRepository organizationUserRepository;
  @Autowired
  private DataSource dataSource;


  /**
   * Creates a new instance of the extension.
   *
   * <p>Reads the plugin id from the plugin manifest (falling back to {@code DEFAULT_PLUGIN_ID})
   * and initializes a memorizing supplier for the {@link PluginEventListener}. The actual repositories are injected by
   * Spring and will be used when the supplier is first invoked. Remove the PluginEventListener initialization if the
   * plugin does not require processing plugin events
   */
  public TemplatePluginExtension() {
    this.pluginId = readPluginIdFromManifest(this.getClass(), DEFAULT_PLUGIN_ID);
    pluginLoadedListener = new MemoizingSupplier<>(() -> new PluginEventListener(
        pluginId,
        new PluginEventHandlerFactory(integrationTypeRepository, integrationRepository)
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
  }


  /**
   * Invoked when the Spring bean is being destroyed. Removes registered application listeners to avoid memory leaks and
   * cleanup plugin-related resources.
   */
  @Override
  public void destroy() {
    removeListeners();
  }

  private void removeListeners() {
    ApplicationEventMulticaster applicationEventMulticaster = applicationContext.getBean(
        AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
        ApplicationEventMulticaster.class
    );
    applicationEventMulticaster.removeApplicationListener(pluginLoadedListener.get());
  }

  /**
   * Retrieves a map of plugin parameters.
   *
   * @return A map containing allowed commands and common commands.
   */
  @Override
  public Map<String, ?> getPluginParams() {
    Map<String, Object> params = new HashMap<>();
    params.put(ALLOWED_COMMANDS, new ArrayList<>(pluginCommandMapping.get().keySet()));
    params.put(COMMON_COMMANDS, new ArrayList<>(commonPluginCommandMapping.get().keySet()));
    return params;
  }

  /**
   * Retrieves a common plugin command by its name.
   *
   * @param commandName The name of the command.
   * @return The corresponding CommonPluginCommand, or null if not found.
   */
  @Override
  public CommonPluginCommand<?> getCommonCommand(String commandName) {
    return commonPluginCommandMapping.get().get(commandName);
  }

  /**
   * Retrieves an integration command by its name.
   *
   * @param commandName The name of the command.
   * @return The corresponding PluginCommand, or null if not found.
   */
  @Override
  public PluginCommand<?> getIntegrationCommand(String commandName) {
    return pluginCommandMapping.get().get(commandName);
  }

  /**
   * Retrieves a map of plugin commands.
   */
  private Map<String, PluginCommand<?>> getCommands() {
    return new HashMap<>();
  }

  private Map<String, CommonPluginCommand<?>> getCommonCommands() {
    HashMap<String, CommonPluginCommand<?>> pluginCommands = new HashMap<>();
    TemplateCommand templatePlugin = new TemplateCommand();
    pluginCommands.put(templatePlugin.getName(), templatePlugin);
    return pluginCommands;
  }
}
