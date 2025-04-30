package com.epam.reportportal.extension.template;

import com.epam.reportportal.extension.CommonPluginCommand;
import com.epam.reportportal.extension.PluginCommand;
import com.epam.reportportal.extension.ReportPortalExtensionPoint;
import com.epam.reportportal.extension.event.PluginEvent;
import com.epam.reportportal.extension.template.command.TemplateCommand;
import com.epam.reportportal.extension.template.event.plugin.PluginEventHandlerFactory;
import com.epam.reportportal.extension.template.event.plugin.PluginEventListener;
import com.epam.reportportal.extension.template.utils.MemoizingSupplier;
import com.epam.ta.reportportal.dao.IntegrationRepository;
import com.epam.ta.reportportal.dao.IntegrationTypeRepository;
import com.epam.ta.reportportal.dao.LogRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import org.pf4j.Extension;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Andrei Piankouski
 */
@Extension
@Service
public class TemplatePluginExtension implements ReportPortalExtensionPoint, DisposableBean {
    private final Supplier<Map<String, PluginCommand>> pluginCommandMapping = new MemoizingSupplier<>(this::getCommands);

    private final Supplier<Map<String, CommonPluginCommand<?>>> commonPluginCommandMapping = new MemoizingSupplier<>(this::getCommonCommands);

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ApplicationContext applicationContext;

    // Delete pluginLoadedListener if you don't need to create default integration.
    private final Supplier<ApplicationListener<PluginEvent>> pluginLoadedListener;

    // Delete integrationTypeRepository if you don't need to create default integration.
    @Autowired
    private IntegrationTypeRepository integrationTypeRepository;

    // Delete integrationTypeRepository if you don't need to create default integration.
    @Autowired
    private IntegrationRepository integrationRepository;

    // Delete TemplatePluginExtension if you don't need to create default integration.
    public TemplatePluginExtension(Map<String, Object> initParams) {
        pluginLoadedListener = new MemoizingSupplier<>(() -> new PluginEventListener(
            "template",
            new PluginEventHandlerFactory(integrationTypeRepository, integrationRepository)
        ));
    }

    // Delete this constructor if you don't need to create default integration.
    @PostConstruct
    public void createIntegration() {
        initListeners();
    }

    // Delete this method if you don't need to create default integration.
    private void initListeners() {
        ApplicationEventMulticaster applicationEventMulticaster = applicationContext.getBean(
            AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
            ApplicationEventMulticaster.class
        );
        applicationEventMulticaster.addApplicationListener(pluginLoadedListener.get());
    }

    // Delete this method if you don't need to create default integration.
    @Override
    public void destroy() {
        removeListeners();
    }

    // Delete this method if you don't need to create default integration.
    private void removeListeners() {
        ApplicationEventMulticaster applicationEventMulticaster = applicationContext.getBean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
            ApplicationEventMulticaster.class
        );
        applicationEventMulticaster.removeApplicationListener(pluginLoadedListener.get());
    }

  @Override
  public Map<String, ?> getPluginParams() {
    Map<String, Object> params = new HashMap<>();
    params.put(ALLOWED_COMMANDS, new ArrayList<>(pluginCommandMapping.get().keySet()));
    params.put(COMMON_COMMANDS, new ArrayList<>(commonPluginCommandMapping.get().keySet()));
    return params;
  }

  @Override
  public CommonPluginCommand getCommonCommand(String commandName) {
    return commonPluginCommandMapping.get().get(commandName);
  }

  @Override
  public PluginCommand getIntegrationCommand(String commandName) {
    return pluginCommandMapping.get().get(commandName);
  }

  private Map<String, PluginCommand> getCommands() {
    HashMap<String, PluginCommand> pluginCommands = new HashMap<>();
    TemplateCommand templatePlugin = new TemplateCommand();
    pluginCommands.put(templatePlugin.getName(), templatePlugin);
    return pluginCommands;
  }

  private Map<String, CommonPluginCommand<?>> getCommonCommands() {
    HashMap<String, CommonPluginCommand<?>> pluginCommands = new HashMap<>();
    return pluginCommands;
  }
}
