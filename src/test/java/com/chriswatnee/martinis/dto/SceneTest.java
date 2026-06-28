package com.chriswatnee.martinis.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneTest {

    @Test
    void gettersAndSetters() {
        Scene scene = new Scene();

        assertNull(scene.getId());
        assertNull(scene.getOrder());
        assertNull(scene.getName());
        assertNull(scene.getProject());

        scene.setId(1);
        scene.setOrder(3);
        scene.setName("Opening Scene");

        Project project = new Project();
        project.setId(10);
        scene.setProject(project);

        assertEquals(1, scene.getId());
        assertEquals(3, scene.getOrder());
        assertEquals("Opening Scene", scene.getName());
        assertNotNull(scene.getProject());
        assertEquals(10, scene.getProject().getId());
    }

    @Test
    void implementsSerializable() {
        assertInstanceOf(java.io.Serializable.class, new Scene());
    }

    @Test
    void projectAssociationCanBeNull() {
        Scene scene = new Scene();
        scene.setProject(new Project());
        scene.setProject(null);

        assertNull(scene.getProject());
    }
}
