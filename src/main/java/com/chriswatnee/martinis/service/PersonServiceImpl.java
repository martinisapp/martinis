/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.PersonDao;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import java.util.List;
import jakarta.inject.Inject;

/**
 *
 * @author chris
 */
public class PersonServiceImpl implements PersonService {

    PersonDao personDao;

    @Inject
    public PersonServiceImpl(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public Person create(Person person) {
        return personDao.create(person);
    }

    @Override
    public Person read(Integer id) {
        return personDao.read(id);
    }

    @Override
    public void update(Person person) {
        personDao.update(person);
    }

    @Override
    public void delete(Person person) {
        personDao.delete(person);
    }

    @Override
    public List<Person> list() {
        return personDao.list();
    }

    @Override
    public List<Person> getPersonsByProject(Project project) {
        return personDao.getPersonsByProject(project);
    }
    
}
