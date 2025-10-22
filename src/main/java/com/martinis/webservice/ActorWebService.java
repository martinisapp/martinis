/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.webservice;

import com.martinis.commandmodel.actor.createactor.CreateActorCommandModel;
import com.martinis.commandmodel.actor.editactor.EditActorCommandModel;
import com.martinis.dto.Actor;
import com.martinis.viewmodel.actor.actorlist.ActorListViewModel;
import com.martinis.viewmodel.actor.actorprofile.ActorProfileViewModel;
import com.martinis.viewmodel.actor.createactor.CreateActorViewModel;
import com.martinis.viewmodel.actor.editactor.EditActorViewModel;

/**
 *
 * @author chris
 */
public interface ActorWebService {
    
    public ActorListViewModel getActorListViewModel();
    public ActorProfileViewModel getActorProfileViewModel(Integer id);

    public CreateActorViewModel getCreateActorViewModel();
    public EditActorViewModel getEditActorViewModel(Integer id);

    public Actor saveCreateActorCommandModel(CreateActorCommandModel createActorCommandModel);
    public Actor saveEditActorCommandModel(EditActorCommandModel editActorCommandModel);

    public Actor deleteActor(Integer id);
    
}
