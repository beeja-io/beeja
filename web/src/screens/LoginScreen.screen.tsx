import { useTranslation } from "react-i18next";
import { LoginUrl, OriginURL, ProdOriginURL } from "../constants/UrlConstants";
import { Button } from "../styles/CommonStyles.style";
import {
  BrandText,
  LoginContainer,
  LoginInnerContainer,
} from "../styles/LoginStyles.style";
import { GoogleIconSVG } from "../svgs/CommonSvgs.svs";
import {
  ERR_ACCOUNT_NOT_FOUND,
  ERR_ACCOUNT_NOT_FOUND_TEXT,
  LoginText1,
  LoginText2,
  SignInWithGoogleText,
} from "../constants/Constants";
import { LoginBgSVG } from "../svgs/LoginBgSVG.svg";
import { toast } from "sonner";
import { Text } from "web-kit-components";

const LoginScreen = () => {
  const { t } = useTranslation();
  const handleLogin = () => {
    /*If application is running
      in dev environment, then it will use dev base URL
      else, it will use localhost as origin
    */
    window.location.href =
      window.location.origin === OriginURL ||
      window.location.origin === ProdOriginURL
        ? `${window.location.origin}${LoginUrl}`
        : `${import.meta.env.VITE_API_BASE_URL}${LoginUrl}`;
  };

  const urlSearchParams = new URLSearchParams(window.location.search);
  const error = urlSearchParams.get("error");

  return (
    <LoginContainer>
      <span className="backgroundSVG">
        <LoginBgSVG />
      </span>
      <LoginInnerContainer>
        <BrandText>
          BEE<span>JA</span>
        </BrandText>
        <div className="subText">
          <Text className="boldText">{t(LoginText1)}</Text>
          <Text>{t(LoginText2)}</Text>
          <Button
            width="480px"
            onClick={() => {
              handleLogin();
              toast.loading("Signing in with Google", {
                description: "Please wait",
                closeButton: false,
              });
            }}
            className="LoginButton"
          >
            <GoogleIconSVG /> {t(SignInWithGoogleText)}
          </Button>
          {error && (
            <div className="loginErrorMessage">
              sdsdf
              {decodeURIComponent(error) === ERR_ACCOUNT_NOT_FOUND &&
                ERR_ACCOUNT_NOT_FOUND_TEXT}
            </div>
          )}
        </div>
      </LoginInnerContainer>
    </LoginContainer>
  );
};

export default LoginScreen;
