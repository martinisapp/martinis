package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.block.createblock.CreateBlockCommandModel;
import com.chriswatnee.martinis.commandmodel.block.createblockbelow.CreateBlockBelowCommandModel;
import com.chriswatnee.martinis.commandmodel.block.editblock.EditBlockCommandModel;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import com.chriswatnee.martinis.service.BlockService;
import com.chriswatnee.martinis.service.PersonService;
import com.chriswatnee.martinis.service.ProjectService;
import com.chriswatnee.martinis.service.SceneService;
import com.chriswatnee.martinis.viewmodel.block.createblock.CreateBlockViewModel;
import com.chriswatnee.martinis.viewmodel.block.createblockbelow.CreateBlockBelowViewModel;
import com.chriswatnee.martinis.viewmodel.block.editblock.EditBlockViewModel;
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
class BlockWebServiceImplTest {

    @Mock
    private BlockService blockService;

    @Mock
    private PersonService personService;

    @Mock
    private SceneService sceneService;

    @Mock
    private ProjectService projectService;

    private BlockWebServiceImpl webService;

    @BeforeEach
    void setUp() {
        webService = new BlockWebServiceImpl(blockService, personService, sceneService, projectService);
    }

    @Test
    void getCreateBlockViewModelPopulatesFromScene() {
        Scene scene = new Scene();
        scene.setId(1);

        Project project = new Project();
        project.setId(10);

        Person person = new Person();
        person.setId(20);
        person.setName("Hamlet");

        when(sceneService.read(1)).thenReturn(scene);
        when(projectService.getProjectByScene(scene)).thenReturn(project);
        when(personService.getPersonsByProject(project)).thenReturn(List.of(person));

        CreateBlockViewModel result = webService.getCreateBlockViewModel(1);

        assertNotNull(result);
        assertEquals(1, result.getSceneId());
        assertEquals(1, result.getCreateBlockCommandModel().getSceneId());
        assertEquals(1, result.getPersons().size());
        assertEquals("Hamlet", result.getPersons().get(0).getName());
    }

    @Test
    void getCreateBlockBelowViewModelPopulatesFromExistingBlock() {
        Block existing = new Block();
        existing.setId(1);
        Scene scene = new Scene();
        scene.setId(10);
        existing.setScene(scene);

        Project project = new Project();
        project.setId(20);

        when(blockService.read(1)).thenReturn(existing);
        when(sceneService.read(10)).thenReturn(scene);
        when(projectService.getProjectByScene(scene)).thenReturn(project);
        when(personService.getPersonsByProject(project)).thenReturn(Collections.emptyList());

        CreateBlockBelowViewModel result = webService.getCreateBlockBelowViewModel(1);

        assertNotNull(result);
        assertEquals(10, result.getSceneId());
        assertEquals(1, result.getCreateBlockBelowCommandModel().getId());
    }

    @Test
    void getEditBlockViewModelPopulatesFromExistingBlock() {
        Block existing = new Block();
        existing.setId(1);
        existing.setContent("Test content");

        Person person = new Person();
        person.setId(20);
        existing.setPerson(person);

        Scene scene = new Scene();
        scene.setId(10);
        existing.setScene(scene);

        when(blockService.read(1)).thenReturn(existing);
        when(personService.list()).thenReturn(List.of(person));
        when(personService.read(20)).thenReturn(person);
        when(sceneService.read(10)).thenReturn(scene);

        EditBlockViewModel result = webService.getEditBlockViewModel(1);

        assertNotNull(result);
        assertEquals(10, result.getSceneId());
        assertEquals(1, result.getEditBlockCommandModel().getId());
        assertEquals("Test content", result.getEditBlockCommandModel().getContent());
        assertEquals(20, result.getEditBlockCommandModel().getPersonId());
    }

    @Test
    void getEditBlockViewModelWithNoPerson() {
        Block existing = new Block();
        existing.setId(1);
        existing.setContent("No person");

        Scene scene = new Scene();
        scene.setId(10);
        existing.setScene(scene);

        when(blockService.read(1)).thenReturn(existing);
        when(personService.list()).thenReturn(Collections.emptyList());
        when(sceneService.read(10)).thenReturn(scene);

        EditBlockViewModel result = webService.getEditBlockViewModel(1);

        assertNull(result.getEditBlockCommandModel().getPersonId());
    }

    @Test
    void saveCreateBlockCommandModelCreatesBlock() {
        CreateBlockCommandModel cmd = new CreateBlockCommandModel();
        cmd.setContent("New line");
        cmd.setPersonId(20);
        cmd.setSceneId(10);

        Person person = new Person();
        person.setId(20);

        Scene scene = new Scene();
        scene.setId(10);

        Block saved = new Block();
        saved.setId(1);
        saved.setContent("New line");

        when(personService.read(20)).thenReturn(person);
        when(sceneService.read(10)).thenReturn(scene);
        when(blockService.create(any(Block.class))).thenReturn(saved);

        Block result = webService.saveCreateBlockCommandModel(cmd);

        assertEquals(1, result.getId());
        verify(blockService).create(any(Block.class));
    }

