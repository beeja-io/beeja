interface InventoryOptions {
  availability: string[];
}

export enum Availability {
  YES = 'Yes',
  NO = 'No',
}

export const inventoryOptions: InventoryOptions = {
  availability: [Availability.YES, Availability.NO],
};
