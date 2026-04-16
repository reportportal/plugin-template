import { ExtensionPropsContext } from 'hooks/useExtensionProps';
import { defineMessages, useIntl } from 'react-intl';
import type { ExtensionProps } from 'types/extensionProps';

const messages = defineMessages({
  title: {
    id: 'PluginTemplate.instanceAdmin.title',
    defaultMessage: 'Template plugin — instance administration',
  },
  lead: {
    id: 'PluginTemplate.instanceAdmin.lead',
    defaultMessage: 'Instance admin · route segment "template" · MF scope/plugin name: plugin_name',
  },
  body: {
    id: 'PluginTemplate.instanceAdmin.body',
    defaultMessage: 'Replace with your instance-level UI.',
  },
});

const InstanceAdminPageContent = () => {
  const { formatMessage } = useIntl();

  return (
    <section>
      <h1>{formatMessage(messages.title)}</h1>
      <p>{formatMessage(messages.lead)}</p>
      <p>{formatMessage(messages.body)}</p>
    </section>
  );
};

const InstanceAdminPage = (props: ExtensionProps) => (
  <ExtensionPropsContext.Provider value={props}>
    <InstanceAdminPageContent />
  </ExtensionPropsContext.Provider>
);

export default InstanceAdminPage;
