import { ExtensionPropsContext, useExtensionProps } from 'hooks/useExtensionProps';
import instanceNavIcon from 'icons/instance-sidebar.svg';
import type { ComponentType } from 'react';
import { defineMessages, useIntl } from 'react-intl';
import type { ExtensionProps } from 'types/extensionProps';

interface InstanceAdminSidebarNavProps extends ExtensionProps {
  components: { SidebarButton: ComponentType<any> };
  constants: { PLUGIN_UI_EXTENSION_ADMIN_PAGE: string };
}

const messages = defineMessages({
  label: {
    id: 'PluginTemplate.instanceNav.label',
    defaultMessage: 'Template plugin — instance admin',
  },
});

/** Instance sidebar → admin page; `pluginPage` = `name` of the `adminPage` extension. */
const InstanceAdminSidebarNavContent = () => {
  const { formatMessage } = useIntl();

  const {
    components: { SidebarButton },
    constants: { PLUGIN_UI_EXTENSION_ADMIN_PAGE },
  } = useExtensionProps() as InstanceAdminSidebarNavProps;

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

const InstanceAdminSidebarNav = (props: ExtensionProps) => (
  <ExtensionPropsContext.Provider value={props}>
    <InstanceAdminSidebarNavContent />
  </ExtensionPropsContext.Provider>
);

export default InstanceAdminSidebarNav;
