/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.webservice;

import com.martinis.commandmodel.person.createperson.CreatePersonCommandModel;
import com.martinis.commandmodel.person.editperson.EditPersonCommandModel;
import com.martinis.dto.Person;
import com.martinis.viewmodel.person.createperson.CreatePersonViewModel;
import com.martinis.viewmodel.person.editperson.EditPersonViewModel;
import com.martinis.viewmodel.person.personprofile.PersonProfileViewModel;

/**
 *
 * @author chris
 */
public interface PersonWebService {
    
    public PersonProfileViewModel getPersonProfileViewModel(Integer id);

    public CreatePersonViewModel getCreatePersonViewModel(Integer projectId);
    public EditPersonViewModel getEditPersonViewModel(Integer id);

    public Person saveCreatePersonCommandModel(CreatePersonCommandModel createPersonCommandModel);
    public Person saveEditPersonCommandModel(EditPersonCommandModel editPersonCommandModel);

    public Person deletePerson(Integer id);
    
}
