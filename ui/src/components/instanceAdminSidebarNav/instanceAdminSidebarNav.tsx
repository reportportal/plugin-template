import instanceNavIcon from 'icons/instance-sidebar.svg';
import { defineMessages, useIntl } from 'react-intl';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface InstanceAdminSidebarNavProps
  extends Pick<HostInjectedExtensionProps, 'components' | 'constants'> {}

const messages = defineMessages({
  label: {
    id: 'PluginTemplate.instanceNav.label',
    defaultMessage: 'Template plugin — instance admin',
  },
});

/** Instance sidebar → admin page; `pluginPage` = `name` of the `adminPage` extension. */
const InstanceAdminSidebarNav = (props: InstanceAdminSidebarNavProps) => {
  const { formatMessage } = useIntl();

  const {
    components: { SidebarButton },
    constants: { PLUGIN_UI_EXTENSION_ADMIN_PAGE },
  } = props as any;

  const link = {
    type: PLUGIN_UI_EXTENSION_ADMIN_PAGE,
    payload: { pluginPage: 'template' },
  };

  return (
    <SidebarButton
      icon={instanceNavIcon}
      link={link}
      message={formatMessage(messages.label)}
      onClick={() => {}}
    />
  );
};

export default InstanceAdminSidebarNav;
