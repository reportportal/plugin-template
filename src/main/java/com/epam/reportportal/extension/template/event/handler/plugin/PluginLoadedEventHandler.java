package com.epam.reportportal.extension.template.event.handler.plugin;

import com.epam.reportportal.extension.event.PluginEvent;
import com.epam.reportportal.extension.template.event.handler.EventHandler;
import com.epam.ta.reportportal.dao.IntegrationRepository;
import com.epam.ta.reportportal.dao.IntegrationTypeRepository;
import com.epam.ta.reportportal.entity.integration.Integration;
import com.epam.ta.reportportal.entity.integration.IntegrationParams;
import com.epam.ta.reportportal.entity.integration.IntegrationType;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;


/**
 * @author Andrei Piankouski
 */
public class PluginLoadedEventHandler implements EventHandler<PluginEvent> {

	private final IntegrationTypeRepository integrationTypeRepository;
	private final IntegrationRepository integrationRepository;

	public PluginLoadedEventHandler(IntegrationTypeRepository integrationTypeRepository,
			IntegrationRepository integrationRepository) {
		this.integrationTypeRepository = integrationTypeRepository;
		this.integrationRepository = integrationRepository;
	}

	@Override
	public void handle(PluginEvent event) {
		integrationTypeRepository.findByName(event.getPluginId()).ifPresent(integrationType ->
			createIntegration(event.getPluginId(), integrationType));
	}

	private void createIntegration(String name, IntegrationType integrationType) {
		List<Integration> integrations = integrationRepository.findAllGlobalByType(integrationType);
		if (integrations.isEmpty()) {
			Integration integration = new Integration();
			integration.setName(name);
			integration.setType(integrationType);
			integration.setCreationDate(Instant.now());
			integration.setEnabled(true);
			integration.setCreator("SYSTEM");
			integration.setParams(new IntegrationParams(new HashMap<>()));
			integrationRepository.save(integration);
		}
	}
}
