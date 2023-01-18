package com.epam.reportportal.extension.template;

import com.epam.reportportal.extension.template.command.TemplateCommand;
import com.epam.reportportal.extension.template.utils.MemoizingSupplier;
import com.epam.reportportal.extension.CommonPluginCommand;
import com.epam.reportportal.extension.PluginCommand;
import com.epam.reportportal.extension.ReportPortalExtensionPoint;
import com.epam.ta.reportportal.dao.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Andrei Piankouski
 */
public class TemplatePluginExtension implements ReportPortalExtensionPoint {

	private final Supplier<Map<String, PluginCommand>> pluginCommandMapping = new MemoizingSupplier<>(this::getCommands);

	private final Supplier<Map<String, CommonPluginCommand<?>>> commonPluginCommandMapping = new MemoizingSupplier<>(this::getCommonCommands);

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private ApplicationContext applicationContext;

	public TemplatePluginExtension(Map<String, Object> initParams) {
	}

	@Override
	public Map<String, ?> getPluginParams() {
		Map<String, Object> params = new HashMap<>();
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
