package com.epam.reportportal.extension.template;

import com.epam.reportportal.extension.common.IntegrationTypeProperties;
import com.epam.reportportal.extension.event.PluginEvent;
import com.epam.reportportal.extension.template.command.TemplateCommand;
import com.epam.reportportal.extension.template.event.plugin.PluginEventHandlerFactory;
import com.epam.reportportal.extension.template.event.plugin.PluginEventListener;
import com.epam.reportportal.extension.template.utils.MemoizingSupplier;
import com.epam.reportportal.extension.CommonPluginCommand;
import com.epam.reportportal.extension.PluginCommand;
import com.epam.reportportal.extension.ReportPortalExtensionPoint;
import com.epam.reportportal.extension.util.RequestEntityConverter;
import com.epam.ta.reportportal.dao.IntegrationRepository;
import com.epam.ta.reportportal.dao.IntegrationTypeRepository;
import com.epam.ta.reportportal.dao.LogRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Andrei Piankouski
 */
public class TemplatePluginExtension implements ReportPortalExtensionPoint {

    private static final String PLUGIN_ID = "template";
    public static final String BINARY_DATA_PROPERTIES_FILE_ID = "binary-data.properties";

    private final Supplier<Map<String, PluginCommand>> pluginCommandMapping = new MemoizingSupplier<>(this::getCommands);

    private final Supplier<Map<String, CommonPluginCommand<?>>> commonPluginCommandMapping = new MemoizingSupplier<>(this::getCommonCommands);

    private final ObjectMapper objectMapper;
    private final RequestEntityConverter requestEntityConverter;

    private final String resourcesDir;

    private final Supplier<ApplicationListener<PluginEvent>> pluginLoadedListener;

    @Autowired
    private IntegrationTypeRepository integrationTypeRepository;

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ApplicationContext applicationContext;

    public TemplatePluginExtension(Map<String, Object> initParams) {
        resourcesDir = IntegrationTypeProperties.RESOURCES_DIRECTORY.getValue(initParams).map(String::valueOf).orElse("");
        objectMapper = configureObjectMapper();

        pluginLoadedListener = new MemoizingSupplier<>(() -> new PluginEventListener(PLUGIN_ID,
                new PluginEventHandlerFactory(resourcesDir,
                        integrationTypeRepository,
                        integrationRepository
                )
        ));

        requestEntityConverter = new RequestEntityConverter(objectMapper);
    }

    protected ObjectMapper configureObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        om.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.registerModule(new JavaTimeModule());
        return om;
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
