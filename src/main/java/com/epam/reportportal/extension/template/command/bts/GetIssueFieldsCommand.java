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
import com.epam.reportportal.base.infrastructure.model.externalsystem.PostFormField;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.ProjectUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationRepository;
import com.epam.reportportal.base.infrastructure.persistence.dao.organization.OrganizationUserRepository;
import com.epam.reportportal.base.infrastructure.persistence.entity.integration.Integration;
import com.epam.reportportal.base.infrastructure.persistence.entity.organization.OrganizationRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.project.ProjectRole;
import com.epam.reportportal.base.infrastructure.persistence.entity.user.UserRole;
import com.epam.reportportal.base.infrastructure.rules.exception.ErrorType;
import com.epam.reportportal.base.infrastructure.rules.exception.ReportPortalException;
import com.epam.reportportal.extension.command.AbstractExtensionCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:pavel_bortnik@epam.com">Pavel Bortnik</a>
 */
@Slf4j
public class GetIssueFieldsCommand extends AbstractExtensionCommand<List<PostFormField>> {

  public static final String ISSUE_TYPE = "issueType";

  private final ObjectMapper objectMapper;

  public GetIssueFieldsCommand(ProjectRepository projectRepository,
      OrganizationUserRepository organizationUserRepository,
      OrganizationRepository organizationRepository,
      ProjectUserRepository projectUserRepository,
      ObjectMapper objectMapper) {
    super(projectRepository, organizationUserRepository, organizationRepository, projectUserRepository);
    this.objectMapper = objectMapper;

    // Set required permission levels
    this.minProjectRole = ProjectRole.EDITOR;
    this.minOrgRole = OrganizationRole.MANAGER;
    this.minUserRole = UserRole.ADMINISTRATOR;
  }

  @Override
  public String getName() {
    return "getIssueFields";
  }

  @Override
  protected List<PostFormField> invokeCommand(Integration integration, PluginCommandRQ pluginCommandRq) {
    var params = pluginCommandRq.getArguments();
    String issueType = getIssueType(params);

    // TODO: download filed list
    List<PostFormField> result = new ArrayList<>();

    try {
      return result;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  private static String getIssueType(Map<String, Object> params) {
    return Optional.ofNullable(params.get(ISSUE_TYPE))
        .map(it -> (String) it)
        .orElseThrow(() -> new ReportPortalException(ErrorType.BAD_REQUEST_ERROR, "Issue type is not provided"));
  }

}
