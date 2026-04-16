export type PluginCommandFetchFn = (
  url: string,
  options: Record<string, unknown>
) => Promise<unknown>;

export interface UtilsInterface {
  getDefectFormFields: (fields: any, checkedFieldsIds: any, integrationData: any) => string;
  fetch?: PluginCommandFetchFn;
  URLS?: {
    pluginsCommandsCommon: (pluginName: string, command: string) => string;
  };
}
