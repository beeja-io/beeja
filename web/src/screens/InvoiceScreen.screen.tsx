import { useState } from "react";

import { InvoiceManagementMainContainer,
        InvoiceInnerBigContainer,
        InvoiceInnersmallContainer,
      InvoiceManagementHeading } from "../styles/InvoiceManagementStyles.style";
import CenterModalMain from "../components/reusableComponents/CenterModalMain.component";
import { AddInvoiceForm } from "../components/directComponents/AddInvoiceForm.component";

import { BlueDotDividerSVG ,MessageIconSVG ,PhoneIconSVG ,ChevronRightSVG} from "../svgs/CommonSvgs.svs";
import { NotepadSVG ,CompanyIconSVG ,CompanyLogoSVG} from "../svgs/InvoiceSvgs.svg";

import { useUser } from "../context/UserContext";
import { hasPermission } from "../utils/permissionCheck";
import { CLIENT_MODULE } from "../constants/PermissionConstants";

export const InvoiceScreen = () => {

    const [isCreateModalOpen,setIsCreateModalOpen] = useState(false);

    const {user} = useUser();

    const  handleIsCreateModalOpen = () => {
           setIsCreateModalOpen(!isCreateModalOpen);
    }
    return (
    <>
      <InvoiceManagementHeading>
          <span>Contract Management</span>&nbsp;
              <ChevronRightSVG />&nbsp;
          <span className="highlight">Contract Details</span>
      </InvoiceManagementHeading>
      <InvoiceManagementMainContainer>
        
          <InvoiceInnerBigContainer>
              <div className="Project_Heading">
                <span className="projectName">Website Redesign Contract</span>
                <span>InProgress</span>
              </div>
              <div className="projectDetails">
                <span>ID:CON-001<span>
                    &nbsp; <BlueDotDividerSVG /> &nbsp;
                </span> Fixed Price Contract <span>
                    &nbsp; <BlueDotDividerSVG /> &nbsp; 
                </span> Billable <span>
                    &nbsp; <BlueDotDividerSVG /> &nbsp;
                </span> <span> $30000</span></span>
              </div>
              <div className="projectDetails">
                <div>
                    <span><NotepadSVG /></span><span> WebSite Redesign</span>
                </div>
                <div>
                <span><CompanyIconSVG /></span><span >Barron LLC.</span>
                </div>
              </div>
    
            {user && hasPermission(user,CLIENT_MODULE.GENERATE_INVOICE) &&
                <button className="button_element" onClick={handleIsCreateModalOpen}>Generate Invoice</button>
            }
          </InvoiceInnerBigContainer>
          <InvoiceInnersmallContainer>
              <span style={{fontSize:"13px"}}>Client Details</span>
              <div className="clientDetails">
                <CompanyLogoSVG />             
                <div className="align">
                  <span >ID:<span className="text"> TAC BE 12</span></span>
                  <span style={{fontWeight:"bold" , margin:"5px 0px"}}>Baron LLC.</span>
                  <span style={{fontSize:"12px"}}>HRMS Solution</span>
                </div>
              </div>
              <div>
                  <span><PhoneIconSVG /></span>
                  &nbsp;
                  <span><MessageIconSVG /></span>
              </div>
          </InvoiceInnersmallContainer>
        </InvoiceManagementMainContainer>

        {isCreateModalOpen && (
            <CenterModalMain
              heading="Generate Invoice"
              modalClose={handleIsCreateModalOpen}
              actualContentContainer={
                <AddInvoiceForm
                  handleClose={handleIsCreateModalOpen}
                />
              }
            />
         )}
    </>
    )
}