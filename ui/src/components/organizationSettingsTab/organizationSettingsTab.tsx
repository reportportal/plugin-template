import { ExtensionPropsContext } from 'hooks/useExtensionProps';
import { defineMessages, useIntl } from 'react-intl';
import type { ExtensionProps } from 'types/extensionProps';

const messages = defineMessages({
  title: {
    id: 'PluginTemplate.orgSettings.title',
    defaultMessage: 'Template plugin — organization settings',
  },
  lead: {
    id: 'PluginTemplate.orgSettings.lead',
    defaultMessage: 'Tab id orgSettingsTemplate — org sidebar must use the same settingsTab.',
  },
  body: {
    id: 'PluginTemplate.orgSettings.body',
    defaultMessage: 'Replace with organization-wide UI.',
  },
});

const OrganizationSettingsTabContent = () => {
  const { formatMessage } = useIntl();

  return (
    <section>
      <h1>{formatMessage(messages.title)}</h1>
      <p>{formatMessage(messages.lead)}</p>
      <p>{formatMessage(messages.body)}</p>
    </section>
  );
};

const OrganizationSettingsTab = (props: ExtensionProps) => (
  <ExtensionPropsContext.Provider value={props}>
    <OrganizationSettingsTabContent />
  </ExtensionPropsContext.Provider>
);

export default OrganizationSettingsTab;
