/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, ReactNode } from "react";

type ProfileImageContextType = {
  profileImageUrl: string | null;
  setProfileImageUrl: (url: string | null, isOwnProfile: boolean) => void;
};

export const ProfileImageContext = createContext<
  ProfileImageContextType | undefined
>(undefined);

export const ProfileImageProvider = ({ children }: { children: ReactNode }) => {
  const [profileImageUrl, setProfileImageUrlState] = useState<string | null>(
    () => {
      return localStorage.getItem("profileImageUrl") || null;
    },
  );

  const setProfileImageUrl = (url: string | null, isOwnProfile: boolean) => {
    setProfileImageUrlState(url);

    if (url && isOwnProfile) {
      localStorage.removeItem("profileImageUrl");
      localStorage.setItem("profileImageUrl", url);
    }
  };

  return (
    <ProfileImageContext.Provider
      value={{ profileImageUrl, setProfileImageUrl }}
    >
      {children}
    </ProfileImageContext.Provider>
  );
};

export const useProfileImage = () => {
  const context = useContext(ProfileImageContext);
  if (!context) {
    throw new Error(
      "useProfileImage must be used within a ProfileImageProvider",
    );
  }

  return context;
};
/* eslint-enable react-refresh/only-export-components */
