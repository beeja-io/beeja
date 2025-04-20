import {
  InputLabelContainer,
  TextInput,
} from "../../styles/DocumentTabStyles.style";
import { DeviceDetails } from "../../entities/InventoryEntity";
import { ExpenseAddFormMainContainer } from "../../styles/ExpenseManagementStyles.style";
import { Button } from "../../styles/CommonStyles.style";
import { useUser } from "../../context/UserContext";
import { INVENTORY_MODULE } from "../../constants/PermissionConstants";
import { removeUnderScore } from "../../utils/stringUtils";
import { formatDate } from "../../utils/dateFormatter";
import useKeyPress from "../../service/keyboardShortcuts/onKeyPress";
import { hasPermission } from "../../utils/permissionCheck";
import { useTranslation } from "react-i18next";
type PreviewInventoryFormProps = {
  formData: DeviceDetails;
  handleClose: () => void;
  handleEdit: () => void;
};

const PreviewInventoryForm = (props: PreviewInventoryFormProps) => {
  const { user } = useUser();

  const handleEditClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    props.handleEdit(); // Call the callback function provided by the parent component
  };
  const hasEditPermission =
    user && hasPermission(user, INVENTORY_MODULE.UPDATE_DEVICE);
  useKeyPress(27, () => {
    props.handleClose();
  });

  const { t } = useTranslation();
  return (
    <ExpenseAddFormMainContainer>
      <div className="formInputs">
        <div>
          <InputLabelContainer>
            <label>{t("DEVICE_NUMBER")}</label>
            <TextInput
              type="text"
              name="deviceNumber"
              className="largeInput grayText"
              value={props.formData.deviceNumber}
              readOnly
            />
          </InputLabelContainer>
          {props.formData.device === "Accessories" && (
            <InputLabelContainer>
              <label>{t("ACCESSORY_TYPE")}</label>
              <TextInput
                type="text"
                name="accessoryType"
                className="largeInput grayText"
                value={
                  props.formData.accessoryType
                    ? removeUnderScore(props.formData.accessoryType)
                    : ""
                }
                readOnly
              />
            </InputLabelContainer>
          )}
          <InputLabelContainer>
            <label>{t("TYPE")}</label>
            <TextInput
              type="text"
              name="type"
              className="largeInput grayText"
              value={removeUnderScore(props.formData.type)}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("SPECIFICATIONS")}</label>
            <TextInput
              type="text"
              name="specifications"
              className="largeInput grayText"
              value={props.formData.specifications}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("AVAILABILITY")}</label>
            <TextInput
              type="text"
              name="availability"
              className="largeInput grayText"
              value={removeUnderScore(props.formData.availability)}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("DATE_OF_PURCHASE")}</label>
            <TextInput
              type="text"
              name="dateOfPurchase"
              value={
                props.formData.dateOfPurchase
                  ? formatDate(
                      new Date(props.formData.dateOfPurchase).toDateString(),
                    )
                  : ""
              }
              className="largeInput grayText"
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("PRICE")}</label>
            <TextInput
              type="number"
              name="price"
              className="largeInput grayText"
              value={props.formData.price}
              readOnly
            />
          </InputLabelContainer>
          {props.formData.device !== "Accessories" && (
            <InputLabelContainer>
              <label>{t("COMMENTS")}</label>
              <TextInput
                type="text"
                name="comments"
                className="largeInput grayText"
                value={props.formData.comments}
                readOnly
              />
            </InputLabelContainer>
          )}
        </div>
        <div>
          <InputLabelContainer>
            <label>{t("DEVICE")}</label>
            <TextInput
              type="text"
              name="device"
              className="largeInput grayText"
              value={removeUnderScore(props.formData.device)}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("PROVIDER")}</label>
            <TextInput
              type="text"
              name="provider"
              className="largeInput grayText"
              value={removeUnderScore(props.formData.provider)}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("MODEL")}</label>
            <TextInput
              type="text"
              name="model"
              className="largeInput grayText"
              value={props.formData.model}
              readOnly
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>
              {t("OS")}
              {["DESKTOP", "MUSIC_SYSTEM", "PRINTER", "ACCESSORIES"].includes(
                props.formData.device,
              ) && null}
            </label>
            <TextInput
              type="text"
              name="os"
              className="largeInput grayText"
              value={props.formData.os ? props.formData.os : "-"}
              readOnly
              disabled={
                props.formData.device === "Accessories" ||
                props.formData.device.toUpperCase() === "MUSIC SYSTEM"
              }
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>
              {t("RAM")}
              {["DESKTOP", "MUSIC_SYSTEM", "PRINTER", "ACCESSORIES"].includes(
                props.formData.device,
              ) && null}
            </label>
            <TextInput
              type="text"
              name="ram"
              className="largeInput grayText"
              value={props.formData.ram ? props.formData.ram : "-"}
              readOnly
              disabled={props.formData.device === "Accessories"}
            />
          </InputLabelContainer>
          <InputLabelContainer>
            <label>{t("PRODUCT_ID_SERIAL_NUMBER")}</label>
            <TextInput
              type="text"
              name="productId"
              className="largeInput grayText"
              value={props.formData.productId}
              readOnly
            />
          </InputLabelContainer>
          {props.formData.device === "Accessories" && (
            <InputLabelContainer>
              <label>{t("COMMENTS")}</label>
              <TextInput
                type="text"
                name="comments"
                className="largeInput grayText"
                value={props.formData.comments}
                readOnly
              />
            </InputLabelContainer>
          )}
        </div>
      </div>
      <div className="formButtons">
        <Button onClick={props.handleClose} fontSize="16px" width="145px">
          {t("CANCEL")}
        </Button>
        {hasEditPermission && (
          <Button
            onClick={handleEditClick}
            fontSize="16px"
            width="145px"
            className="submit"
          >
            {t("EDIT")}
          </Button>
        )}
      </div>
    </ExpenseAddFormMainContainer>
  );
};

export default PreviewInventoryForm;
