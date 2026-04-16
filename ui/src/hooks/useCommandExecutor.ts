/** Same value as `pluginId` in root `gradle.properties` (first argument to `URLS.pluginsCommandsCommon`). */
export const PLUGIN_NAME = 'template';

type FetchFn = (url: string, options: Record<string, unknown>) => Promise<unknown>;

/**
 * Example for **organization / instance** UI: **`POST`** on **`URLS.pluginsCommandsCommon`**
 * Pass **`utils`** from host extension props.
 */
export interface UseCommandExecutorProps {
  utils: {
    fetch: FetchFn;
    URLS: {
      pluginsCommandsCommon: (pluginName: string, command: string) => string;
    };
  };
}

export const useCommandExecutor =
  ({ utils: { fetch, URLS } }: UseCommandExecutorProps) =>
  (command: string, data: Record<string, unknown> = {}, params: Record<string, unknown> = {}) =>
    fetch(URLS.pluginsCommandsCommon(PLUGIN_NAME, command), {
      method: 'POST',
      data,
      ...params,
    });
