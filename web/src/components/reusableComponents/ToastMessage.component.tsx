import { useEffect } from "react";
import { ToastMainContainer } from "../../styles/ToastMessage.style";
import {
  ErrorRedMark,
  SuccessGreenTick,
  ToastCloseCross,
} from "../../svgs/CommonSvgs.svs";
import { useTranslation } from "react-i18next";

type ToastMessageProps = {
  messageType: "success" | "warn" | "error";
  messageHeading: string;
  messageBody: string;
  handleClose: () => void;
};
const ToastMessage = (props: ToastMessageProps) => {
  const { t } = useTranslation();
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      props.handleClose();
    }, 5000);

    return () => clearTimeout(timeoutId);
  }, [props]);
  return (
    <ToastMainContainer>
      <span className={`${props.messageType} toastStatusIcon`}>
        {props.messageType === "success" ? (
          <SuccessGreenTick />
        ) : (
          <ErrorRedMark />
        )}
      </span>
      <span className="toastMessageContent">
        <span className={`${props.messageType} toastMessageHead`}>
          {t(props.messageHeading)}
        </span>
        <span className="toastMessageBody">{t(props.messageBody)}</span>
      </span>
      <span className="toastCloseIcon" onClick={props.handleClose}>
        <span className="icon">
          <ToastCloseCross />
        </span>
      </span>
    </ToastMainContainer>
  );
};

export default ToastMessage;
