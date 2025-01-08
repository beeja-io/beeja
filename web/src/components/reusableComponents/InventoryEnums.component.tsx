interface InventoryOptions {
  device: string[];
  availability: string[];
  provider: string[];
  os: string[];
}
/* eslint-disable no-unused-vars */
export enum Device {
  LAPTOP = 'Laptop',
  MOBILE = 'MOBILE',
  DESKTOP = 'Desktop',
  PRINTER = 'Printer',
  TABLET = 'Tablet',
  ACCESSORIES = 'Accessories',
  MUSIC_SYSTEM = 'Music_System',
}
export enum Availability {
  YES = 'Yes',
  NO = 'No',
}
/* eslint-enable no-unused-vars */
export const inventoryOptions: InventoryOptions = {
  device: [
    Device.LAPTOP,
    Device.MOBILE,
    Device.DESKTOP,
    Device.PRINTER,
    Device.TABLET,
    Device.ACCESSORIES,
    Device.MUSIC_SYSTEM,
  ],
  availability: [Availability.YES, Availability.NO],
  provider: ['Dell', 'Apple', 'HP'],
  os: ['Windows', 'MacOS', 'Linux', 'Android', 'IOS'],
};
