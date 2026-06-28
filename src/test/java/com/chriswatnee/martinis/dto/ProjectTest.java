package com.chriswatnee.martinis.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void gettersAndSetters() {
        Project project = new Project();

        assertNull(project.getId());
        assertNull(project.getTitle());

        project.setId(1);
        project.setTitle("My Screenplay");

        assertEquals(1, project.getId());
        assertEquals("My Screenplay", project.getTitle());
    }

    @Test
    void implementsSerializable() {
        Project project = new Project();
        assertInstanceOf(java.io.Serializable.class, project);
    }

    @Test
    void setTitleAcceptsNull() {
        Project project = new Project();
        project.setTitle("Test");
        project.setTitle(null);

        assertNull(project.getTitle());
    }
}
