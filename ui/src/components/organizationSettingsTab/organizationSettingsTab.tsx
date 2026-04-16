import { defineMessages, useIntl } from 'react-intl';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface OrganizationSettingsTabProps extends HostInjectedExtensionProps {}

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

const OrganizationSettingsTab = (
  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- props injected by host
  props: OrganizationSettingsTabProps
) => {
  const { formatMessage } = useIntl();

  return (
    <section>
      <h1>{formatMessage(messages.title)}</h1>
      <p>{formatMessage(messages.lead)}</p>
      <p>{formatMessage(messages.body)}</p>
    </section>
  );
};

export default OrganizationSettingsTab;
