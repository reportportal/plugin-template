import { defineMessages, useIntl } from 'react-intl';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface ProjectSettingsTabProps extends HostInjectedExtensionProps {}

const messages = defineMessages({
  title: {
    id: 'PluginTemplate.projectSettings.title',
    defaultMessage: 'Template plugin — project settings',
  },
  lead: {
    id: 'PluginTemplate.projectSettings.lead',
    defaultMessage: 'Tab id projectSettingsTemplate (general is reserved by RP).',
  },
  body: {
    id: 'PluginTemplate.projectSettings.body',
    defaultMessage: 'Replace with per-project plugin settings.',
  },
});

const ProjectSettingsTab = (
  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- props injected by host
  props: ProjectSettingsTabProps
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

export default ProjectSettingsTab;
