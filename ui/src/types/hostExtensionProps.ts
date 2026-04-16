/** Shape of the object spread onto every remote extension root by ReportPortal `service-ui` (see `createImportProps`). */
export type HostSelector = (state: unknown) => unknown;

export interface HostInjectedExtensionProps {
  lib?: Record<string, unknown>;
  components?: Record<string, unknown>;
  constants?: Record<string, unknown>;
  selectors?: Record<string, HostSelector>;
  actions?: Record<string, unknown>;
  utils?: Record<string, unknown>;
  validators?: Record<string, unknown>;
  icons?: Record<string, unknown>;
  HOCs?: Record<string, unknown>;
  portalRootIds?: Record<string, string>;
  componentLibrary?: Record<string, unknown>;
}
