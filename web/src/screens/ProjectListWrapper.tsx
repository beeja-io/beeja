import { useOutletContext } from 'react-router-dom';
import { ProjectEntity } from '../entities/ProjectEntity';
import ProjectList from './ProjectListScreen.screen';

type ProjectOutletContext = {
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

const ProjectListWrapper = () => {
  const {
    projectList,
    isLoading,
    onEditProject,
    updateProjectList,
    totalItems,
    currentPage,
    setCurrentPage,
    itemsPerPage,
    setItemsPerPage,
  } = useOutletContext<ProjectOutletContext>();

  return (
    <ProjectList
      projectList={projectList}
      isLoading={isLoading}
      onEditProject={onEditProject}
      updateProjectList={updateProjectList}
      totalItems={totalItems}
      currentPage={currentPage}
      setCurrentPage={setCurrentPage}
      itemsPerPage={itemsPerPage}
      setItemsPerPage={setItemsPerPage}
    />
  );
};

export default ProjectListWrapper;
