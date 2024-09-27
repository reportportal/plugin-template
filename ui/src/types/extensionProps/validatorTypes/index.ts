type ValidatorType = (value: string) => string | undefined;

export interface ValidatorsInterface {
  requiredField: ValidatorType;
  btsUrl: ValidatorType;
  btsProjectId: ValidatorType;
  btsIntegrationName: ValidatorType;
}
