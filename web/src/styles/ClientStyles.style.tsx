import styled from "styled-components";

export const FormContainer = styled.div`
  background-color: white;
  border-radius: 10px;
  border: 1px solid #f1f2f4;
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 0;
  margin-bottom: 20px;
`;

export const StepLabel = styled.div<{ isActive: boolean }>`
  flex: 1;
  text-align: left;
  font-family: "Nunito";
  padding: 10px;
  font-weight: ${(props) => (props.isActive ? "bold" : "normal")};
  color: ${(props) => (props.isActive ? "#005792" : "#888")};
`;

export const FormInputsContainer = styled.div`
  display: flex;
  gap: 20px;
  width: 850px;
  margin-left: 80px;
`;

export const LogoLabel = styled.div`
  font-family: Nunito;
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  width: 850px;
  margin-left: 80px;
`;

export const LogoUploadContainer = styled.div`
  box-sizing: border-box;
  height: 54px;
  border: 1px dashed #d5d5d5;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 16px;
  width: 850px;
  margin-left: 80px;
  cursor: pointer;
`;
export const SubHeadingDivTwo = styled.div`
font-family: "Nunito";
  font-size: 14px;
  font-weight: 600;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: #818181;
  width: 90px;
`;
export const UploadText = styled.span`
  font-family: "Nunito", sans-serif;
  font-weight: 400;
  font-size: 14px;
  color: #a0aec0;
  padding-left: 10px;
`;

export const BrowseText = styled.span`
  font-weight: 700;
  text-decoration: underline;
  color: #005792;
`;

export const FormButtons = styled.div`
  display: flex;
  gap: 10px;
`;

export const FormInputs = styled.div`
 display: flex;
  gap: 20px;
  margin-bottom: 100px;
  width: 850px;
  margin-left: 80px;
  margin-bottom:20px
`;
export const HeadingDiv = styled.div`
font-family: 'Nunito';
  width: 850px;
  margin-left: 80px;
`;

export const CheckBoxOuterContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  width: 850px;
  margin-left: 80px;
`;

export const StyledCheckbox = styled.input`
  width: 16px;
  height: 16px;
  color: rgba(0, 87, 146, 1);
  align-items: center;
  display: flex;
`;

export const LabelText = styled.div`
  align-items: center;
  height: 100%;
  font-family: 'Nunito';
`;

export const SubHeadingDiv = styled.div`
font-family: "Nunito";
  font-size: 14px;
  font-weight: 600;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: #818181;
  width: 90px;
`;

export const HeadingContainer = styled.div`
width: 850px;
  margin-left: 80px;
  font-family: "Nunito";
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const InfoRow = styled.div`
  width: 750px;
  padding-top: 10px;
  padding-left: 10px;
  display: flex;
`;

export const InfoText = styled.div`
  font-family: "Nunito";
  padding-left: 60px;
`;

export const InfoBlock = styled.div`
  width: 400px;
  padding-top: 20px;
  padding-left: 10px;
  padding-bottom: 20px;
`;
export const AddressBlock = styled.div`
  width: 400px;
  padding: 10px 0 10px 10px;
  display: flex;
`;
export const BasicOrganizationDetailsContainer = styled.div`
margin-bottom: 5px;
  margin-top: 5px;
  width: 850px;
  margin-left: 80px;
  background-color: rgba(248, 249, 251, 1);
`;

export const LeftSection = styled.div`
  display: flex;
  flex-direction: column;
  width: 750px;
`;

export const ClientInfo = styled.div`
display: flex;
flex-direction: column;
  width: 750px;
  height: 200px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;

export const TableContainer = styled.div`
  width: 700px;
  background-color: #ffffff;
  border-radius: 8px;
  margin-top: 10px;
  padding: 10px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;

export const RightSection = styled.div`
  width: 350px;
  /* height: 550px; */
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;
export const Container = styled.div`
  display: flex;
  width: 100%;
  gap: 20px;
`;

export const ClientInfoDiv = styled.div`
  font-family: Nunito;
  font-size: 14px;
  font-weight: 500;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: #818181;
  width: 70px;
`;

export const ClientTitle = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 26px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  width: 330px;
  height: 42px;
  color: #005792;
  margin-bottom: 20px;
`;

export const ProjectInfo = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  line-height: 160%;
  letter-spacing: 0.2px;
  color: #005792;
  padding-left: 10px;
  padding-right: 10px;
`;

export const RightSectionDiv = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  line-height: 160%;
  letter-spacing: 0.2px;
  width: 320px;
  height: 128px;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f1f2f4;
  align-items: flex-start;
  justify-content: flex-start;
  word-wrap: break-word;
`;

export const AddressDiv = styled.div`
  font-family: Nunito;
  font-weight: 400;
  font-size: 15px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  color: #818181;
  display: flex;
  flex-direction: column;
  padding-top: 20px;

`;