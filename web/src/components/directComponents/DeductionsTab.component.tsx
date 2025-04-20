import { useUser } from "../../context/UserContext";
import { EmployeeEntity } from "../../entities/EmployeeEntity";
import {
  BorderDivLine,
  InlineInput,
  TabContentEditArea,
  TabContentInnerContainer,
  TabContentMainContainer,
  TabContentMainContainerHeading,
  TabContentTable,
  TabContentTableTd,
} from "../../styles/MyProfile.style";
import {
  EditWhitePenSVG,
  CheckBoxOnSVG,
  CrossMarkSVG,
} from "../../svgs/CommonSvgs.svs";
import { useTranslation } from "react-i18next";
import { SelectInput } from "../reusableComponents/MyProfileGeneralTabContent.component";
import { useEffect, useMemo, useState } from "react";
import {
  getHealthInsuranceDetails,
  postHealthInsuranceDetails,
  updateHealthInsuranceDetails,
} from "../../service/axiosInstance";
import SpinAnimation from "../loaders/SprinAnimation.loader";
import { capitalizeFirstLetter } from "../../utils/stringUtils";
import { toast } from "sonner";
import axios, { AxiosError } from "axios";
import { ValidationText } from "../../styles/DocumentTabStyles.style";
import { HEALTH_INSURANCE_MODULE } from "../../constants/PermissionConstants";
import { HealthInsurance } from "../../entities/HealthInsuranceEntity";
import { hasPermission } from "../../utils/permissionCheck";

