package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.actor.createactor.CreateActorCommandModel;
import com.chriswatnee.martinis.commandmodel.actor.editactor.EditActorCommandModel;
import com.chriswatnee.martinis.dto.Actor;
import com.chriswatnee.martinis.service.ActorService;
import com.chriswatnee.martinis.viewmodel.actor.actorlist.ActorListViewModel;
import com.chriswatnee.martinis.viewmodel.actor.actorprofile.ActorProfileViewModel;
import com.chriswatnee.martinis.viewmodel.actor.createactor.CreateActorViewModel;
import com.chriswatnee.martinis.viewmodel.actor.editactor.EditActorViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorWebServiceImplTest {

    @Mock
    private ActorService actorService;

    private ActorWebServiceImpl webService;

    @BeforeEach
    void setUp() {
        webService = new ActorWebServiceImpl(actorService);
    }

    @Test
    void getActorListViewModelReturnsActors() {
        Actor a1 = new Actor();
        a1.setId(1);
        a1.setFirstName("John");
        a1.setLastName("Doe");
        a1.setPhone("555-1234");
        a1.setEmail("john@test.com");

        Actor a2 = new Actor();
        a2.setId(2);
        a2.setFirstName("Jane");
        a2.setLastName("Smith");

        when(actorService.list()).thenReturn(Arrays.asList(a1, a2));

        ActorListViewModel result = webService.getActorListViewModel();

        assertNotNull(result);
        assertEquals(2, result.getActors().size());
        assertEquals("John", result.getActors().get(0).getFirst());
        assertEquals("Doe", result.getActors().get(0).getLast());
        assertEquals("555-1234", result.getActors().get(0).getPhone());
        assertEquals("john@test.com", result.getActors().get(0).getEmail());
        assertEquals(1, result.getActors().get(0).getId());
    }

    @Test
    void getActorListViewModelReturnsEmptyList() {
        when(actorService.list()).thenReturn(Collections.emptyList());

        ActorListViewModel result = webService.getActorListViewModel();

        assertTrue(result.getActors().isEmpty());
    }

    @Test
    void getActorProfileViewModelPopulatesAllFields() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setPhone("555-1234");
        actor.setEmail("john@test.com");

        when(actorService.read(1)).thenReturn(actor);

        ActorProfileViewModel result = webService.getActorProfileViewModel(1);

        assertEquals(1, result.getId());
        assertEquals("John", result.getFirst());
        assertEquals("Doe", result.getLast());
        assertEquals("555-1234", result.getPhone());
        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    void getCreateActorViewModelReturnsViewModelWithCommandModel() {
        CreateActorViewModel result = webService.getCreateActorViewModel();

        assertNotNull(result);
        assertNotNull(result.getCreateActorCommandModel());
    }

    @Test
    void getEditActorViewModelPopulatesFromExistingActor() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setPhone("555-1234");
        actor.setEmail("john@test.com");

        when(actorService.read(1)).thenReturn(actor);

        EditActorViewModel result = webService.getEditActorViewModel(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        EditActorCommandModel cmd = result.getEditActorCommandModel();
        assertEquals(1, cmd.getId());
        assertEquals("John", cmd.getFirst());
        assertEquals("Doe", cmd.getLast());
        assertEquals("555-1234", cmd.getPhone());
        assertEquals("john@test.com", cmd.getEmail());
    }

    @Test
    void saveCreateActorCommandModelCreatesActor() {
        CreateActorCommandModel cmd = new CreateActorCommandModel();
        cmd.setFirst("Jane");
        cmd.setLast("Smith");
        cmd.setPhone("555-5678");
        cmd.setEmail("jane@test.com");

        Actor saved = new Actor();
        saved.setId(1);
        saved.setFirstName("Jane");
        saved.setLastName("Smith");

        when(actorService.create(any(Actor.class))).thenReturn(saved);

        Actor result = webService.saveCreateActorCommandModel(cmd);

        assertEquals(1, result.getId());
        verify(actorService).create(any(Actor.class));
    }

    @Test
    void saveEditActorCommandModelUpdatesActor() {
        EditActorCommandModel cmd = new EditActorCommandModel();
        cmd.setId(1);
        cmd.setFirst("Updated");
        cmd.setLast("Name");
        cmd.setPhone("555-9999");
        cmd.setEmail("updated@test.com");

        Actor existing = new Actor();
        existing.setId(1);
        existing.setFirstName("Old");
        existing.setLastName("Name");

        when(actorService.read(1)).thenReturn(existing);

        Actor result = webService.saveEditActorCommandModel(cmd);

        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("555-9999", result.getPhone());
        assertEquals("updated@test.com", result.getEmail());
        verify(actorService).update(existing);
    }

    @Test
    void deleteActorReadsAndDeletes() {
        Actor actor = new Actor();
        actor.setId(1);

        when(actorService.read(1)).thenReturn(actor);

        Actor result = webService.deleteActor(1);

        assertEquals(1, result.getId());
        verify(actorService).read(1);
        verify(actorService).delete(actor);
    }
}
