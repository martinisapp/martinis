/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.scene.createscene.CreateSceneCommandModel;
import com.chriswatnee.martinis.commandmodel.scene.editscene.EditSceneCommandModel;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import com.chriswatnee.martinis.service.BlockService;
import com.chriswatnee.martinis.service.PersonService;
import com.chriswatnee.martinis.service.ProjectService;
import com.chriswatnee.martinis.service.SceneService;
import com.chriswatnee.martinis.viewmodel.scene.createscene.CreateSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.editscene.EditSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.sceneprofile.BlockViewModel;
import com.chriswatnee.martinis.viewmodel.scene.sceneprofile.SceneProfileViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author chris
 */
public class SceneWebServiceImpl implements SceneWebService {

    SceneService sceneService;
    BlockService blockService;
    ProjectService projectService;
    PersonService personService;

    @Inject
    public SceneWebServiceImpl(SceneService sceneService, BlockService blockService, ProjectService projectService, PersonService personService) {
        this.sceneService = sceneService;
        this.blockService = blockService;
        this.projectService = projectService;
        this.personService = personService;
    }
    
    @Override
    public SceneProfileViewModel getSceneProfileViewModel(Integer id) {
        
        // Instantiate
        SceneProfileViewModel sceneProfileViewModel = new SceneProfileViewModel();

        // Look up stuff
        Scene scene = sceneService.read(id);
        List<Block> blocks = blockService.getBlocksByScene(scene);

        Project project = null;
        if (scene.getProject() != null) {
            project = projectService.read(scene.getProject().getId());
        }
        
        Scene previousScene = sceneService.getPreviousScene(scene);
        Scene nextScene = sceneService.getNextScene(scene);

        // Put stuff
        sceneProfileViewModel.setId(scene.getId());
        sceneProfileViewModel.setName(scene.getName());
        
        if (project != null) {
            sceneProfileViewModel.setProjectId(project.getId());
            sceneProfileViewModel.setProjectTitle(project.getTitle());
        }
        
        if (previousScene != null) {
            sceneProfileViewModel.setPreviousSceneId(previousScene.getId());
            sceneProfileViewModel.setPreviousSceneName(previousScene.getName());
        }
        
        if (nextScene != null) {
            sceneProfileViewModel.setNextSceneId(nextScene.getId());
            sceneProfileViewModel.setNextSceneName(nextScene.getName());
        }
        
        sceneProfileViewModel.setBlocks(translateBlock(blocks));

        return sceneProfileViewModel;
    }

    @Override
    public CreateSceneViewModel getCreateSceneViewModel(Integer projectId) {

        // Instantiate
        CreateSceneViewModel createSceneViewModel = new CreateSceneViewModel();

        // Populate commmand model
        CreateSceneCommandModel commandModel = new CreateSceneCommandModel();
        commandModel.setProjectId(projectId);
        
        createSceneViewModel.setCreateSceneCommandModel(commandModel);

        // Populate
        createSceneViewModel.setProjectId(projectId);

        return createSceneViewModel;
    }

    @Override
    public EditSceneViewModel getEditSceneViewModel(Integer id) {

        // Instantiate
        EditSceneViewModel editSceneViewModel = new EditSceneViewModel();

        // Look up stuff
        Scene existingScene = sceneService.read(id);

        Project selectedProject = projectService.read(existingScene.getProject().getId());
        
        // Populate
        editSceneViewModel.setId(id);

        // Populate commmand model
        EditSceneCommandModel commandModel = new EditSceneCommandModel();
        commandModel.setId(existingScene.getId());
        commandModel.setName(existingScene.getName());
        commandModel.setProjectId(selectedProject.getId());

        editSceneViewModel.setEditSceneCommandModel(commandModel);

        return editSceneViewModel;
    }

    @Override
    public Scene saveCreateSceneCommandModel(CreateSceneCommandModel createSceneCommandModel) {

        // Instantiate
        Scene scene = new Scene();
        
        // Look up stuff
        Project project = projectService.read(createSceneCommandModel.getProjectId());
        
        // Put stuff
        scene.setName(createSceneCommandModel.getName());

        if (project != null) {
            scene.setProject(project);
        }

        // Save stuff
        scene = sceneService.create(scene);
        
        return scene;
    }

    @Override
    public Scene saveEditSceneCommandModel(EditSceneCommandModel editSceneCommandModel) {

        // Instantiate
        Scene scene = sceneService.read(editSceneCommandModel.getId());

        // Look up stuff
        Project project = projectService.read(editSceneCommandModel.getProjectId());

        // Put stuff
        scene.setName(editSceneCommandModel.getName());
        scene.setProject(project);

        // Save stuff
        sceneService.update(scene);

        return scene;
    }
    
    @Override
    public Scene deleteScene(Integer id) {

        // Instantiate
        Scene scene = sceneService.read(id);

        // Delete
        sceneService.delete(scene);

        return scene;
    }
    
    @Override
    public Scene moveSceneUp(Integer id) {

        // Instantiate
        Scene scene = sceneService.read(id);

        // Delete
        sceneService.moveUp(scene);

        return scene;
    }
    
    @Override
    public Scene moveSceneDown(Integer id) {

        // Instantiate
        Scene scene = sceneService.read(id);

        // Delete
        sceneService.moveDown(scene);

        return scene;
    }

    private List<BlockViewModel> translateBlock(List<Block> blocks) {
        List<BlockViewModel> blockViewModels = new ArrayList<>();

        for (Block block : blocks) {
            blockViewModels.add(translateBlock(block));
        }

        return blockViewModels;
    }

    private BlockViewModel translateBlock(Block block) {

        BlockViewModel blockViewModel = new BlockViewModel();

        blockViewModel.setOrder(block.getOrder());
        blockViewModel.setContent(block.getContent());
        blockViewModel.setId(block.getId());
        
        if (block.getPerson() != null) {
            Person person = personService.read(block.getPerson().getId());

            if (person != null) {
                blockViewModel.setPersonId(person.getId());
                blockViewModel.setPersonName(person.getName());
            }

        }

        return blockViewModel;
    }
    
}
