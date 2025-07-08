import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { keyPressFind } from '../service/keyboardShortcuts/shortcutValidator';

import {
  ExpenseHeading,
  ExpenseTitle,
  StyledDiv,
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';

import { ProjectEntity, ProjectStatus } from '../entities/ProjectEntity';
import { updateProjectStatus } from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { EditSVG } from '../svgs/ClientSvgs.svs';
import { capitalizeFirstLetter } from '../utils/stringUtils';

interface Props {
  projectList: ProjectEntity[];
  updateProjectList: () => void;
  isLoading: boolean;
  onEditProject: (project: ProjectEntity) => void;
}

const ProjectList = ({ projectList, updateProjectList, isLoading }: Props) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [projectLists, setProjectList] = useState<ProjectEntity[]>([]);
  const [statusUpdateLoading, setStatusUpdateLoading] = useState(false);
  const [editableProjectId, setEditableProjectId] = useState<string | null>(
    null
  );

  useEffect(() => {
    setProjectList(projectList);
  }, [projectList]);

  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
    updateProjectList();
  };

  useEffect(() => {
    keyPressFind(searchInputRef);
  }, []);

  const handleProjectClick = (projectId: string, clientId: string) => {
    navigate(`/projects/project-management/${projectId}/${clientId}`);
  };

  const handleEditClick = (project: ProjectEntity) => {
    setEditableProjectId(project.projectId);
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All Projects')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && projectLists.length === 0 ? (
            <ZeroEntriesFound
              heading="No Projects Found"
              message="You Don't Have Any Projects"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th style={{ textAlign: 'left' }}>{t('Project ID')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Project Name')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Client Name')}</th>
                  <th style={{ textAlign: 'left' }}>
                    {t('Project Manager(s)')}
                  </th>
                  <th style={{ textAlign: 'left' }}>{t('Status')}</th>
                  <th style={{ textAlign: 'left' }}>{t('ACTION')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading
                  ? [...Array(6).keys()].map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(4).keys()].map((cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))
                  : projectLists?.map((project, index) => (
                      <TableBodyRow key={index}>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project.projectId ?? '',
                              project.clientId ?? ''
                            )
                          }
                        >
                          {project.projectId ?? '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project.projectId ?? '',
                              project.clientId ?? ''
                            )
                          }
                        >
                          {project.name
                            ? capitalizeFirstLetter(project.name)
                            : '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project.projectId ?? '',
                              project.clientId ?? ''
                            )
                          }
                        >
                          {project.clientName
                            ? capitalizeFirstLetter(project.clientName)
                            : '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project.projectId ?? '',
                              project.clientId ?? ''
                            )
                          }
                        >
                          {project.projectManagerNames &&
                            project.projectManagerNames[0]}
                        </td>

                        <td>
                          <StatusDropdown
                            value={project.projectStatus ?? 'NOT_STARTED'}
                            disabled={editableProjectId !== project.projectId}
                            onChange={async (newStatus) => {
                              try {
                                setStatusUpdateLoading(true);
                                await updateProjectStatus(
                                  project.projectId,
                                  newStatus as ProjectStatus
                                );
                                setProjectList((prevList) =>
                                  prevList.map((p) =>
                                    p.projectId === project.projectId
                                      ? {
                                          ...p,
                                          status: newStatus as ProjectStatus,
                                        }
                                      : p
                                  )
                                );
                                handleShowSuccessMessage();
                                setEditableProjectId(null);
                              } catch (error) {
                                throw new Error(
                                  'Failed to update status:' + error
                                );
                              } finally {
                                setStatusUpdateLoading(false);
                              }
                            }}
                          />
                        </td>
                        <td>
                          <EditSVG onClick={() => handleEditClick(project)} />
                        </td>
                      </TableBodyRow>
                    ))}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_PROJECT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_UPDATED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default ProjectList;
