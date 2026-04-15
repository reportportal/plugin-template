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

package com.epam.reportportal.extension.template.event.handler;

import com.epam.reportportal.core.events.domain.PluginUploadedEvent;
import com.epam.reportportal.infrastructure.persistence.dao.IntegrationRepository;
import com.epam.reportportal.infrastructure.persistence.dao.IntegrationTypeRepository;
import com.epam.reportportal.infrastructure.persistence.entity.integration.Integration;
import com.epam.reportportal.infrastructure.persistence.entity.integration.IntegrationParams;
import com.epam.reportportal.infrastructure.persistence.entity.integration.IntegrationType;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;


/**
 * @author Andrei Piankouski
 */
public class PluginLoadedEventHandler implements EventHandler<PluginUploadedEvent> {

  private final IntegrationTypeRepository integrationTypeRepository;
  private final IntegrationRepository integrationRepository;

  public PluginLoadedEventHandler(IntegrationTypeRepository integrationTypeRepository,
      IntegrationRepository integrationRepository) {
    this.integrationTypeRepository = integrationTypeRepository;
    this.integrationRepository = integrationRepository;
  }

  @Override
  public void handle(PluginUploadedEvent event) {
    integrationTypeRepository.findByName(event.getPluginActivityResource().getName())
        .ifPresent(integrationType -> createIntegration(event.getPluginActivityResource().getName(), integrationType));
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
