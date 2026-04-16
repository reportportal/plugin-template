import { defineMessages, useIntl } from 'react-intl';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface InstanceAdminPageProps extends HostInjectedExtensionProps {}

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

const InstanceAdminPage = (
  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- props injected by host
  props: InstanceAdminPageProps
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

export default InstanceAdminPage;
