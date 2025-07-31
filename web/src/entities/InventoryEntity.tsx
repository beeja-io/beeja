import { Availability } from '../components/reusableComponents/InventoryEnums.component';

export interface DeviceDetails {
  id: string;
  device: string;
  provider: string;
  model: string;
  type: string;
  os?: string | null;
  specifications?: string;
  ram?: string | null;
  availability: Availability;
  productId: string;
  price: number;
  dateOfPurchase: Date;
  comments?: string;
  accessoryType?: string;
  deviceNumber: string;
}

export interface IUpdateDeviceDetails {
  device: string | null;
  provider: string | null;
  model: string | null;
  type: string | null;
  os?: string | null;
  specifications?: string | null;
  ram?: string | null;
  availability: Availability | null | string;
  productId: string | null;
  price: number | null | string;
  dateOfPurchase: Date | null;
  comments?: string | null;
  accessoryType?: string | null;
  deviceNumber: string | null;
}
