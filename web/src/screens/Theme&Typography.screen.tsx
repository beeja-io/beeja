import { useEffect, useState } from "react";

import { DarkThemeBannerSVG } from "../svgs/CommonSvgs.svs";

import { LightThemeBannerSVG } from "../svgs/CommonSvgs.svs";

import { ActiveIconSVG } from "../svgs/CommonSvgs.svs";
import { useUser } from "../context/UserContext";
import {
  getOrganizationById,
  updateOrganizationById,
} from "../service/axiosInstance";
import axios, { AxiosError } from "axios";
import { IOrganization, IPreferences } from "../entities/OrganizationEntity";
import { toast } from "sonner";
import {
  ThemeSection,
  SectionTitle,
  SectionDivider,
  ThemeOptions,
  ThemeOption,
  ThemePreview,
  ThemeImage,
  ThemeDetails,
  ThemeLabel,
  ActiveIndicator,
  InActiveIndicator,
  TypographyContainer,
  Wrapper,
  WrapperContainer,
  FontName,
  WrapperInnerContainer,
  OptionElement,
  FontStyleWrapper,
  WrapperFontInnerContainer,
  ThemeTypographyHead,
} from "../styles/ThemeTypography.style";
import { usePreferences } from "../context/PreferencesContext";
import { BorderDivLine } from "../styles/MyProfile.style";
import { ProfileHeading } from "../styles/OrganizationSettingsStyles.style";
import { hasFeature } from "../utils/featureCheck";
import { capitalizeFirstLetter } from "../utils/stringUtils";
import { useFeatureToggles } from "../context/FeatureToggleContext";
import { EFeatureToggles } from "../entities/FeatureToggle";
import { useTranslation } from "react-i18next";

interface ThemeOption {
  label: string;
  icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>;
}

const themeOptions: ThemeOption[] = [
  {
    label: "Light",
    icon: LightThemeBannerSVG,
  },
  {
    label: "Dark",
    icon: DarkThemeBannerSVG,
  },
];

interface FontOption {
  name: string;
}

const fontOptions: FontOption[] = [
  {
    name: "NUNITO",
  },
  {
    name: "INTER",
  },
  {
    name: "MANROPE",
  },
  {
    name: "ROBOTO",
  },
];

interface FontSizeOption {
  label: string;
  size: string;
}

const fontSizeOptions: FontSizeOption[] = [
  {
    label: "Small",
    size: "14px",
  },
  {
    label: "Medium",
    size: "18px",
  },
  {
    label: "Large",
    size: "20px",
  },
];

