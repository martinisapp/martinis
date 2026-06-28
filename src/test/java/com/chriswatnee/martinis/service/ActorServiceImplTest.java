package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.ActorDao;
import com.chriswatnee.martinis.dto.Actor;
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
class ActorServiceImplTest {

    @Mock
    private ActorDao actorDao;

    private ActorServiceImpl actorService;

    @BeforeEach
    void setUp() {
        actorService = new ActorServiceImpl(actorDao);
    }

    @Test
    void createDelegatesToDao() {
        Actor actor = new Actor();
        actor.setFirstName("John");
        actor.setLastName("Doe");

        Actor saved = new Actor();
        saved.setId(1);
        saved.setFirstName("John");
        saved.setLastName("Doe");

        when(actorDao.create(actor)).thenReturn(saved);

        Actor result = actorService.create(actor);

        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(actorDao).create(actor);
    }

    @Test
    void readDelegatesToDao() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setFirstName("Jane");

        when(actorDao.read(1)).thenReturn(actor);

        Actor result = actorService.read(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Jane", result.getFirstName());
        verify(actorDao).read(1);
    }

    @Test
    void readReturnsNullForNonExistentId() {
        when(actorDao.read(999)).thenReturn(null);

        assertNull(actorService.read(999));
        verify(actorDao).read(999);
    }

    @Test
    void updateDelegatesToDao() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setFirstName("Updated");

        actorService.update(actor);

        verify(actorDao).update(actor);
    }

    @Test
    void deleteDelegatesToDao() {
        Actor actor = new Actor();
        actor.setId(1);

        actorService.delete(actor);

        verify(actorDao).delete(actor);
    }

    @Test
    void listDelegatesToDao() {
        Actor a1 = new Actor();
        a1.setId(1);
        a1.setFirstName("Actor 1");

        Actor a2 = new Actor();
        a2.setId(2);
        a2.setFirstName("Actor 2");

        when(actorDao.list()).thenReturn(Arrays.asList(a1, a2));

        List<Actor> result = actorService.list();

        assertEquals(2, result.size());
        verify(actorDao).list();
    }

    @Test
    void listReturnsEmptyList() {
        when(actorDao.list()).thenReturn(Collections.emptyList());

        assertTrue(actorService.list().isEmpty());
        verify(actorDao).list();
    }
}
