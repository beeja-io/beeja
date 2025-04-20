import React, { createContext, useContext, useState, ReactNode } from 'react';
import { LoggedInUserEntity } from '../entities/LoggedInUserEntity';

type UserContextType = {
  user: LoggedInUserEntity | null;
  setUser: (user: LoggedInUserEntity | null) => void;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<LoggedInUserEntity | null>(null);

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);

  if (!context) {
    return { user: null, setUser: () => {}, isLoading: true };
  }

  return { ...context, isLoading: false };
};
