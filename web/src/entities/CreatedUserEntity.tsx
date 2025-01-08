import { User } from "./UserEntity";

export interface CreatedUserEntity {
  user: User;
  password: string;
}
