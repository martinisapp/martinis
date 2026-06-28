package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.SceneDao;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SceneServiceImplTest {

    @Mock
    private SceneDao sceneDao;

    private SceneServiceImpl sceneService;

    @BeforeEach
    void setUp() {
        sceneService = new SceneServiceImpl(sceneDao);
    }

    @Test
    void createDelegatesToDao() {
        Scene scene = new Scene();
        scene.setName("Scene 1");

        Scene saved = new Scene();
        saved.setId(1);
        saved.setName("Scene 1");

        when(sceneDao.create(scene)).thenReturn(saved);

        Scene result = sceneService.create(scene);

        assertEquals(1, result.getId());
        assertEquals("Scene 1", result.getName());
        verify(sceneDao).create(scene);
    }

    @Test
    void createBelowDelegatesToDao() {
        Scene scene = new Scene();
        scene.setName("Scene Below");
        scene.setOrder(2);

        Scene saved = new Scene();
        saved.setId(3);
        saved.setName("Scene Below");
        saved.setOrder(3);

        when(sceneDao.createBelow(scene)).thenReturn(saved);

        Scene result = sceneService.createBelow(scene);

        assertEquals(3, result.getId());
        verify(sceneDao).createBelow(scene);
    }

    @Test
    void readDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);
        scene.setName("Test");

        when(sceneDao.read(1)).thenReturn(scene);

        Scene result = sceneService.read(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(sceneDao).read(1);
    }

    @Test
    void readReturnsNullForNonExistentId() {
        when(sceneDao.read(999)).thenReturn(null);

        assertNull(sceneService.read(999));
        verify(sceneDao).read(999);
    }

    @Test
    void updateDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);
        scene.setName("Updated");

        sceneService.update(scene);

        verify(sceneDao).update(scene);
    }

    @Test
    void deleteDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);

        sceneService.delete(scene);

        verify(sceneDao).delete(scene);
    }

    @Test
    void moveUpDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);

        sceneService.moveUp(scene);

        verify(sceneDao).moveUp(scene);
    }

    @Test
    void moveDownDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);

        sceneService.moveDown(scene);

        verify(sceneDao).moveDown(scene);
    }

    @Test
    void listDelegatesToDao() {
        Scene s1 = new Scene();
        s1.setId(1);

        Scene s2 = new Scene();
        s2.setId(2);

        when(sceneDao.list()).thenReturn(Arrays.asList(s1, s2));

        List<Scene> result = sceneService.list();

        assertEquals(2, result.size());
        verify(sceneDao).list();
    }

    @Test
    void listReturnsEmptyList() {
        when(sceneDao.list()).thenReturn(Collections.emptyList());

        assertTrue(sceneService.list().isEmpty());
        verify(sceneDao).list();
    }

    @Test
    void getPreviousSceneDelegatesToDao() {
        Scene current = new Scene();
        current.setId(2);

        Scene previous = new Scene();
        previous.setId(1);

        when(sceneDao.getPreviousScene(current)).thenReturn(previous);

        Scene result = sceneService.getPreviousScene(current);

        assertEquals(1, result.getId());
        verify(sceneDao).getPreviousScene(current);
    }

    @Test
    void getPreviousSceneReturnsNullForFirstScene() {
        Scene first = new Scene();
        first.setId(1);

        when(sceneDao.getPreviousScene(first)).thenReturn(null);

        assertNull(sceneService.getPreviousScene(first));
    }

    @Test
    void getNextSceneDelegatesToDao() {
        Scene current = new Scene();
        current.setId(1);

        Scene next = new Scene();
        next.setId(2);

        when(sceneDao.getNextScene(current)).thenReturn(next);

        Scene result = sceneService.getNextScene(current);

        assertEquals(2, result.getId());
        verify(sceneDao).getNextScene(current);
    }

    @Test
    void getNextSceneReturnsNullForLastScene() {
        Scene last = new Scene();
        last.setId(5);

        when(sceneDao.getNextScene(last)).thenReturn(null);

        assertNull(sceneService.getNextScene(last));
    }

    @Test
    void getScenesByProjectDelegatesToDao() {
        Project project = new Project();
        project.setId(1);

        Scene s1 = new Scene();
        s1.setId(1);

        when(sceneDao.getScenesByProject(project)).thenReturn(List.of(s1));

        List<Scene> result = sceneService.getScenesByProject(project);

        assertEquals(1, result.size());
        verify(sceneDao).getScenesByProject(project);
    }
}
