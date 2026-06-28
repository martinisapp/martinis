package com.chriswatnee.martinis.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void gettersAndSetters() {
        Person person = new Person();

        assertNull(person.getId());
        assertNull(person.getName());
        assertNull(person.getFullName());
        assertNull(person.getActor());
        assertNull(person.getProject());

        person.setId(1);
        person.setName("Hamlet");
        person.setFullName("Prince Hamlet of Denmark");

        Actor actor = new Actor();
        actor.setId(10);
        person.setActor(actor);

        Project project = new Project();
        project.setId(20);
        person.setProject(project);

        assertEquals(1, person.getId());
        assertEquals("Hamlet", person.getName());
        assertEquals("Prince Hamlet of Denmark", person.getFullName());
        assertEquals(10, person.getActor().getId());
        assertEquals(20, person.getProject().getId());
    }

    @Test
    void implementsSerializable() {
        assertInstanceOf(java.io.Serializable.class, new Person());
    }

    @Test
    void associationsCanBeNull() {
        Person person = new Person();
        person.setActor(new Actor());
        person.setProject(new Project());

        person.setActor(null);
        person.setProject(null);

        assertNull(person.getActor());
        assertNull(person.getProject());
    }
}
