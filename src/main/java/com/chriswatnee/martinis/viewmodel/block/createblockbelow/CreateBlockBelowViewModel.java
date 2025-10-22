/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.viewmodel.block.createblockbelow;

import com.chriswatnee.martinis.commandmodel.block.createblockbelow.CreateBlockBelowCommandModel;
import com.chriswatnee.martinis.viewmodel.block.createblock.CreatePersonViewModel;
import java.util.List;

/**
 *
 * @author chris
 */
public class CreateBlockBelowViewModel {
    
    private int sceneId;
    private List<CreatePersonViewModel> persons;
    
    // Specifically to handle redisplaying when validation errors happen
    private CreateBlockBelowCommandModel createBlockBelowCommandModel;

    public CreateBlockBelowCommandModel getCreateBlockBelowCommandModel() {
        return createBlockBelowCommandModel;
    }

    public void setCreateBlockBelowCommandModel(CreateBlockBelowCommandModel createBlockBelowCommandModel) {
        this.createBlockBelowCommandModel = createBlockBelowCommandModel;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public List<CreatePersonViewModel> getPersons() {
        return persons;
    }

    public void setPersons(List<CreatePersonViewModel> persons) {
        this.persons = persons;
    }
    
}
