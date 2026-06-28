package com.chriswatnee.martinis.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void gettersAndSetters() {
        Block block = new Block();

        assertNull(block.getId());
        assertNull(block.getOrder());
        assertNull(block.getContent());
        assertNull(block.getPerson());
        assertNull(block.getScene());

        block.setId(1);
        block.setOrder(5);
        block.setContent("To be or not to be");

        Person person = new Person();
        person.setId(10);
        block.setPerson(person);

        Scene scene = new Scene();
        scene.setId(20);
        block.setScene(scene);

        assertEquals(1, block.getId());
        assertEquals(5, block.getOrder());
        assertEquals("To be or not to be", block.getContent());
        assertEquals(10, block.getPerson().getId());
        assertEquals(20, block.getScene().getId());
    }

    @Test
    void implementsSerializable() {
        assertInstanceOf(java.io.Serializable.class, new Block());
    }

    @Test
    void associationsCanBeNull() {
        Block block = new Block();
        block.setPerson(new Person());
        block.setScene(new Scene());

        block.setPerson(null);
        block.setScene(null);

        assertNull(block.getPerson());
        assertNull(block.getScene());
    }
}
