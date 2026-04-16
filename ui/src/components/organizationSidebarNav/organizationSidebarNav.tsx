import orgNavIcon from 'icons/org-sidebar.svg';
import { defineMessages, useIntl } from 'react-intl';
import { useSelector } from 'react-redux';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface OrganizationSidebarNavProps
  extends Pick<HostInjectedExtensionProps, 'components' | 'constants' | 'selectors'> {}

const messages = defineMessages({
  label: {
    id: 'PluginTemplate.orgNav.label',
    defaultMessage: 'Template plugin — organization settings',
  },
});

/** Organization sidebar shortcut → Organization settings tab (same `settingsTab` as org settings extension `name`). */
const OrganizationSidebarNav = (props: OrganizationSidebarNavProps) => {
  const { formatMessage } = useIntl();

  const {
    components: { SidebarButton },
    constants: { ORGANIZATION_SETTINGS_TAB_PAGE },
    selectors: { urlOrganizationSlugSelector },
  } = props as any;
  const organizationSlug = useSelector(urlOrganizationSlugSelector);

  const link = {
    type: ORGANIZATION_SETTINGS_TAB_PAGE,
    payload: {
      organizationSlug,
      settingsTab: 'orgSettingsTemplate',
    },
  };

  return (
    <SidebarButton
      icon={orgNavIcon}
      link={link}
      message={formatMessage(messages.label)}
      onClick={() => {}}
    />
  );
};

export default OrganizationSidebarNav;
