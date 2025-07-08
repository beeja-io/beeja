import { useCallback, useEffect, useState } from 'react';
import { matchPath, useLocation, useNavigate } from 'react-router-dom';
import AddProjectForm from '../components/directComponents/AddProjectForm.component';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { Outlet } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { ProjectEntity } from '../entities/ProjectEntity';
import { getAllProjects } from '../service/axiosInstance';

const ProjectManagement = () => {
  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };

  const location = useLocation();

  const isProjectDetailsRoute = matchPath(
    '/clients/client-management/:id',
    location.pathname
  );

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedProjectData, setSelectedProjectData] =
    useState<ProjectEntity | null>(null);

  const handleOpenCreateModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(true);
    setSelectedProjectData(null);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(false);
    setSelectedProjectData(null);
  }, []);

  const handleSuccessMessage = () => {
    handleShowSuccessMessage();
    setIsCreateModalOpen(false);
  };

  const [loading, setLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
  };

  const [allProjects, setAllProjects] = useState<ProjectEntity[]>([]);
  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllProjects();
      setAllProjects(res.data.projects);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      throw new Error('Error fetching project data: ' + error);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const updateProjectList = async (): Promise<void> => {
    await fetchData();
  };

  const { t } = useTranslation();

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Project Management')}
            {isCreateModalOpen && (
              <>
                <span className="separator"> {`>`} </span>
                <span className="nav_AddClient">{t('Add Project')}</span>
              </>
            )}
            {!isCreateModalOpen && isProjectDetailsRoute && (
              <>
                <span className="separator"> {`>`} </span>
                <span className="nav_AddClient">{t('Project Details')}</span>
              </>
            )}
          </span>
          {!isCreateModalOpen && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add New Project')}
            </Button>
          )}
        </ExpenseHeadingSection>

        {isCreateModalOpen ? (
          <AddProjectForm
            handleClose={handleCloseModal}
            handleSuccessMessage={handleSuccessMessage}
            initialData={selectedProjectData ?? undefined}
          />
        ) : (
          <Outlet
            context={{
              projectList: allProjects,
              isLoading: loading,
              updateProjectList,
            }}
          />
        )}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_PROJECT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_ADDED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default ProjectManagement;
