package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.person.createperson.CreatePersonCommandModel;
import com.chriswatnee.martinis.commandmodel.person.editperson.EditPersonCommandModel;
import com.chriswatnee.martinis.dto.Actor;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.service.ActorService;
import com.chriswatnee.martinis.service.PersonService;
import com.chriswatnee.martinis.service.ProjectService;
import com.chriswatnee.martinis.viewmodel.person.createperson.CreatePersonViewModel;
import com.chriswatnee.martinis.viewmodel.person.editperson.EditPersonViewModel;
import com.chriswatnee.martinis.viewmodel.person.personprofile.PersonProfileViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonWebServiceImplTest {

    @Mock
    private PersonService personService;

    @Mock
    private ActorService actorService;

    @Mock
    private ProjectService projectService;

    private PersonWebServiceImpl webService;

    @BeforeEach
    void setUp() {
        webService = new PersonWebServiceImpl(personService, actorService, projectService);
    }

    @Test
    void getPersonProfileViewModelWithActorAndProject() {
        Actor actor = new Actor();
        actor.setId(10);
        actor.setFirstName("John");
        actor.setLastName("Doe");

        Project project = new Project();
        project.setId(20);
        project.setTitle("My Project");

        Person person = new Person();
        person.setId(1);
        person.setName("Hamlet");
        person.setFullName("Prince Hamlet");
        person.setActor(actor);
        person.setProject(project);

        when(personService.read(1)).thenReturn(person);
        when(actorService.read(10)).thenReturn(actor);
        when(projectService.read(20)).thenReturn(project);

        PersonProfileViewModel result = webService.getPersonProfileViewModel(1);

        assertEquals(1, result.getId());
        assertEquals("Hamlet", result.getName());
        assertEquals("Prince Hamlet", result.getFullName());
        assertEquals(10, result.getActorId());
        assertEquals("John Doe", result.getActorName());
        assertEquals(20, result.getProjectId());
        assertEquals("My Project", result.getProjectTitle());
    }

    @Test
    void getPersonProfileViewModelWithoutActorOrProject() {
        Person person = new Person();
        person.setId(1);
        person.setName("Orphan Character");

        when(personService.read(1)).thenReturn(person);

        PersonProfileViewModel result = webService.getPersonProfileViewModel(1);

        assertEquals(1, result.getId());
        assertEquals("Orphan Character", result.getName());
        assertEquals(0, result.getActorId());
        assertNull(result.getActorName());
        assertEquals(0, result.getProjectId());
        assertNull(result.getProjectTitle());
    }

    @Test
    void getCreatePersonViewModelPopulatesActorsAndProjectId() {
        Actor actor = new Actor();
        actor.setId(10);
        actor.setFirstName("Jane");
        actor.setLastName("Smith");

        when(actorService.list()).thenReturn(List.of(actor));

        CreatePersonViewModel result = webService.getCreatePersonViewModel(5);

        assertNotNull(result);
        assertEquals(5, result.getProjectId());
        assertNotNull(result.getCreatePersonCommandModel());
        assertEquals(5, result.getCreatePersonCommandModel().getProjectId());
        assertEquals(1, result.getActors().size());
        assertEquals("Jane Smith", result.getActors().get(0).getName());
    }

    @Test
    void getCreatePersonViewModelWithNoActors() {
        when(actorService.list()).thenReturn(Collections.emptyList());

        CreatePersonViewModel result = webService.getCreatePersonViewModel(5);

        assertTrue(result.getActors().isEmpty());
    }

    @Test
    void getEditPersonViewModelPopulatesAllFields() {
        Actor actor = new Actor();
        actor.setId(10);
        actor.setFirstName("John");
        actor.setLastName("Doe");

        Project project = new Project();
        project.setId(20);

        Person existing = new Person();
        existing.setId(1);
        existing.setName("Hamlet");
        existing.setFullName("Prince Hamlet");
        existing.setActor(actor);
        existing.setProject(project);

        when(personService.read(1)).thenReturn(existing);
        when(actorService.list()).thenReturn(List.of(actor));
        when(actorService.read(10)).thenReturn(actor);
        when(projectService.read(20)).thenReturn(project);

        EditPersonViewModel result = webService.getEditPersonViewModel(1);

        assertEquals(1, result.getId());
        assertEquals(1, result.getEditPersonCommandModel().getId());
        assertEquals("Hamlet", result.getEditPersonCommandModel().getName());
        assertEquals("Prince Hamlet", result.getEditPersonCommandModel().getFullName());
        assertEquals(10, result.getEditPersonCommandModel().getActorId());
        assertEquals(20, result.getEditPersonCommandModel().getProjectId());
        assertEquals(1, result.getActors().size());
    }

    @Test
    void getEditPersonViewModelWithNoActor() {
        Project project = new Project();
        project.setId(20);

        Person existing = new Person();
        existing.setId(1);
        existing.setName("No Actor");
        existing.setProject(project);

        when(personService.read(1)).thenReturn(existing);
        when(actorService.list()).thenReturn(Collections.emptyList());
        when(projectService.read(20)).thenReturn(project);

        EditPersonViewModel result = webService.getEditPersonViewModel(1);

        assertNull(result.getEditPersonCommandModel().getActorId());
    }

    @Test
    void saveCreatePersonCommandModelCreatesPersonWithActorAndProject() {
        CreatePersonCommandModel cmd = new CreatePersonCommandModel();
        cmd.setName("Ophelia");
        cmd.setFullName("Lady Ophelia");
        cmd.setActorId(10);
        cmd.setProjectId(20);

        Actor actor = new Actor();
        actor.setId(10);

        Project project = new Project();
        project.setId(20);

        Person saved = new Person();
        saved.setId(1);
        saved.setName("Ophelia");

        when(actorService.read(10)).thenReturn(actor);
        when(projectService.read(20)).thenReturn(project);
        when(personService.create(any(Person.class))).thenReturn(saved);

        Person result = webService.saveCreatePersonCommandModel(cmd);

        assertEquals(1, result.getId());
        verify(personService).create(any(Person.class));
    }

    @Test
    void saveCreatePersonCommandModelWithNullActorAndProject() {
        CreatePersonCommandModel cmd = new CreatePersonCommandModel();
        cmd.setName("Extra");
        cmd.setActorId(null);
        cmd.setProjectId(null);

        Person saved = new Person();
        saved.setId(1);

        when(actorService.read(null)).thenReturn(null);
        when(projectService.read(null)).thenReturn(null);
        when(personService.create(any(Person.class))).thenReturn(saved);

        Person result = webService.saveCreatePersonCommandModel(cmd);

        assertNotNull(result);
    }

    @Test
    void saveEditPersonCommandModelUpdates() {
        EditPersonCommandModel cmd = new EditPersonCommandModel();
        cmd.setId(1);
        cmd.setName("Updated");
        cmd.setFullName("Updated Full");
        cmd.setActorId(10);
        cmd.setProjectId(20);

        Person existing = new Person();
        existing.setId(1);

        Actor actor = new Actor();
        actor.setId(10);

        Project project = new Project();
        project.setId(20);

        when(personService.read(1)).thenReturn(existing);
        when(actorService.read(10)).thenReturn(actor);
        when(projectService.read(20)).thenReturn(project);

        Person result = webService.saveEditPersonCommandModel(cmd);

        assertEquals("Updated", result.getName());
        assertEquals("Updated Full", result.getFullName());
        verify(personService).update(existing);
    }

    @Test
    void deletePersonReadsAndDeletes() {
        Person person = new Person();
        person.setId(1);

        when(personService.read(1)).thenReturn(person);

        Person result = webService.deletePerson(1);

        assertEquals(1, result.getId());
        verify(personService).delete(person);
    }
}
