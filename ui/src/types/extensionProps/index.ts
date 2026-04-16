import type { ActionsInterface } from 'extensionProps/actions';
import type { UtilsInterface } from 'extensionProps/utils';
import type { ValidatorsInterface } from 'extensionProps/validators';

/** Shape of the object spread onto every remote extension root by ReportPortal `service-ui` (see `createImportProps`). */
export type HostSelector = (state: unknown) => unknown;

export interface ExtensionProps {
  lib?: Record<string, unknown>;
  components: Record<string, unknown>;
  constants?: Record<string, unknown>;
  selectors?: Record<string, HostSelector>;
  actions?: Partial<ActionsInterface> & Record<string, unknown>;
  utils?: Partial<UtilsInterface> & Record<string, unknown>;
  validators?: Partial<ValidatorsInterface> & Record<string, unknown>;
  icons?: Record<string, unknown>;
  HOCs?: Record<string, unknown>;
  portalRootIds?: Record<string, string>;
  componentLibrary?: Record<string, unknown>;
}
