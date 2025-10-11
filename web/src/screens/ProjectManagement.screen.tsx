import { useCallback, useEffect, useState } from 'react';
import { matchPath, useLocation, useNavigate, Outlet } from 'react-router-dom';
import AddProjectForm, {
  ProjectFormData,
} from '../components/directComponents/AddProjectForm.component';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
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
  const [selectedProjectData, setSelectedProjectData] =
    useState<Partial<ProjectFormData> | null>(null);
  const [allProjects, setAllProjects] = useState<ProjectEntity[]>([]);
  const [loading, setLoading] = useState(false);

  const [toastData, setToastData] = useState<{
    heading: string;
    body: string;
  } | null>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const isProjectDetailsRoute = matchPath(
    '/projects/project-management/:id/:id',
    location.pathname
  );

  const goToPreviousPage = () => {
    if (isCreateModalOpen) {
      setIsCreateModalOpen(false);
    } else {
      navigate(-1);
    }
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
      heading: isEditMode
        ? 'Project Updated Successfully.'
        : 'Project Added Successfully.',
      body: isEditMode
        ? `Project "${projectId}" has been updated successfully.`
        : `New Project has been Added\nsuccessfully with project: "${projectId}".`,
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
      const res = await getAllProjects(currentPage, itemsPerPage);
      setAllProjects(res.data.data || []);
      const { totalRecords} = res.data.metadata || {};
      setTotalItems(totalRecords);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      toast.error('Error Fetching Project data');
    }
  }, [currentPage, itemsPerPage]);

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
            {t('Project_Management')}
            {isCreateModalOpen && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">
                  {isEditMode ? t('Edit_Project') : t('Add_Project')}
                </span>
              </>
            )}
            {!isCreateModalOpen && isProjectDetailsRoute && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Project_Details')}</span>
              </>
            )}
          </span>
          {!isCreateModalOpen && !isProjectDetailsRoute && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add_New_Project')}
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
              totalItems,
              currentPage,
              setCurrentPage,
              itemsPerPage,
              setItemsPerPage,
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
