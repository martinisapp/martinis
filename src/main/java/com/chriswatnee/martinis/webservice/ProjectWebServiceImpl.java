/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.project.createproject.CreateProjectCommandModel;
import com.chriswatnee.martinis.commandmodel.project.editproject.EditProjectCommandModel;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import com.chriswatnee.martinis.service.PersonService;
import com.chriswatnee.martinis.service.ProjectService;
import com.chriswatnee.martinis.service.SceneService;
import com.chriswatnee.martinis.viewmodel.project.createproject.CreateProjectViewModel;
import com.chriswatnee.martinis.viewmodel.project.editproject.EditProjectViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectlist.ProjectListViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectlist.ProjectViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectprofile.PersonViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectprofile.ProjectProfileViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectprofile.SceneViewModel;
import com.chriswatnee.martinis.exception.ResourceNotFoundException;
import com.chriswatnee.martinis.webservice.support.ViewModelMapper;
import java.util.List;
import jakarta.inject.Inject;

/**
 *
 * @author chris
 */
public class ProjectWebServiceImpl implements ProjectWebService {

    ProjectService projectService;
    SceneService sceneService;
    PersonService personService;

    @Inject
    public ProjectWebServiceImpl(ProjectService projectService, SceneService sceneService, PersonService personService) {
        this.projectService = projectService;
        this.sceneService = sceneService;
        this.personService = personService;
    }
    
    @Override
    public ProjectListViewModel getProjectListViewModel() {

        // Instantiate
        ProjectListViewModel projectListViewModel = new ProjectListViewModel();

        // Look stuff up
        List<Project> projects = projectService.list();

        // Put stuff in
        projectListViewModel.setProjects(translate(projects));

        return projectListViewModel;
    }

    @Override
    public ProjectProfileViewModel getProjectProfileViewModel(Integer id) {
        
        // Instantiate
        ProjectProfileViewModel projectProfileViewModel = new ProjectProfileViewModel();

        // Look up stuff
        Project project = projectService.read(id);
        if (project == null) {
            throw new ResourceNotFoundException("Project", id);
        }
        List<Scene> scenes = sceneService.getScenesByProject(project);
        List<Person> persons = personService.getPersonsByProject(project);

        // Put stuff
        projectProfileViewModel.setId(project.getId());
        projectProfileViewModel.setTitle(project.getTitle());
        projectProfileViewModel.setScenes(translateScene(scenes));
        projectProfileViewModel.setPersons(translatePerson(persons));

        return projectProfileViewModel;
    }

    @Override
    public CreateProjectViewModel getCreateProjectViewModel() {

        // Instantiate
        CreateProjectViewModel createProjectViewModel = new CreateProjectViewModel();

        CreateProjectCommandModel commandModel = new CreateProjectCommandModel();
        createProjectViewModel.setCreateProjectCommandModel(commandModel);

        return createProjectViewModel;
    }

    @Override
    public EditProjectViewModel getEditProjectViewModel(Integer id) {

        // Instantiate
        EditProjectViewModel editProjectViewModel = new EditProjectViewModel();

        // Look up stuff
        Project existingProject = projectService.read(id);
        if (existingProject == null) {
            throw new ResourceNotFoundException("Project", id);
        }

        // Populate
        editProjectViewModel.setId(id);

        // Populate commmand model
        EditProjectCommandModel commandModel = new EditProjectCommandModel();
        commandModel.setId(existingProject.getId());
        commandModel.setTitle(existingProject.getTitle());

        editProjectViewModel.setEditProjectCommandModel(commandModel);

        return editProjectViewModel;
    }

    @Override
    public Project saveCreateProjectCommandModel(CreateProjectCommandModel createProjectCommandModel) {

        // Instantiate
        Project project = new Project();
        
        // Put stuff
        project.setTitle(createProjectCommandModel.getTitle());

        // Save stuff
        project = projectService.create(project);
        
        return project;
    }

    @Override
    public Project saveEditProjectCommandModel(EditProjectCommandModel editProjectCommandModel) {

        // Instantiate
        Project project = projectService.read(editProjectCommandModel.getId());
        if (project == null) {
            throw new ResourceNotFoundException("Project", editProjectCommandModel.getId());
        }

        // Put stuff
        project.setTitle(editProjectCommandModel.getTitle());

        // Save stuff
        projectService.update(project);

        return project;
    }
    
    @Override
    public Project deleteProject(Integer id) {

        // Instantiate
        Project project = projectService.read(id);
        if (project == null) {
            throw new ResourceNotFoundException("Project", id);
        }

        // Delete
        projectService.delete(project);

        return project;
    }

    private List<SceneViewModel> translateScene(List<Scene> scenes) {
        return ViewModelMapper.mapList(scenes, this::translateScene);
    }

    private SceneViewModel translateScene(Scene scene) {

        SceneViewModel sceneViewModel = new SceneViewModel();

        sceneViewModel.setName(scene.getName());
        sceneViewModel.setId(scene.getId());

        return sceneViewModel;
    }

    private List<PersonViewModel> translatePerson(List<Person> persons) {
        return ViewModelMapper.mapList(persons, this::translatePerson);
    }

    private PersonViewModel translatePerson(Person person) {

        PersonViewModel personViewModel = new PersonViewModel();

        personViewModel.setName(person.getName());
        personViewModel.setId(person.getId());

        return personViewModel;
    }

    private List<ProjectViewModel> translate(List<Project> projects) {
        return ViewModelMapper.mapList(projects, this::translate);
    }

    private ProjectViewModel translate(Project project) {

        ProjectViewModel projectViewModel = new ProjectViewModel();

        projectViewModel.setTitle(project.getTitle());
        projectViewModel.setId(project.getId());

        return projectViewModel;
    }
    
}
