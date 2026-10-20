import { createContext, useContext } from 'react';
import { type ExtensionProps } from 'types/extensionProps';

const defaultExtensionProps: ExtensionProps = {
  lib: {},
  components: {},
  constants: {},
  selectors: {},
  actions: {},
  utils: {},
  validators: {},
  icons: {},
  HOCs: {},
  componentLibrary: {},
  portalRootIds: {
    tooltipRoot: 'tooltip-root',
    modalRoot: 'modal-root',
    popoverRoot: 'popover-root',
    notificationRoot: 'notification-root',
    screenLockRoot: 'screen-lock-root',
  },
};

export const ExtensionPropsContext = createContext<ExtensionProps>(defaultExtensionProps);

export const useExtensionProps = (): ExtensionProps => useContext(ExtensionPropsContext);
