import { ActionsInterface } from 'extensionProps/actions';
import {
  IntegrationFormFieldsComponentsInterface,
  IntegrationSettingsComponentsInterface,
} from 'extensionProps/components';
import { UtilsInterface } from 'extensionProps/utils';
import { ValidatorsInterface } from 'extensionProps/validators';

export interface Metadata {
  [key: string]: any;
}

export interface IntegrationParameters {
  integrationName: string;
  project: string;
  url: string;
  defectFormFields?: [];
}

export interface IntegrationFormFieldsInterface {
  initialize: (initialData?: IntegrationParameters) => {};
  disabled: boolean;
  lineAlign: 'left' | 'center' | 'start' | 'end' | 'right' | 'justify';
  initialData: IntegrationParameters;
  updateMetaData: (metadata: { [key: string]: any }) => void;
  components: IntegrationFormFieldsComponentsInterface;
  validators: ValidatorsInterface;
  constants: { SECRET_FIELDS_KEY: 'SECRET_FIELDS_KEY'; BTS_FIELDS_FORM: 'BTS_FIELDS_FORM' };
}

export interface IntegrationSettingsInterface {
  data: {
    creator: string;
    enabled: boolean;
    id: number;
    integrationParameters: IntegrationParameters;
    integrationType: {
      type: number;
      name: string;
      enabled: boolean;
      creationDate: number;
      groupType: string;
      details: {
        allowedCommands: string[];
        binaryData: { icon: string; main: string; metadata: string };
        commonCommands: string[];
        description: string;
        documentationLink: string;
        id: string;
        metadata: { embedded: boolean; multiple: boolean };
        name: string;
        resources: string;
        version: string;
      };
    };
    name: string;
  };
  goToPreviousPage: () => void;
  onUpdate: (data: object, cb: () => void, metaData: object) => {};
  isGlobal: boolean;
  actions: ActionsInterface;
  components: IntegrationSettingsComponentsInterface;
  utils: UtilsInterface;
  constants: { SECRET_FIELDS_KEY: 'SECRET_FIELDS_KEY'; BTS_FIELDS_FORM: 'BTS_FIELDS_FORM' };
}

export interface OnSubmit {
  (
    integrationData: Metadata,
    callback: () => void,
    metaData: { fields: Metadata; checkedFieldsIds: { key: string; value: any }[] }
  ): void;
}
