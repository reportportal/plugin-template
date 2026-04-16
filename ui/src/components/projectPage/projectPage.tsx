import { defineMessages, useIntl } from 'react-intl';

import type { HostInjectedExtensionProps } from '../../types/hostExtensionProps';

interface ProjectPageProps extends HostInjectedExtensionProps {}

const messages = defineMessages({
  title: {
    id: 'PluginTemplate.projectPage.title',
    defaultMessage: 'Template plugin — project page',
  },
  lead: {
    id: 'PluginTemplate.projectPage.lead',
    defaultMessage: 'Route segment "template" · linked from sidebar via PROJECT_PLUGIN_PAGE.',
  },
  body: {
    id: 'PluginTemplate.projectPage.body',
    defaultMessage: 'Replace with your project-level UI.',
  },
});

const ProjectPage = (
  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- props injected by host
  props: ProjectPageProps
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

export default ProjectPage;