type DeductionTabProps = {
  heading: string;
  isEditModeOn: boolean;
  handleIsEditModeOn: () => void;
  employee: EmployeeEntity;
};
const DeductionsTab = ({
  heading,
  employee,
  isEditModeOn,
  handleIsEditModeOn,
}: DeductionTabProps) => {
  const { user } = useUser();
  const { t } = useTranslation();

  const [healthInsurance, setHealthInsurance] =
    useState<HealthInsurance | null>();

  const fetchHealthInsurance = () => {
    return new Promise((resolve, reject) => {
      getHealthInsuranceDetails(employee.employee.employeeId)
        .then((response) => {
          const data = response.data;
          setHealthInsurance(data);
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  useEffect(() => {
    if (
      employee.account.employeeId === user?.employeeId ||
      (user &&
        hasPermission(user, HEALTH_INSURANCE_MODULE.CREATE_HEALTH_INSURANCE))
    ) {
      fetchHealthInsurance();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const healthInsuranceDetails = useMemo(
    () => [
      {
        label: "Gross Premium",
        value:
          healthInsurance && healthInsurance.grossPremium
            ? healthInsurance.grossPremium.toString()
            : "-",
      },
      {
        label: "Instalment Type",
        value:
          healthInsurance && healthInsurance.instalmentType
            ? healthInsurance.instalmentType
            : "-",
      },
      {
        label: "Instalment Amount",
        value:
          healthInsurance && healthInsurance.instalmentAmount
            ? healthInsurance.instalmentAmount.toString()
            : "-",
      },
      {
        label: "No. of Instalments",
        value:
          healthInsurance && healthInsurance.instalmentFrequency
            ? healthInsurance.instalmentFrequency.toString()
            : "-",
      },
    ],
    [healthInsurance],
  );

  const [formData, setFormData] = useState<{ [key: string]: string }>({});
  const [isResponseLoading, setISResponseLoading] = useState(false);

  const resetFormData = () => {
    const defaultFormData: { [key: string]: string } = {};
    healthInsuranceDetails.forEach(({ label, value }) => {
      defaultFormData[label] = value;
    });
    setFormData(defaultFormData);
  };

  const handleChange = (label: string, newValue: string): void => {
    let updatedFormData: { [key: string]: string } = {
      ...formData,
      [label]: newValue,
    };

    if (
      (label === "Gross Premium" || label === "Instalment Type") &&
      updatedFormData["Instalment Type"] != "-"
    ) {
      const grossAmount = parseInt(updatedFormData["Gross Premium"]);
      const instalmentType = updatedFormData["Instalment Type"];
      const noOfMonths =
        instalmentType === "MONTHLY" || instalmentType === "Monthly" ? 12 : 4;
      const installmentAmount = (grossAmount / noOfMonths).toFixed(2);
      updatedFormData = {
        ...updatedFormData,
        instalmentAmount: installmentAmount.toString(),
      };
    }
    setFormData(updatedFormData);
  };

  useEffect(() => {
    const defaultFormData: { [key: string]: string } = {};
    healthInsuranceDetails.forEach(({ label, value }) => {
      if (label !== "No. of Instalments") {
        defaultFormData[label] = value;
      }
    });
    defaultFormData["employeeId"] = employee.employee.employeeId;
    setFormData(defaultFormData);
  }, [healthInsuranceDetails, employee.employee.employeeId]);

  const handleFinalDataToBeSentToBackend = (label: string): string => {
    switch (label) {
      case "Gross Premium":
        return "grossPremium";
      case "Instalment Amount":
        return "instalmentAmount";
      case "Instalment Type":
        return "instalmentType";
      case "instalmentAmount":
        return "instalmentAmount";
      case "employeeId":
        return "employeeId";
      default:
        return label.toLowerCase().replace(/\s/g, "");
    }
  };

  const handleSubmitHealthInsurance = () => {
    setISResponseLoading(true);
    const transformedData: { [key: string]: string } = {};
    Object.entries(formData).forEach(([label, value]) => {
      transformedData[handleFinalDataToBeSentToBackend(label)] = value;
    });
    if (
      transformedData["grossPremium"] === null ||
      transformedData["grossPremium"] === undefined ||
      transformedData["grossPremium"] === "-" ||
      transformedData["grossPremium"] === ""
    ) {
      toast.error("Error, Enter Gross Premium");
      setISResponseLoading(false);
      return;
    }
    if (
      transformedData["instalmentType"] === "Select" ||
      transformedData["instalmentType"] === undefined ||
      transformedData["instalmentType"] === "-" ||
      transformedData["instalmentType"] === ""
    ) {
      toast.error("Error, Select Instalment Type");
      setISResponseLoading(false);
      return;
    }
    transformedData["instalmentType"] =
      transformedData["instalmentType"].toUpperCase();
    const hasGrossAmountMinus = healthInsuranceDetails.some(
      (healthInsuranceDetails) =>
        healthInsuranceDetails.label === "Instalment Amount" &&
        healthInsuranceDetails.value === "-",
    );
    if (hasGrossAmountMinus) {
      toast.promise(postHealthInsuranceDetails(transformedData), {
        loading: "Adding health Insurance...",
        closeButton: true,
        success: () => {
          setISResponseLoading(false);
          fetchHealthInsurance();
          handleIsEditModeOn();
          setISResponseLoading(false);
          return "The Health Insurance details has been Submitted successfully";
        },
        error: (error) => {
          setISResponseLoading(false);
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
      });
    } else {
      toast.promise(
        updateHealthInsuranceDetails(
          employee.employee.employeeId,
          transformedData,
        ),
        {
          loading: `Updating health insurance of ${employee.employee.employeeId}...`,
          closeButton: true,
          success: () => {
            fetchHealthInsurance();
            handleIsEditModeOn();
            setISResponseLoading(false);
            setFormData((prev) => ({
              ...prev,
              "Instalment Type": transformedData["instalmentType"],
              "Gross Premium": transformedData["grossPremium"],
            }));
            return "The Health Insurance details has been Updated successfully";
          },
          error: (error) => {
            setISResponseLoading(false);
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
    }
  };

  return (
    <TabContentMainContainer>
      <TabContentMainContainerHeading>
        <h4>{heading}</h4>
        {user &&
        hasPermission(user, HEALTH_INSURANCE_MODULE.CREATE_HEALTH_INSURANCE) &&
        user.employeeId != employee.account.employeeId ? (
          <TabContentEditArea>
            {!isEditModeOn ? (
              <span
                title={`Edit ${employee.account.firstName}'s Deductions`}
                onClick={() => {
                  handleIsEditModeOn();
                }}
              >
                <EditWhitePenSVG />
              </span>
            ) : (
              <span>
                <span
                  title="Save Changes"
                  onClick={() => {
                    handleSubmitHealthInsurance();
                    resetFormData();
                  }}
                >
                  <CheckBoxOnSVG />
                </span>
                <span
                  title="Discard Changes"
                  onClick={() => {
                    handleIsEditModeOn();
                    resetFormData();
                  }}
                >
                  <CrossMarkSVG />
                </span>
              </span>
            )}
          </TabContentEditArea>
        ) : (
          ""
        )}
      </TabContentMainContainerHeading>
      <BorderDivLine width="100%" />
      <TabContentInnerContainer>
        <div>
          <TabContentTable>
            {healthInsuranceDetails.map(
              ({ label, value }) =>
                !(
                  user &&
                  hasPermission(
                    user,
                    HEALTH_INSURANCE_MODULE.CREATE_HEALTH_INSURANCE,
                  ) &&
                  label === "No. of Instalments"
                ) && (
                  <tr key={label}>
                    <TabContentTableTd>
                      {t(label)}{" "}
                      {isEditModeOn &&
                        label != "Instalment Amount" &&
                        label != "No. of Instalments" && (
                          <ValidationText className="star">*</ValidationText>
                        )}
                    </TabContentTableTd>
                    {isEditModeOn &&
                    label != "Instalment Amount" &&
                    label != "No. of Instalments" ? (
                      <TabContentTableTd>
                        {label === "Instalment Type" ? (
                          <SelectInput
                            label={label}
                            value={formData[value]}
                            options={["Monthly", "Quarterly"]}
                            onChange={(label, selectedValue) =>
                              handleChange(label, selectedValue)
                            }
                            selected={
                              formData["Instalment Type"] == "MONTHLY"
                                ? "Monthly"
                                : formData["Instalment Type"] == "QUARTERLY"
                                  ? "Quarterly"
                                  : ""
                            }
                          />
                        ) : (
                          <InlineInput
                            type="text"
                            placeholder={"Enter Amount"}
                            value={formData[label] || ""}
                            onChange={(e) => {
                              const inputValue = e.target.value;
                              const labelsWhichAllowOnlyNumbers = [
                                "Gross Premium",
                              ];
                              if (labelsWhichAllowOnlyNumbers.includes(label)) {
                                const numericInputValue = inputValue
                                  .replace(/[^0-9]/g, "")
                                  .slice(0, 10);
                                handleChange(label, numericInputValue);
                              } else {
                                handleChange(label, inputValue);
                              }
                            }}
                          />
                        )}
                      </TabContentTableTd>
                    ) : label === "Instalment Amount" ? (
                      <TabContentTableTd>
                        {!isNaN(parseFloat(formData["instalmentAmount"]))
                          ? formData["instalmentAmount"]
                          : value}
                      </TabContentTableTd>
                    ) : (
                      <TabContentTableTd>
                        {t(capitalizeFirstLetter(value))}
                      </TabContentTableTd>
                    )}
                  </tr>
                ),
            )}
          </TabContentTable>
        </div>
      </TabContentInnerContainer>
      {isResponseLoading && <SpinAnimation />}
    </TabContentMainContainer>
  );
};

export default DeductionsTab;
