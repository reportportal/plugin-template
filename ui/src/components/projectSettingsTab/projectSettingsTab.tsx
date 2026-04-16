import { ExtensionPropsContext } from 'hooks/useExtensionProps';
import { defineMessages, useIntl } from 'react-intl';
import type { ExtensionProps } from 'types/extensionProps';

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

const ProjectSettingsTabContent = () => {
  const { formatMessage } = useIntl();

  return (
    <section>
      <h1>{formatMessage(messages.title)}</h1>
      <p>{formatMessage(messages.lead)}</p>
      <p>{formatMessage(messages.body)}</p>
    </section>
  );
};

const ProjectSettingsTab = (props: ExtensionProps) => (
  <ExtensionPropsContext.Provider value={props}>
    <ProjectSettingsTabContent />
  </ExtensionPropsContext.Provider>
);

export default ProjectSettingsTab;
