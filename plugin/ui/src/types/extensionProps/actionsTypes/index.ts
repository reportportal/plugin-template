export interface ActionsInterface {
  showModalAction: (data: ShowModalActionDataInterface) => ShowModalActionInterface;
  hideModalAction: () => HideModalActionInterface;
}

interface ShowModalActionDataInterface {
  id?: number | string;
  data?: unknown;
  component?: unknown;
}

interface ShowModalActionInterface {
  type: 'showModal';
  payload: {
    activeModal: ShowModalActionDataInterface;
  };
}

interface HideModalActionInterface {
  type: 'hideModal';
}