    @Test
    void saveCreateBlockCommandModelWithoutPerson() {
        CreateBlockCommandModel cmd = new CreateBlockCommandModel();
        cmd.setContent("Stage direction");
        cmd.setSceneId(10);

        Scene scene = new Scene();
        scene.setId(10);

        Block saved = new Block();
        saved.setId(1);

        when(sceneService.read(10)).thenReturn(scene);
        when(blockService.create(any(Block.class))).thenReturn(saved);

        Block result = webService.saveCreateBlockCommandModel(cmd);

        assertNotNull(result);
        verify(personService, never()).read(any());
    }

    @Test
    void saveCreateBlockBelowCommandModelCreatesBlockBelow() {
        CreateBlockBelowCommandModel cmd = new CreateBlockBelowCommandModel();
        cmd.setId(1);
        cmd.setContent("Below line");
        cmd.setPersonId(20);
        cmd.setSceneId(10);

        Block existing = new Block();
        existing.setId(1);
        existing.setOrder(3);
        Scene scene = new Scene();
        scene.setId(10);
        existing.setScene(scene);

        Person person = new Person();
        person.setId(20);

        Block saved = new Block();
        saved.setId(2);

        when(blockService.read(1)).thenReturn(existing);
        when(personService.read(20)).thenReturn(person);
        when(sceneService.read(10)).thenReturn(scene);
        when(blockService.createBelow(any(Block.class))).thenReturn(saved);

        Block result = webService.saveCreateBlockBelowCommandModel(cmd);

        assertEquals(2, result.getId());
        verify(blockService).createBelow(any(Block.class));
    }

    @Test
    void saveEditBlockCommandModelUpdatesBlock() {
        EditBlockCommandModel cmd = new EditBlockCommandModel();
        cmd.setId(1);
        cmd.setContent("Updated content");
        cmd.setPersonId(20);
        cmd.setSceneId(10);

        Block existing = new Block();
        existing.setId(1);

        Person person = new Person();
        person.setId(20);

        Scene scene = new Scene();
        scene.setId(10);

        when(blockService.read(1)).thenReturn(existing);
        when(personService.read(20)).thenReturn(person);
        when(sceneService.read(10)).thenReturn(scene);

        Block result = webService.saveEditBlockCommandModel(cmd);

        assertEquals("Updated content", result.getContent());
        verify(blockService).update(existing);
    }

    @Test
    void deleteBlockReadsAndDeletes() {
        Block block = new Block();
        block.setId(1);

        when(blockService.read(1)).thenReturn(block);

        Block result = webService.deleteBlock(1);

        assertEquals(1, result.getId());
        verify(blockService).delete(block);
    }

    @Test
    void restoreBlockWithValidPerson() {
        Person person = new Person();
        person.setId(20);

        Block block = new Block();
        block.setId(1);
        block.setPerson(person);

        when(personService.read(20)).thenReturn(person);

        webService.restoreBlock(block);

        assertNotNull(block.getPerson());
        verify(blockService).restore(block);
    }

    @Test
    void restoreBlockWithDeletedPerson() {
        Person person = new Person();
        person.setId(20);

        Block block = new Block();
        block.setId(1);
        block.setPerson(person);

        when(personService.read(20)).thenReturn(null);

        webService.restoreBlock(block);

        assertNull(block.getPerson());
        verify(blockService).restore(block);
    }

    @Test
    void restoreBlockWithNoPerson() {
        Block block = new Block();
        block.setId(1);

        webService.restoreBlock(block);

        verify(blockService).restore(block);
        verify(personService, never()).read(any());
    }

    @Test
    void moveBlockUpReadsAndMoves() {
        Block block = new Block();
        block.setId(1);

        when(blockService.read(1)).thenReturn(block);

        Block result = webService.moveBlockUp(1);

        assertEquals(1, result.getId());
        verify(blockService).moveUp(block);
    }

    @Test
    void moveBlockDownReadsAndMoves() {
        Block block = new Block();
        block.setId(1);

        when(blockService.read(1)).thenReturn(block);

        Block result = webService.moveBlockDown(1);

        assertEquals(1, result.getId());
        verify(blockService).moveDown(block);
    }

    @Test
    void reorderBlocksDelegatesToService() {
        List<Integer> ids = Arrays.asList(3, 1, 2);

        webService.reorderBlocks(ids);

        verify(blockService).reorderBlocks(ids);
    }

    @Test
    void getPersonsForSceneLooksUpViaProjectChain() {
        Scene scene = new Scene();
        scene.setId(1);

        Project project = new Project();
        project.setId(10);

        Person person = new Person();
        person.setId(20);

        when(sceneService.read(1)).thenReturn(scene);
        when(projectService.getProjectByScene(scene)).thenReturn(project);
        when(personService.getPersonsByProject(project)).thenReturn(List.of(person));

        List<Person> result = webService.getPersonsForScene(1);

        assertEquals(1, result.size());
        assertEquals(20, result.get(0).getId());
    }

    @Test
    void getBlockDelegatesToService() {
        Block block = new Block();
        block.setId(1);

        when(blockService.read(1)).thenReturn(block);

        Block result = webService.getBlock(1);

        assertEquals(1, result.getId());
    }
}
