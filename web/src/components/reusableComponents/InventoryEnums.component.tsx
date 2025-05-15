interface InventoryOptions {
  availability: string[];
}

export enum Availability {
  YES = 'YES',
  NO = 'NO',
}

export const inventoryOptions: InventoryOptions = {
  availability: [Availability.YES, Availability.NO],
};
