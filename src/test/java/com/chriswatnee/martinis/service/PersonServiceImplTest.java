package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.PersonDao;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
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
class PersonServiceImplTest {

    @Mock
    private PersonDao personDao;

    private PersonServiceImpl personService;

    @BeforeEach
    void setUp() {
        personService = new PersonServiceImpl(personDao);
    }

    @Test
    void createDelegatesToDao() {
        Person person = new Person();
        person.setName("Hamlet");

        Person saved = new Person();
        saved.setId(1);
        saved.setName("Hamlet");

        when(personDao.create(person)).thenReturn(saved);

        Person result = personService.create(person);

        assertEquals(1, result.getId());
        assertEquals("Hamlet", result.getName());
        verify(personDao).create(person);
    }

    @Test
    void readDelegatesToDao() {
        Person person = new Person();
        person.setId(1);
        person.setName("Ophelia");

        when(personDao.read(1)).thenReturn(person);

        Person result = personService.read(1);

        assertNotNull(result);
        assertEquals("Ophelia", result.getName());
        verify(personDao).read(1);
    }

    @Test
    void readReturnsNullForNonExistentId() {
        when(personDao.read(999)).thenReturn(null);

        assertNull(personService.read(999));
        verify(personDao).read(999);
    }

    @Test
    void updateDelegatesToDao() {
        Person person = new Person();
        person.setId(1);
        person.setName("Updated");

        personService.update(person);

        verify(personDao).update(person);
    }

    @Test
    void deleteDelegatesToDao() {
        Person person = new Person();
        person.setId(1);

        personService.delete(person);

        verify(personDao).delete(person);
    }

    @Test
    void listDelegatesToDao() {
        Person p1 = new Person();
        p1.setId(1);

        Person p2 = new Person();
        p2.setId(2);

        when(personDao.list()).thenReturn(Arrays.asList(p1, p2));

        List<Person> result = personService.list();

        assertEquals(2, result.size());
        verify(personDao).list();
    }

    @Test
    void listReturnsEmptyList() {
        when(personDao.list()).thenReturn(Collections.emptyList());

        assertTrue(personService.list().isEmpty());
        verify(personDao).list();
    }

    @Test
    void getPersonsByProjectDelegatesToDao() {
        Project project = new Project();
        project.setId(1);

        Person p1 = new Person();
        p1.setId(1);

        when(personDao.getPersonsByProject(project)).thenReturn(List.of(p1));

        List<Person> result = personService.getPersonsByProject(project);

        assertEquals(1, result.size());
        verify(personDao).getPersonsByProject(project);
    }

    @Test
    void getPersonsByProjectReturnsEmptyList() {
        Project project = new Project();
        project.setId(1);

        when(personDao.getPersonsByProject(project)).thenReturn(Collections.emptyList());

        assertTrue(personService.getPersonsByProject(project).isEmpty());
        verify(personDao).getPersonsByProject(project);
    }
}
