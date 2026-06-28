package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.ProjectDao;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectDao projectDao;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectDao);
    }

    @Test
    void createDelegatesToDao() {
        Project project = new Project();
        project.setTitle("Test Project");

        Project saved = new Project();
        saved.setId(1);
        saved.setTitle("Test Project");

        when(projectDao.create(project)).thenReturn(saved);

        Project result = projectService.create(project);

        assertEquals(1, result.getId());
        assertEquals("Test Project", result.getTitle());
        verify(projectDao).create(project);
    }

    @Test
    void readDelegatesToDao() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("Test");

        when(projectDao.read(1)).thenReturn(project);

        Project result = projectService.read(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(projectDao).read(1);
    }

    @Test
    void readReturnsNullForNonExistentId() {
        when(projectDao.read(999)).thenReturn(null);

        Project result = projectService.read(999);

        assertNull(result);
        verify(projectDao).read(999);
    }

    @Test
    void updateDelegatesToDao() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("Updated");

        projectService.update(project);

        verify(projectDao).update(project);
    }

    @Test
    void deleteDelegatesToDao() {
        Project project = new Project();
        project.setId(1);

        projectService.delete(project);

        verify(projectDao).delete(project);
    }

    @Test
    void listDelegatesToDao() {
        Project p1 = new Project();
        p1.setId(1);
        p1.setTitle("Project 1");

        Project p2 = new Project();
        p2.setId(2);
        p2.setTitle("Project 2");

        when(projectDao.list()).thenReturn(Arrays.asList(p1, p2));

        List<Project> result = projectService.list();

        assertEquals(2, result.size());
        verify(projectDao).list();
    }

    @Test
    void listReturnsEmptyList() {
        when(projectDao.list()).thenReturn(Collections.emptyList());

        List<Project> result = projectService.list();

        assertTrue(result.isEmpty());
        verify(projectDao).list();
    }

    @Test
    void getProjectBySceneDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);

        Project project = new Project();
        project.setId(1);
        project.setTitle("Test");

        when(projectDao.getProjectByScene(scene)).thenReturn(project);

        Project result = projectService.getProjectByScene(scene);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(projectDao).getProjectByScene(scene);
    }
}
