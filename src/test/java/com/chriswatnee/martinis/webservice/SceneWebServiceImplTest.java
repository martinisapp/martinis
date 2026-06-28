package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.scene.createscene.CreateSceneCommandModel;
import com.chriswatnee.martinis.commandmodel.scene.createscenebelow.CreateSceneBelowCommandModel;
import com.chriswatnee.martinis.commandmodel.scene.editscene.EditSceneCommandModel;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import com.chriswatnee.martinis.service.BlockService;
import com.chriswatnee.martinis.service.PersonService;
import com.chriswatnee.martinis.service.ProjectService;
import com.chriswatnee.martinis.service.SceneService;
import com.chriswatnee.martinis.viewmodel.scene.createscene.CreateSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.createscenebelow.CreateSceneBelowViewModel;
import com.chriswatnee.martinis.viewmodel.scene.editscene.EditSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.sceneprofile.SceneProfileViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SceneWebServiceImplTest {

    @Mock
    private SceneService sceneService;

    @Mock
    private BlockService blockService;

    @Mock
    private ProjectService projectService;

    @Mock
    private PersonService personService;

    private SceneWebServiceImpl webService;

    @BeforeEach
    void setUp() {
        webService = new SceneWebServiceImpl(sceneService, blockService, projectService, personService);
    }

    @Test
    void getSceneProfileViewModelPopulatesAllFields() {
        Scene scene = new Scene();
        scene.setId(1);
        scene.setName("Scene 1");
        Project project = new Project();
        project.setId(10);
        project.setTitle("My Project");
        scene.setProject(project);

        Block block = new Block();
        block.setId(100);
        block.setContent("Line");
        block.setOrder(1);

        Scene prevScene = new Scene();
        prevScene.setId(0);
        prevScene.setName("Prev");

        Scene nextScene = new Scene();
        nextScene.setId(2);
        nextScene.setName("Next");

        Person person = new Person();
        person.setId(50);
        person.setName("Hamlet");

        when(sceneService.read(1)).thenReturn(scene);
        when(blockService.getBlocksByScene(scene)).thenReturn(List.of(block));
        when(projectService.read(10)).thenReturn(project);
        when(sceneService.getPreviousScene(scene)).thenReturn(prevScene);
        when(sceneService.getNextScene(scene)).thenReturn(nextScene);
        when(personService.getPersonsByProject(project)).thenReturn(List.of(person));

        SceneProfileViewModel result = webService.getSceneProfileViewModel(1);

        assertEquals(1, result.getId());
        assertEquals("Scene 1", result.getName());
        assertEquals(10, result.getProjectId());
        assertEquals("My Project", result.getProjectTitle());
        assertEquals(0, result.getPreviousSceneId());
        assertEquals("Prev", result.getPreviousSceneName());
        assertEquals(2, result.getNextSceneId());
        assertEquals("Next", result.getNextSceneName());
        assertEquals(1, result.getBlocks().size());
        assertEquals(1, result.getPersons().size());
    }

    @Test
    void getSceneProfileViewModelWithNoProject() {
        Scene scene = new Scene();
        scene.setId(1);
        scene.setName("Orphan Scene");

        when(sceneService.read(1)).thenReturn(scene);
        when(blockService.getBlocksByScene(scene)).thenReturn(Collections.emptyList());
        when(sceneService.getPreviousScene(scene)).thenReturn(null);
        when(sceneService.getNextScene(scene)).thenReturn(null);

        SceneProfileViewModel result = webService.getSceneProfileViewModel(1);

        assertEquals(1, result.getId());
        assertEquals(0, result.getProjectId());
        assertNull(result.getProjectTitle());
        assertEquals(0, result.getPreviousSceneId());
        assertNull(result.getPreviousSceneName());
        assertEquals(0, result.getNextSceneId());
        assertNull(result.getNextSceneName());
        assertTrue(result.getBlocks().isEmpty());
        assertTrue(result.getPersons().isEmpty());
    }

    @Test
    void getCreateSceneViewModelPopulatesProjectId() {
        CreateSceneViewModel result = webService.getCreateSceneViewModel(5);

        assertNotNull(result);
        assertEquals(5, result.getProjectId());
        assertNotNull(result.getCreateSceneCommandModel());
        assertEquals(5, result.getCreateSceneCommandModel().getProjectId());
    }

    @Test
    void getCreateSceneBelowViewModelPopulatesFromExistingScene() {
        Scene existing = new Scene();
        existing.setId(1);
        existing.setName("Existing");

        Project project = new Project();
        project.setId(10);

        when(sceneService.read(1)).thenReturn(existing);
        when(projectService.getProjectByScene(existing)).thenReturn(project);

        CreateSceneBelowViewModel result = webService.getCreateSceneBelowViewModel(1);

        assertNotNull(result);
        assertEquals(10, result.getProjectId());
        assertEquals(1, result.getCreateSceneBelowCommandModel().getId());
        assertEquals(10, result.getCreateSceneBelowCommandModel().getProjectId());
    }

    @Test
    void getEditSceneViewModelPopulatesFromExistingScene() {
        Scene existing = new Scene();
        existing.setId(1);
        existing.setName("Existing Scene");
        Project project = new Project();
        project.setId(10);
        existing.setProject(project);

        when(sceneService.read(1)).thenReturn(existing);
        when(projectService.read(10)).thenReturn(project);

        EditSceneViewModel result = webService.getEditSceneViewModel(1);

        assertEquals(1, result.getId());
        assertEquals(1, result.getEditSceneCommandModel().getId());
        assertEquals("Existing Scene", result.getEditSceneCommandModel().getName());
        assertEquals(10, result.getEditSceneCommandModel().getProjectId());
    }

    @Test
    void saveCreateSceneCommandModelCreatesScene() {
        CreateSceneCommandModel cmd = new CreateSceneCommandModel();
        cmd.setName("New Scene");
        cmd.setProjectId(10);

        Project project = new Project();
        project.setId(10);

        Scene saved = new Scene();
        saved.setId(1);
        saved.setName("New Scene");

        when(projectService.read(10)).thenReturn(project);
        when(sceneService.create(any(Scene.class))).thenReturn(saved);

        Scene result = webService.saveCreateSceneCommandModel(cmd);

        assertEquals(1, result.getId());
        verify(sceneService).create(any(Scene.class));
    }

    @Test
    void saveCreateSceneBelowCommandModelCreatesSceneBelow() {
        CreateSceneBelowCommandModel cmd = new CreateSceneBelowCommandModel();
        cmd.setId(1);
        cmd.setName("Below Scene");
        cmd.setProjectId(10);

        Scene existing = new Scene();
        existing.setId(1);
        existing.setOrder(3);
        Project project = new Project();
        project.setId(10);
        existing.setProject(project);

        Scene saved = new Scene();
        saved.setId(2);

        when(sceneService.read(1)).thenReturn(existing);
        when(projectService.read(10)).thenReturn(project);
        when(sceneService.createBelow(any(Scene.class))).thenReturn(saved);

        Scene result = webService.saveCreateSceneBelowCommandModel(cmd);

        assertEquals(2, result.getId());
        verify(sceneService).createBelow(any(Scene.class));
    }

    @Test
    void saveEditSceneCommandModelUpdatesScene() {
        EditSceneCommandModel cmd = new EditSceneCommandModel();
        cmd.setId(1);
        cmd.setName("Updated Name");
        cmd.setProjectId(10);

        Scene existing = new Scene();
        existing.setId(1);
        existing.setName("Old Name");

        Project project = new Project();
        project.setId(10);

        when(sceneService.read(1)).thenReturn(existing);
        when(projectService.read(10)).thenReturn(project);

        Scene result = webService.saveEditSceneCommandModel(cmd);

        assertEquals("Updated Name", result.getName());
        verify(sceneService).update(existing);
    }

    @Test
    void deleteSceneReadsAndDeletes() {
        Scene scene = new Scene();
        scene.setId(1);

        when(sceneService.read(1)).thenReturn(scene);

        Scene result = webService.deleteScene(1);

        assertEquals(1, result.getId());
        verify(sceneService).delete(scene);
    }

    @Test
    void moveSceneUpReadsAndMoves() {
        Scene scene = new Scene();
        scene.setId(1);

        when(sceneService.read(1)).thenReturn(scene);

        Scene result = webService.moveSceneUp(1);

        assertEquals(1, result.getId());
        verify(sceneService).moveUp(scene);
    }

    @Test
    void moveSceneDownReadsAndMoves() {
        Scene scene = new Scene();
        scene.setId(1);

        when(sceneService.read(1)).thenReturn(scene);

        Scene result = webService.moveSceneDown(1);

        assertEquals(1, result.getId());
        verify(sceneService).moveDown(scene);
    }
}
