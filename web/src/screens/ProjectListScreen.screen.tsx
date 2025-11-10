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
import Pagination from '../components/directComponents/Pagination.component';
import { PROJECT_MODULE } from '../constants/PermissionConstants';
import { hasPermission } from '../utils/permissionCheck';
import { useUser } from '../context/UserContext';
export type ProjectListProps = {
  projectList: ProjectEntity[];
  updateProjectList: () => void;
  isLoading: boolean;
  onEditProject: (project: ProjectEntity) => void;
  totalItems: number;
  currentPage: number;
  setCurrentPage: (page: number) => void;
  itemsPerPage: number;
  setItemsPerPage: (size: number) => void;
};

const ProjectList = ({
  projectList,
  updateProjectList,
  isLoading,
  onEditProject,
  totalItems,
  currentPage,
  setCurrentPage,
  itemsPerPage,
  setItemsPerPage,
}: ProjectListProps) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user } = useUser();

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

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const page = parseInt(searchParams.get('page') || '1', 10);
    const size = parseInt(
      searchParams.get('size') || itemsPerPage.toString(),
      10
    );

    setCurrentPage(page);
    setItemsPerPage(size);
  }, []);

  const handleProjectClick = (projectId: string, clientId: string) => {
    navigate(`/projects/project-management/${projectId}/${clientId}`);
  };
  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('page', newPage.toString());
    searchParams.set('size', itemsPerPage.toString());
    navigate({ search: searchParams.toString() });
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('page', '1');
    searchParams.set('size', newPageSize.toString());
    navigate({ search: searchParams.toString() });
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All_Projects')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && projectLists.length === 0 ? (
            <ZeroEntriesFound
              heading="No_Projects_Found"
              message="You_Don't_Have_Any_Projects"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Project_ID')}</th>
                  <th>{t('Project_Name')}</th>
                  <th>{t('Client_Name')}</th>
                  <th>{t('Project_Manager(s)')}</th>
                  <th>{t('STATUS')}</th>
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
                              project?.projectId ?? '',
                              project?.clientId ?? ''
                            )
                          }
                        >
                          {project?.projectId ?? '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project?.projectId ?? '',
                              project?.clientId ?? ''
                            )
                          }
                        >
                          {project?.name
                            ? project.name
                            : '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project?.projectId ?? '',
                              project?.clientId ?? ''
                            )
                          }
                        >
                          {project?.clientName
                            ? project?.clientName
                            : '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleProjectClick(
                              project?.projectId ?? '',
                              project?.clientId ?? ''
                            )
                          }
                        >
                          {Array.isArray(project?.projectManagerNames)
                            ? project.projectManagerNames.map((name, index) => (
                                <div key={index}>
                                  {name}
                                  {index <
                                    project.projectManagerNames.length - 1 &&
                                    ' ,'}
                                </div>
                              ))
                            : project?.projectManagerNames}
                        </td>
                        <td>
                          {project?.projectStatus ? (
                            <StatusDropdown
                              value={project?.projectStatus}
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
                                    updateProjectList();
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
                              disabled={
                                !(
                                  user &&
                                  hasPermission(
                                    user,
                                    PROJECT_MODULE.UPDATE_PROJECT
                                  )
                                )
                              }
                              onClick={async () => {
                                if (
                                  !(
                                    user &&
                                    hasPermission(
                                      user,
                                      PROJECT_MODULE.UPDATE_PROJECT
                                    )
                                  )
                                )
                                  return;
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
        <Pagination
          currentPage={currentPage}
          totalPages={Math.ceil(totalItems / itemsPerPage)}
          handlePageChange={handlePageChange}
          itemsPerPage={itemsPerPage}
          handleItemsPerPage={handlePageSizeChange}
          totalItems={totalItems}
        />
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
