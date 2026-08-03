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

import static com.epam.reportportal.base.infrastructure.persistence.commons.Predicates.isNull;
import static com.epam.reportportal.base.infrastructure.rules.commons.validation.BusinessRule.expect;
import static com.epam.reportportal.base.infrastructure.rules.exception.ErrorType.UNABLE_INTERACT_WITH_INTEGRATION;
import static com.epam.reportportal.extension.util.CommandParamUtils.ENTITY_PARAM;
import static java.util.function.Predicate.not;

import com.epam.reportportal.api.model.PluginCommandRQ;
import com.epam.reportportal.base.infrastructure.model.externalsystem.PostFormField;
import com.epam.reportportal.base.infrastructure.model.externalsystem.PostTicketRQ;
import com.epam.reportportal.base.infrastructure.model.externalsystem.Ticket;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.entity.integration.Integration;
import com.epam.reportportal.base.infrastructure.persistence.entity.organization.OrganizationRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.project.ProjectRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.user.UserRole;
import com.epam.reportportal.extension.command.AbstractExtensionCommand;
import com.epam.reportportal.extension.util.RequestEntityConverter;
import com.epam.reportportal.extension.util.RequestEntityValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:pavel_bortnik@epam.com">Pavel Bortnik</a>
 */
@Slf4j
public class PostTicketCommand extends AbstractExtensionCommand<Ticket> {

  private final RequestEntityConverter requestEntityConverter;
  private final ObjectMapper objectMapper;

  public PostTicketCommand(ProjectRepository projectRepository,
      RequestEntityConverter requestEntityConverter,
      OrganizationUserRepository organizationUserRepository,
      OrganizationRepository organizationRepository,
      ProjectUserRepository projectUserRepository,
      ObjectMapper objectMapper) {
    super(projectRepository, organizationUserRepository, organizationRepository, projectUserRepository);
    this.requestEntityConverter = requestEntityConverter;
    this.objectMapper = objectMapper;

    // Set required permission levels
    this.minProjectRole = ProjectRole.EDITOR;
    this.minOrgRole = OrganizationRole.MANAGER;
    this.minUserRole = UserRole.ADMINISTRATOR;
  }

  @Override
  public String getName() {
    return "postTicket";
  }

  @Override
  protected Ticket invokeCommand(Integration integration, PluginCommandRQ pluginCommandRq) {
    var params = pluginCommandRq.getArguments();
    PostTicketRQ ticketRQ = requestEntityConverter.getEntity(ENTITY_PARAM, params, PostTicketRQ.class);
    RequestEntityValidator.validate(ticketRQ);
    List<PostFormField> fields = ticketRQ.getFields();

    expect(fields, not(isNull()))
        .verify(UNABLE_INTERACT_WITH_INTEGRATION, "External System fields set is empty!");

    // TODO: implement BTS ticket creation

    return new Ticket();
  }


}
