/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.webservice;

import com.martinis.commandmodel.scene.createscene.CreateSceneCommandModel;
import com.martinis.commandmodel.scene.createscenebelow.CreateSceneBelowCommandModel;
import com.martinis.commandmodel.scene.editscene.EditSceneCommandModel;
import com.martinis.dto.Scene;
import com.martinis.viewmodel.scene.createscene.CreateSceneViewModel;
import com.martinis.viewmodel.scene.createscenebelow.CreateSceneBelowViewModel;
import com.martinis.viewmodel.scene.editscene.EditSceneViewModel;
import com.martinis.viewmodel.scene.sceneprofile.SceneProfileViewModel;

/**
 *
 * @author chris
 */
public interface SceneWebService {
    
    public SceneProfileViewModel getSceneProfileViewModel(Integer id);

    public CreateSceneViewModel getCreateSceneViewModel(Integer projectId);
    public CreateSceneBelowViewModel getCreateSceneBelowViewModel(Integer id);
    public EditSceneViewModel getEditSceneViewModel(Integer id);

    public Scene saveCreateSceneCommandModel(CreateSceneCommandModel createSceneCommandModel);
    public Scene saveCreateSceneBelowCommandModel(CreateSceneBelowCommandModel createSceneBelowCommandModel);
    public Scene saveEditSceneCommandModel(EditSceneCommandModel editSceneCommandModel);

    public Scene deleteScene(Integer id);
    public Scene moveSceneUp(Integer id);
    public Scene moveSceneDown(Integer id);
    
}
