/*
 * Copyright 2021 EPAM Systems
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

package com.epam.reportportal.extension.template.command.bts;

import com.epam.reportportal.api.model.PluginCommandRQ;
import com.epam.reportportal.base.infrastructure.model.externalsystem.Ticket;
import com.epam.reportportal.base.infrastructure.persistence.dao.IntegrationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.TicketRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.entity.organization.OrganizationRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.project.ProjectRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.user.UserRole;
import com.epam.reportportal.base.infrastructure.rules.exception.ErrorType;
import com.epam.reportportal.base.infrastructure.rules.exception.ReportPortalException;
import com.epam.reportportal.extension.command.AbstractExtensionCommand;
import com.epam.reportportal.extension.template.model.BtsRqProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

/**
 * @author <a href="mailto:pavel_bortnik@epam.com">Pavel Bortnik</a>
 */
public class GetIssueCommand extends AbstractExtensionCommand<Ticket> {

  private final String TICKET_ID = "ticketId";
  private final String PROJECT_ID = "projectId";

  private final TicketRepository ticketRepository;
  private final IntegrationRepository integrationRepository;
  private final ObjectMapper objectMapper;

  public GetIssueCommand(TicketRepository ticketRepository,
      IntegrationRepository integrationRepository,
      ProjectRepository projectRepository,
      OrganizationUserRepository organizationUserRepository,
      OrganizationRepository organizationRepository,
      ProjectUserRepository projectUserRepository,
      ObjectMapper objectMapper) {
    super(projectRepository, organizationUserRepository, organizationRepository, projectUserRepository);
    this.ticketRepository = ticketRepository;
    this.integrationRepository = integrationRepository;
    this.objectMapper = objectMapper;

    // Set required permission levels
    this.minProjectRole = ProjectRole.EDITOR;
    this.minOrgRole = OrganizationRole.MANAGER;
    this.minUserRole = UserRole.ADMINISTRATOR;
  }

  @Override
  public String getName() {
    return "getIssue";
  }

  @Override
  public Ticket executeCommand(PluginCommandRQ pluginCommandRq) {
    var params = pluginCommandRq.getArguments();

    var ticketId = Optional.ofNullable(params.get(TICKET_ID))
        .map(String::valueOf)
        .orElseThrow(() -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR, TICKET_ID + "  must be provided"));

    /* Optional
    var ticket = ticketRepository.findByTicketId(ticketId)
        .orElseThrow(
            () -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR, "Ticket not found with id " + TICKET_ID));
    */
    var projectId = (Long) Optional.ofNullable(params.get(PROJECT_ID))
        .orElseThrow(() -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR, PROJECT_ID + " must be provided"));
    var btsUrl = BtsRqProperties.URL.getParam(params)
        .orElseThrow(
            () -> new ReportPortalException(ErrorType.UNABLE_INTERACT_WITH_INTEGRATION, "Url is not specified."));
    var btsProject = BtsRqProperties.PROJECT.getParam(params)
        .orElseThrow(
            () -> new ReportPortalException(ErrorType.UNABLE_INTERACT_WITH_INTEGRATION, "Project is not specified."));

    var integration = integrationRepository.findProjectBtsByUrlAndLinkedProject(btsUrl, btsProject, projectId)
        .orElseGet(() -> integrationRepository.findGlobalBtsByUrlAndLinkedProject(btsUrl, btsProject)
            .orElseThrow(() -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR,
                "Integration with provided url and project isn't found")));

    return new Ticket();
  }


}
