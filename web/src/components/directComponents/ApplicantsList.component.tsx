import { IApplicant } from '../../entities/ApplicantEntity';
import { useUser } from '../../context/UserContext';
import { RECRUITMENT_MODULE } from '../../constants/PermissionConstants';
import {
  TableBodyRow,
  TableHead,
  TableList,
} from '../../styles/DocumentTabStyles.style';
import ZeroEntriesFound from '../reusableComponents/ZeroEntriesFound.compoment';
import { CalenderIcon, DeleteIcon } from '../../svgs/DocumentTabSvgs.svg';
import { formatDate } from '../../utils/dateFormatter';
import { ExpenseListSection } from '../../styles/ExpenseListStyles.style';
import { StatusIndicator } from '../../styles/LoanApplicationStyles.style';
import { removeUnderScore } from '../../utils/stringUtils';
import { BulkPayslipContainer } from '../../styles/BulkPayslipStyles.style';
import ApplicantListActions from '../actionsOfLists/ApplicantListActions';
import { EditIcon } from '../../svgs/ExpenseListSvgs.svg';
import { DownloadSVG } from '../../svgs/CommonSvgs.svs';
import { downloadReferralResume } from '../../service/axiosInstance';
import axios, { AxiosError } from 'axios';

type ApplicantsListProps = {
  allApplicants: IApplicant[];
  isLoading: boolean;
  isReferral: boolean;
};
const ApplicantsList = (props: ApplicantsListProps) => {
  const { user } = useUser();
  const Actions = [
    ...(user?.roles.some((role) =>
      role.permissions.some(
        (permission) =>
          permission === RECRUITMENT_MODULE.UPDATE_ENTIRE_APPLICANT
      )
    )
      ? [{ title: 'Edit', svg: <EditIcon /> }]
      : []),
    ...(user?.roles.some((role) =>
      role.permissions.some(
        (permission) =>
          permission === RECRUITMENT_MODULE.UPDATE_ENTIRE_APPLICANT
      )
    )
      ? [{ title: 'Delete', svg: <DeleteIcon /> }]
      : []),
  ];

  const handleDownloadResume = async (resumeId: string) => {
    try {
      const response = await downloadReferralResume(resumeId);

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${'aas'}.${'pdf'}`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response?.status === 403) {
          // FIXME remove hardcoded strings
          alert('No permissions');
        } else {
          alert("Server Down! We'll come back soon");
        }
      }
    }
  };
  return (
    <ExpenseListSection>
      {props.isLoading ? (
        <div className="mainDiv">
          <div className="Expense_Heading">
            <p className="expenseListTitle">List of Applicants</p>
          </div>
          <TableList>
            <TableHead>
              <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                <th>Name of Applicant</th>
                <th>Position</th>
                <th>Phone Number</th>
                <th>Email</th>
                <th>Status</th>
                <th>Requested Date</th>
                <th>Action</th>
              </tr>
            </TableHead>
            <tbody>
              <>
                {[...Array(6).keys()].map((rowIndex) => (
                  <TableBodyRow key={rowIndex}>
                    {[...Array(7).keys()].map((cellIndex) => (
                      <td key={cellIndex}>
                        <div className="skeleton skeleton-text">&nbsp;</div>
                      </td>
                    ))}
                  </TableBodyRow>
                ))}
              </>
            </tbody>
          </TableList>
        </div>
      ) : props.allApplicants.length > 0 ? (
        <div className="mainDiv">
          <div className="Expense_Heading">
            <p className="expenseListTitle">List of Applicants</p>
          </div>
          <TableList>
            <TableHead>
              <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                <th>Name of Applicant</th>
                <th>Position</th>
                <th>Phone Number</th>
                <th>Email</th>
                <th style={{ textAlign: 'center' }}>Status</th>
                <th>Requested Date</th>
                <th>Resume/CV</th>
                {!props.isReferral && <th>Action</th>}
              </tr>
            </TableHead>
            <tbody>
              <>
                {props.allApplicants &&
                  props.allApplicants.map((applicant, index) => (
                    <TableBodyRow key={index}>
                      <td>
                        {applicant.firstName} {applicant.lastName}
                      </td>
                      <td>{applicant.positionAppliedFor}</td>
                      <td>{applicant.phoneNumber}</td>
                      <td>{applicant.email}</td>
                      <td>
                        <StatusIndicator
                          status={applicant.status}
                          className="applicantStatus"
                        >
                          {removeUnderScore(applicant.status)}
                        </StatusIndicator>
                      </td>

                      <td>
                        <span
                          style={{
                            verticalAlign: 'middle',
                            marginRight: '6px',
                          }}
                        >
                          <CalenderIcon />
                        </span>

                        {formatDate(applicant.createdAt)}
                      </td>
                      <td>
                        <span
                          onClick={() =>
                            handleDownloadResume(applicant.resumeId)
                          }
                        >
                          <DownloadSVG />
                        </span>
                      </td>
                      {!props.isReferral &&
                      user?.roles.some((role) =>
                        role.permissions.some(
                          (permission) =>
                            permission === RECRUITMENT_MODULE.UPDATE_APPLICANT
                        )
                      ) ? (
                        <td>
                          <ApplicantListActions
                            options={Actions}
                            applicant={applicant}
                          />
                        </td>
                      ) : (
                        ''
                      )}
                    </TableBodyRow>
                  ))}
              </>
            </tbody>
          </TableList>
        </div>
      ) : (
        <BulkPayslipContainer>
          <ZeroEntriesFound
            heading="There's no Applicants history found"
            message="You have never involved in making hiring online!!"
          />
        </BulkPayslipContainer>
      )}
    </ExpenseListSection>
  );
};

export default ApplicantsList;
