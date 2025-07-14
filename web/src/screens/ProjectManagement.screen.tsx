import { useCallback, useEffect, useState } from 'react';
import { matchPath, useLocation, useNavigate } from 'react-router-dom';
import AddProjectForm, { ProjectFormData } from '../components/directComponents/AddProjectForm.component';
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
import { getAllProjects, getProject } from '../service/axiosInstance';
import { toast } from 'sonner';

const ProjectManagement = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedProjectData, setSelectedProjectData] = useState<Partial<ProjectFormData> | null>(null);
  const [allProjects, setAllProjects] = useState<ProjectEntity[]>([]);
  const [loading, setLoading] = useState(false);


  const [toastData, setToastData] = useState<{ heading: string; body: string } | null>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);

  const isProjectDetailsRoute = matchPath(
    '/clients/client-management/:id',
    location.pathname
  );


  const goToPreviousPage = () => {
    navigate(-1);
  };

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

  const onEditProject = async (project: ProjectEntity) => {
    try {
      const res = await getProject(project.projectId, project.clientId);
      const data = res.data[0];

      const mappedData: Partial<ProjectFormData> = {
        projectId: data.projectId,
        name: data.name,
        description: data.description,
        clientId: data.clientId,
        clientName: data.clientName,
        projectManagers: data.projectManagerIds || [],
        projectResources: data.projectResourceIds || [],
        startDate: data.startDate
          ? new Date(data.startDate).toISOString().split('T')[0]
          : '',
      };

      setSelectedProjectData(mappedData);
      setIsEditMode(true);
      setIsCreateModalOpen(true);
    } catch (error) {
      toast.error('Error fetching project data');
    }
  };

  const handleSuccessMessage = (projectId: string) => {
    setToastData({
      heading: 'Project Added Successfully.',
      body: `New Project has been Added\nsuccessfully with project: "${projectId}".`,
    });
    setShowSuccessMessage(true);
    setIsCreateModalOpen(false);

    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 3000);
  };

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllProjects();
      setAllProjects(res.data.projects);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      toast.error('Error Fetching Project data');
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const updateProjectList = async (): Promise<void> => {
    await fetchData();
  };

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
            refreshProjectList={updateProjectList}
            isEditMode={isEditMode}
          />
        ) : (
          <Outlet
            context={{
              projectList: allProjects,
              isLoading: loading,
              updateProjectList,
              onEditProject,
            }}
          />
        )}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && toastData && (
        <ToastMessage
          messageType="success"
          messageHeading={toastData.heading}
          messageBody={toastData.body}
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default ProjectManagement;
