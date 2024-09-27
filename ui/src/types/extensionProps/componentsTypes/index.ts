import { IntegrationParameters, OnSubmit } from 'extensionProps/common';
import { FC } from 'react';
import { BaseFieldProps } from 'redux-form';

interface FieldProviderInterface extends BaseFieldProps {
  children: React.ReactNode;
}

interface FieldElementInterface extends FieldProviderInterface {
  label: string;
  description?: string;
  className?: string;
  childrenClassName?: string;
  withoutProvider?: boolean;
  dataAutomationId?: string;
  isRequired?: boolean;
  disabled?: boolean;
}

interface FieldErrorHintInterface {
  children?: React.ReactNode;
  error?: string;
  active?: boolean;
  staticHint?: boolean;
  widthContent?: boolean;
  darkView?: boolean;
  provideHint?: boolean;
  touched?: boolean;
  dataAutomationId?: string;
  hintType?: 'bottom' | 'top' | 'top-right' | 'bottom-left';
}

interface FieldTextInterface {
  maxLength?: number;
  defaultWidth?: boolean;
}

interface BtsAuthFieldsInfo {
  fieldsConfig: { value: string; message: string }[];
}

interface IntegrationSettings {
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
  onUpdate: OnSubmit;
  editAuthConfig: {
    content: JSX.Element;
    onClick: (testConnection: () => void) => void;
  };
  isGlobal: boolean;
  formFieldsComponent: BtsPropertiesForIssueForm;
  formKey: string;
  isEmptyConfiguration: boolean;
}

export interface IntegrationFormFieldsComponentsInterface {
  FieldElement: FC<FieldElementInterface>;
  FieldErrorHint: FC<FieldErrorHintInterface>;
  FieldText: FC<FieldTextInterface>;
  FieldTextFlex: FC;
}

type BtsPropertiesForIssueForm = FC<FieldProviderInterface>;

export interface IntegrationSettingsComponentsInterface {
  IntegrationSettings: FC<IntegrationSettings>;
  BtsAuthFieldsInfo: FC<BtsAuthFieldsInfo>;
  BtsPropertiesForIssueForm: BtsPropertiesForIssueForm;
}
