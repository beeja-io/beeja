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

import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ProjectEntity, ProjectStatus } from '../entities/ProjectEntity';
import { updateProjectStatus } from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { EditSVG } from '../svgs/ClientManagmentSvgs.svg';
import { capitalizeFirstLetter } from '../utils/stringUtils';

interface Props {
  projectList: ProjectEntity[];
  updateProjectList: () => void;
  isLoading: boolean;
  onEditProject: (project: ProjectEntity) => void;
}

const ProjectList = ({
  projectList,
  updateProjectList,
  isLoading,
  onEditProject,
}: Props) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [projectLists, setProjectList] = useState<ProjectEntity[]>([]);
  const [editLoadingProjectId, setEditLoadingProjectId] = useState<
    string | null
  >(null);

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
                  <th>{t('Project ID')}</th>
                  <th>{t('Project Name')}</th>
                  <th>{t('Client Name')}</th>
                  <th>{t('Project Manager(s)')}</th>
                  <th>{t('Status')}</th>
                  <th>{t('ACTION')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading
                  ? [...Array(6).keys()].map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(6).keys()].map((cellIndex) => (
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
                          {project.projectStatus ? (
                            <StatusDropdown
                              value={project.projectStatus}
                              onChange={(newStatus) => {
                                toast.promise(
                                  updateProjectStatus(
                                    project.projectId,
                                    newStatus as ProjectStatus
                                  ).then(() => {
                                    setProjectList((prevList) =>
                                      prevList.map((p) =>
                                        p.projectId === project.projectId
                                          ? {
                                              ...p,
                                              projectStatus:
                                                newStatus as ProjectStatus,
                                            }
                                          : p
                                      )
                                    );
                                  }),
                                  {
                                    loading: 'Updating project status...',
                                    success:
                                      'Project status updated successfully!',
                                    error: 'Failed to update project status',
                                  }
                                );
                              }}
                            />
                          ) : (
                            <span>-</span>
                          )}
                        </td>
                        <td>
                          {editLoadingProjectId === project.projectId ? (
                            <SpinAnimation />
                          ) : (
                            <EditSVG
                              onClick={async () => {
                                setEditLoadingProjectId(project.projectId);
                                try {
                                  await onEditProject(project);
                                } finally {
                                  setEditLoadingProjectId(null);
                                }
                              }}
                            />
                          )}
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
          messageBody="The project Added Sucessfully"
          messageHeading="SUCCESSFULLY_UPDATED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default ProjectList;
