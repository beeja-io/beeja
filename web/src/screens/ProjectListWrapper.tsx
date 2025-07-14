import { useOutletContext } from 'react-router-dom';
import { ProjectEntity } from '../entities/ProjectEntity';
import ProjectList from './ProjectListScreen.screen';

const ProjectListWrapper = () => {
  const { projectList, isLoading, onEditProject, updateProjectList } =
    useOutletContext<{
      projectList: ProjectEntity[];
      isLoading: boolean;
      onEditProject: (project: ProjectEntity) => void;
      updateProjectList: () => void;
    }>();

  return (
    <ProjectList
      projectList={projectList}
      isLoading={isLoading}
      onEditProject={onEditProject}
      updateProjectList={updateProjectList}
    />
  );
};

export default ProjectListWrapper;
