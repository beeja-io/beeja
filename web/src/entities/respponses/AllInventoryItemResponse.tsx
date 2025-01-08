import { DeviceDetails } from '../InventoryEntity';
export interface AllInventoryResponse {
  inventory: DeviceDetails[];
  metadata: {
    totalSize: number;
  };
}