function ThemesAndTypography() {
  const { user } = useUser();
  const { t } = useTranslation();
  const { featureToggles } = useFeatureToggles();
  const { preferences, setPreferences } = usePreferences();
  const [activeFontName, setActiveFontName] = useState<string>(
    fontOptions[0].name,
  );
  const [activeFontStyle, setActiveFontStyle] = useState(
    fontSizeOptions[0].label,
  );
  const [updatedOrganization, setUpdatedOrganization] = useState<IOrganization>(
    {} as IOrganization,
  );

  useEffect(() => {
    user &&
      setActiveFontName(
        capitalizeFirstLetter(
          user?.organizations.preferences.fontName.toUpperCase(),
        ),
      );
  }, [user]);

  const fetchOrganization = async () => {
    try {
      const response = await getOrganizationById(
        user ? user.organizations.id : "",
      );
      setUpdatedOrganization(response.data);
    } catch (error) {
      alert("error");
    }
  };

  const handleThemeClick = (label: string) => {
    const formdata = new FormData();
    const themeUpdated = {
      preferences: { theme: label } as IPreferences,
    } as IOrganization;
    if (updatedOrganization) {
      const updatedThemeJSON = JSON.stringify(themeUpdated);
      formdata.append("organizationFields", updatedThemeJSON);
    }

    toast.promise(
      updateOrganizationById(user ? user.organizations.id : "", formdata),
      {
        loading: "Updating organization preferences",
        success: (Response) => {
          fetchOrganization();
          if (Response && Response.data && Response.data.preferences) {
            setPreferences(Response.data.preferences);
          }
          setUpdatedOrganization({} as IOrganization);
          return `Successfully updated organization theme to ${capitalizeFirstLetter(
            label,
          )} Theme`;
        },
        error: (error) => {
          if (axios.isAxiosError(error)) {
            const axiosError = error as AxiosError;
            if (axiosError.code === "ERR_NETWORK") {
              return "Network Error, Please check connection";
            }
            if (axiosError.code === "ECONNABORTED") {
              return "Request timeout, Please try again";
            }
          }
          return "Request Unsuccessful, Please try again";
        },
      },
    );
  };

  const handleFontName = (fontName: string) => {
    const formdata = new FormData();
    const fontUpdated = {
      preferences: { fontName: fontName } as IPreferences,
    } as IOrganization;
    if (updatedOrganization) {
      const updatedFontJSON = JSON.stringify(fontUpdated);
      formdata.append("organizationFields", updatedFontJSON);
    }

    toast.promise(
      updateOrganizationById(user ? user.organizations.id : "", formdata),
      {
        loading: "Updating Preferences",
        success: (Response) => {
          fetchOrganization();
          if (Response && Response.data && Response.data.preferences) {
            setPreferences(Response.data.preferences);
          }
          setUpdatedOrganization({} as IOrganization);
          document.documentElement.style.setProperty(
            "--font-family-primary",
            Response.data.preferences.fontName,
          );
          setActiveFontName(capitalizeFirstLetter(fontName));
          return `Successfully Updated Font Style with ${capitalizeFirstLetter(
            fontName,
          )}`;
        },
        error: (error) => {
          if (axios.isAxiosError(error)) {
            const axiosError = error as AxiosError;
            if (axiosError.code === "ERR_NETWORK") {
              return "Network Error, Please check connection";
            }
            if (axiosError.code === "ECONNABORTED") {
              return "Request timeout, Please try again";
            }
          }
          return "Request Unsuccessful, Please try again";
        },
      },
    );
  };
  const handleFontSizeChange = (fontSize: string) => {
    setActiveFontStyle(fontSize);
    const selectedFontSize = fontSizeOptions.find(
      (option) => option.label === fontSize,
    )?.size;

    if (selectedFontSize) {
      document.documentElement.style.setProperty(
        "--font-size-primary",
        selectedFontSize,
      );
    }
  };
  return (
    <>
      <ThemeTypographyHead>
        <ProfileHeading>{t("THEMES_AND_TYPOGRAPHY")}</ProfileHeading>
      </ThemeTypographyHead>
      <BorderDivLine width="100%" />

      <ThemeSection>
        <SectionTitle>{t("THEME")}</SectionTitle>
        <SectionDivider />
        <ThemeOptions>
          {themeOptions.map((option) => (
            <ThemeOption
              key={option.label}
              onClick={() => handleThemeClick(option.label.toUpperCase())}
            >
              <ThemePreview>
                <ThemeImage>
                  <option.icon />
                </ThemeImage>
                <ThemeDetails>
                  <ThemeLabel>
                    {preferences?.theme === option.label.toUpperCase()
                      ? `${option.label + " Mode"} (Active)`
                      : option.label + " Mode"}
                  </ThemeLabel>
                  {preferences?.theme === option.label.toUpperCase() ? (
                    <ActiveIndicator>
                      <ActiveIconSVG />
                    </ActiveIndicator>
                  ) : (
                    <InActiveIndicator />
                  )}
                </ThemeDetails>
              </ThemePreview>
            </ThemeOption>
          ))}
        </ThemeOptions>
      </ThemeSection>

      {featureToggles &&
        (hasFeature(
          featureToggles.featureToggles,
          EFeatureToggles.ORGANIZATION_SETTINGS_FONT_NAME,
        ) ||
          hasFeature(
            featureToggles.featureToggles,
            EFeatureToggles.ORGANIZATION_SETTINGS_FONT_SIZE,
          )) && (
          <TypographyContainer>
            <SectionTitle>{t("TYPOGRAPHY")}</SectionTitle>
            <SectionDivider />
            {featureToggles &&
              hasFeature(
                featureToggles.featureToggles,
                EFeatureToggles.ORGANIZATION_SETTINGS_FONT_NAME,
              ) && (
                <Wrapper>
                  <WrapperContainer>
                    <FontName fontNameProps={{ fontFamily: "", fontSize: "" }}>
                      {t("FONT_STYLE")}
                    </FontName>
                  </WrapperContainer>
                  <WrapperInnerContainer>
                    {fontOptions.map((option) => (
                      <OptionElement
                        key={option.name}
                        onClick={() => handleFontName(option.name)}
                      >
                        {activeFontName ===
                        capitalizeFirstLetter(option.name) ? (
                          <ActiveIndicator style={{ marginRight: "20px" }}>
                            <ActiveIconSVG />
                          </ActiveIndicator>
                        ) : (
                          <InActiveIndicator
                            style={{
                              marginRight: "20px",
                              backgroundColor: "#ededed",
                            }}
                          />
                        )}
                        <FontName fontNameProps={{ fontFamily: option.name }}>
                          {capitalizeFirstLetter(option.name)}
                        </FontName>
                      </OptionElement>
                    ))}
                  </WrapperInnerContainer>
                </Wrapper>
              )}
            {featureToggles &&
              hasFeature(
                featureToggles.featureToggles,
                EFeatureToggles.ORGANIZATION_SETTINGS_FONT_SIZE,
              ) && (
                <FontStyleWrapper>
                  <WrapperContainer>
                    <FontName fontNameProps={{}}>{t("FONT_SIZE")}</FontName>
                  </WrapperContainer>
                  <WrapperFontInnerContainer>
                    {fontSizeOptions.map((option) => (
                      <OptionElement
                        style={{ marginBottom: "20px" }}
                        key={option.label}
                        onClick={() => handleFontSizeChange(option.label)}
                      >
                        {activeFontStyle === option.label ? (
                          <ActiveIndicator style={{ marginRight: "20px" }}>
                            <ActiveIconSVG />
                          </ActiveIndicator>
                        ) : (
                          <InActiveIndicator
                            style={{
                              marginRight: "20px",
                              backgroundColor: "#ededed",
                            }}
                          />
                        )}
                        <FontName fontNameProps={{ fontSize: option.size }}>
                          {option.label}
                        </FontName>
                      </OptionElement>
                    ))}
                  </WrapperFontInnerContainer>
                </FontStyleWrapper>
              )}
          </TypographyContainer>
        )}
    </>
  );
}
export default ThemesAndTypography;
