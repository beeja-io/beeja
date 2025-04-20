import styled from "styled-components";
export const SettingsMainContainer = styled.section`
  padding: 30px;
`;
export const SettingHeadSection = styled.div`
  width: fit-content;
  font-weight: 700;
  font-size: 24px;
  gap: 20px;
  span {
    font-weight: 500;
    height: 22px;
    font-size: 14px;
    color: #687588;
  }
`;
export const SectionsContainer = styled.div`
  display: flex;
  gap: 25px;
  margin-top: 20px;
  margin-left: 0;
  @media screen and (max-width: 1100px) {
    flex-direction: column;
  }
`;
export const NavbarSection = styled.section`
  display: flex;
  min-width: 300px;
  height: fit-content;
  padding: 24px;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 16px;
  @media screen and (max-width: 1100px) {
    padding: 5px 10px 10px 5px;
    box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.2);
  }
  position: sticky;
  top: 100px;
  z-index: 2;
`;
export const NavList = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  li {
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
    max-width: 252px;
    font-size: 14px;
    font-weight: 700;
    list-style: none;
    margin-bottom: 10px;
    padding: 16px 10px 16px 10px;
    cursor: pointer;
  }
  .dropdown-menu {
    margin-left: 40px;
    white-space: nowrap;
    position: relative;
  }
  .dropdown-menu:after {
    content: "";
    position: absolute;
    left: -20px;
    top: 0;
    bottom: 30px;
    width: 2px;
    background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 36px;
  }
  .dropdown-item {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .dropdown-item-thread::before {
    content: "";
    position: absolute;
    left: -20px;
    transform: translateY(-10%);
    width: 20px;
    height: 8px;
    border-left: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-bottom: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 0 0 0 100px;
  }
  li.active {
    background-color: ${(props) => props.theme.colors.blackColors.white4};
    border-radius: 10px;
    color: ${(props) => props.theme.colors.brandColors.primary};
  }
  svg {
    margin-right: 10px;
    align-self: flex-start;
  }
  @media screen and (max-width: 1100px) {
    overflow-x: scroll;
    ul {
      display: flex;
    }

    li {
      height: fit-content;
      justify-content: center;
      min-width: fit-content;
      width: 190px;
      margin-bottom: 0px;
    }
  }
  .dropdown-menu {
    margin-left: 40px;
    white-space: nowrap;
    position: relative;
  }
  .dropdown-menu:after {
    content: "";
    position: absolute;
    left: -20px;
    top: 0;
    bottom: 30px;
    width: 2px;
    background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 36px;
  }
  .dropdown-item {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .dropdown-item-thread::before {
    content: "";
    position: absolute;
    left: -20px;
    transform: translateY(-10%);
    width: 20px;
    height: 8px;
    border-left: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-bottom: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 0 0 0 100px;
  }
`;
export const CenterModalTypeExpenseInnerContainer = styled.div`
  width: 50%; /* Adjust modal width */
  max-width: 700px; /* Set a maximum width for responsiveness */
  background: #ffffff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  text-align: center; /* Center content inside the modal */
  position: relative; /* For positioning elements like the close button */
  .headingSection {
    margin-bottom: 20px; /* Add space below the heading */
  }
  .heading {
    font-size: 1.8rem; /* Larger font size for the heading */
    font-weight: 700; /* Bold heading */
    color: #333; /* Darker text color */
    margin: 0;
  }
  .buttonSection {
    margin-top: 25px; /* Add space between content and buttons */
  }
`;

// export const ButtonContainer = styled.div`
//    display: flex;
//   justify-content: center; /* Center the buttons horizontally */
//   gap: 15px; /* Add space between the buttons */
//   .close-button {
//     background-color: #F5F5F5; /* Light gray background */
//     color: #333; /* Dark text color */
//     border: 1px solid #ccc;
//     padding: 10px 20px;
//     border-radius: 8px;
//     cursor: pointer;
//     font-size: 1rem;
//     transition: background-color 0.3s ease;
//   }
//    .create {
//     border: 1px solid ${(props) => props.theme.colors.blackColors.black4};
//     background-color: #0056B3;
//     color: ${(props) =>
//       props.theme.colors.white ? props.theme.colors.white : '#FFFFFF'};
//     padding: 10px 20px;
//     border-radius: 8px;
//     cursor: pointer;
//     font-size: 1rem;
//     transition: background-color 0.3s ease;
//   }
// `;
export const SelectedTabSection = styled.section`
  width: 60%;
  left: 348px;
  height: fit-content;
  right: 16px;
  padding: 15px;
  gap: 24px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  @media screen and (max-width: 1100px) {
    width: 100%;
    margin-top: 20px;
    margin-left: 0;
    margin-right: auto;
    left: 0;
    right: auto;
  }
`;
export const InputContainer = styled.div<{ isValueInvalid?: boolean }>`
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  margin-top: 30px;

  label {
    flex: 1; /* Adjusts to share space */
    font-weight: bold;
    margin-right: 10px;
    font-size: 14px;
    color: #333;
  }

  input,
  textarea {
    flex: 2; /* Adjusts to share space */
    padding: 10px;
    font-size: 14px;
    border: 1px solid ${(props) => (props.isValueInvalid ? "red" : "#ccc")};
    border-radius: 4px;
    box-sizing: border-box;

    &:focus {
      border-color: #007bff;
      outline: none;
      box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
    }
  }

  textarea {
    min-height: 80px;
    resize: vertical;
  }
`;

export const ButtonContainer = styled.div`
  display: flex;
  justify-content: center; /* Center the buttons horizontally */
  gap: 15px; /* Add space between the buttons */

  .close-button {
    background-color: #f5f5f5; /* Light gray background */
    color: #333; /* Dark text color */
    border: 1px solid #ccc;
    padding: 10px 20px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.3s ease;
  }

  .create {
    border: 1px solid ${(props) => props.theme.colors.blackColors.black4};
    background-color: #0056b3;
    color: ${(props) =>
      props.theme.colors.white ? props.theme.colors.white : "#ffffff"};
    padding: 10px 20px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.3s ease;
  }
`;
