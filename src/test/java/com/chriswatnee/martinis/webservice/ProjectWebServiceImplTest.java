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
import com.chriswatnee.martinis.viewmodel.project.projectprofile.ProjectProfileViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectWebServiceImplTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private SceneService sceneService;

    @Mock
    private PersonService personService;

    private ProjectWebServiceImpl webService;

    @BeforeEach
    void setUp() {
        webService = new ProjectWebServiceImpl(projectService, sceneService, personService);
    }

    @Test
    void getProjectListViewModelReturnsProjects() {
        Project p1 = new Project();
        p1.setId(1);
        p1.setTitle("Project 1");

        Project p2 = new Project();
        p2.setId(2);
        p2.setTitle("Project 2");

        when(projectService.list()).thenReturn(Arrays.asList(p1, p2));

        ProjectListViewModel result = webService.getProjectListViewModel();

        assertNotNull(result);
        assertEquals(2, result.getProjects().size());
        assertEquals("Project 1", result.getProjects().get(0).getTitle());
        assertEquals(1, result.getProjects().get(0).getId());
        assertEquals("Project 2", result.getProjects().get(1).getTitle());
    }

    @Test
    void getProjectListViewModelReturnsEmptyList() {
        when(projectService.list()).thenReturn(Collections.emptyList());

        ProjectListViewModel result = webService.getProjectListViewModel();

        assertNotNull(result);
        assertTrue(result.getProjects().isEmpty());
    }

    @Test
    void getProjectProfileViewModelPopulatesAllFields() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("Test Project");

        Scene scene = new Scene();
        scene.setId(10);
        scene.setName("Scene 1");

        Person person = new Person();
        person.setId(20);
        person.setName("Hamlet");

        when(projectService.read(1)).thenReturn(project);
        when(sceneService.getScenesByProject(project)).thenReturn(List.of(scene));
        when(personService.getPersonsByProject(project)).thenReturn(List.of(person));

        ProjectProfileViewModel result = webService.getProjectProfileViewModel(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Project", result.getTitle());
        assertEquals(1, result.getScenes().size());
        assertEquals("Scene 1", result.getScenes().get(0).getName());
        assertEquals(1, result.getPersons().size());
        assertEquals("Hamlet", result.getPersons().get(0).getName());
    }

    @Test
    void getProjectProfileViewModelWithNoScenesOrPersons() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("Empty Project");

        when(projectService.read(1)).thenReturn(project);
        when(sceneService.getScenesByProject(project)).thenReturn(Collections.emptyList());
        when(personService.getPersonsByProject(project)).thenReturn(Collections.emptyList());

        ProjectProfileViewModel result = webService.getProjectProfileViewModel(1);

        assertTrue(result.getScenes().isEmpty());
        assertTrue(result.getPersons().isEmpty());
    }

    @Test
    void getCreateProjectViewModelReturnsViewModelWithCommandModel() {
        CreateProjectViewModel result = webService.getCreateProjectViewModel();

        assertNotNull(result);
        assertNotNull(result.getCreateProjectCommandModel());
    }

    @Test
    void getEditProjectViewModelPopulatesFromExistingProject() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("Existing Project");

        when(projectService.read(1)).thenReturn(project);

        EditProjectViewModel result = webService.getEditProjectViewModel(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNotNull(result.getEditProjectCommandModel());
        assertEquals(1, result.getEditProjectCommandModel().getId());
        assertEquals("Existing Project", result.getEditProjectCommandModel().getTitle());
    }

    @Test
    void saveCreateProjectCommandModelCreatesProject() {
        CreateProjectCommandModel cmd = new CreateProjectCommandModel();
        cmd.setTitle("New Project");

        Project saved = new Project();
        saved.setId(1);
        saved.setTitle("New Project");

        when(projectService.create(any(Project.class))).thenReturn(saved);

        Project result = webService.saveCreateProjectCommandModel(cmd);

        assertEquals(1, result.getId());
        assertEquals("New Project", result.getTitle());
        verify(projectService).create(any(Project.class));
    }

    @Test
    void saveEditProjectCommandModelUpdatesProject() {
        EditProjectCommandModel cmd = new EditProjectCommandModel();
        cmd.setId(1);
        cmd.setTitle("Updated Title");

        Project existing = new Project();
        existing.setId(1);
        existing.setTitle("Old Title");

        when(projectService.read(1)).thenReturn(existing);

        Project result = webService.saveEditProjectCommandModel(cmd);

        assertEquals("Updated Title", result.getTitle());
        verify(projectService).update(existing);
    }

    @Test
    void deleteProjectReadsAndDeletesProject() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("To Delete");

        when(projectService.read(1)).thenReturn(project);

        Project result = webService.deleteProject(1);

        assertEquals(1, result.getId());
        verify(projectService).read(1);
        verify(projectService).delete(project);
    }
}
