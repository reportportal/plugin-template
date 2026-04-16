import projectNavIcon from 'icons/project-sidebar.svg';
import { defineMessages, useIntl } from 'react-intl';
import { useSelector } from 'react-redux';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface ProjectSidebarNavProps
  extends Pick<HostInjectedExtensionProps, 'components' | 'constants' | 'selectors'> {}

const messages = defineMessages({
  label: {
    id: 'PluginTemplate.projectNav.label',
    defaultMessage: 'Template plugin — project page',
  },
});

/** Project sidebar → `PROJECT_PLUGIN_PAGE`; `pluginPage` must match `slug` / `name` of the `projectPage` extension. */
const ProjectSidebarNav = (props: ProjectSidebarNavProps) => {
  const { formatMessage } = useIntl();
  const {
    components: { SidebarButton },
    constants: { PROJECT_PLUGIN_PAGE },
    selectors: { urlOrganizationAndProjectSelector },
  } = props as any;
  const { organizationSlug, projectSlug } = useSelector(urlOrganizationAndProjectSelector) as any;

  const link = {
    type: PROJECT_PLUGIN_PAGE,
    payload: { organizationSlug, projectSlug, pluginPage: 'template' },
  };

  return (
    <SidebarButton
      icon={projectNavIcon}
      link={link}
      message={formatMessage(messages.label)}
      onClick={() => {}}
    />
  );
};

export default ProjectSidebarNav;
