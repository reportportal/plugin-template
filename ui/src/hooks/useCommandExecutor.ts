import type { UtilsInterface } from 'extensionProps/utils';

/** Same value as `pluginId` in root `gradle.properties` (first argument to `URLS.pluginsCommandsCommon`). */
export const PLUGIN_NAME = 'template';

/**
 * Example for **organization / instance** UI: **`POST`** on **`URLS.pluginsCommandsCommon`**
 * Pass **`utils`** from host extension props.
 */
export type UseCommandExecutorProps = {
  utils: Required<Pick<UtilsInterface, 'fetch' | 'URLS'>>;
};

export const useCommandExecutor =
  ({ utils: { fetch, URLS } }: UseCommandExecutorProps) =>
  (command: string, data: Record<string, unknown> = {}, params: Record<string, unknown> = {}) =>
    fetch(URLS.pluginsCommandsCommon(PLUGIN_NAME, command), {
      method: 'POST',
      data,
      ...params,
    });
