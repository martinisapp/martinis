package com.chriswatnee.martinis.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Test
    void gettersAndSetters() {
        Actor actor = new Actor();

        assertNull(actor.getId());
        assertNull(actor.getFirstName());
        assertNull(actor.getLastName());
        assertNull(actor.getPhone());
        assertNull(actor.getEmail());

        actor.setId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setPhone("555-1234");
        actor.setEmail("john@example.com");

        assertEquals(1, actor.getId());
        assertEquals("John", actor.getFirstName());
        assertEquals("Doe", actor.getLastName());
        assertEquals("555-1234", actor.getPhone());
        assertEquals("john@example.com", actor.getEmail());
    }

    @Test
    void implementsSerializable() {
        assertInstanceOf(java.io.Serializable.class, new Actor());
    }

    @Test
    void fieldsAcceptNull() {
        Actor actor = new Actor();
        actor.setFirstName("Test");
        actor.setLastName("User");
        actor.setPhone("123");
        actor.setEmail("test@test.com");

        actor.setFirstName(null);
        actor.setLastName(null);
        actor.setPhone(null);
        actor.setEmail(null);

        assertNull(actor.getFirstName());
        assertNull(actor.getLastName());
        assertNull(actor.getPhone());
        assertNull(actor.getEmail());
    }
}
