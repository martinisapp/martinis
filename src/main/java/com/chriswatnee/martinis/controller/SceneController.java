/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.controller;

import com.chriswatnee.martinis.commandmodel.scene.createscene.CreateSceneCommandModel;
import com.chriswatnee.martinis.commandmodel.scene.editscene.EditSceneCommandModel;
import com.chriswatnee.martinis.dto.Scene;
import com.chriswatnee.martinis.viewmodel.scene.createscene.CreateSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.editscene.EditSceneViewModel;
import com.chriswatnee.martinis.viewmodel.scene.sceneprofile.SceneProfileViewModel;
import com.chriswatnee.martinis.webservice.SceneWebService;
import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author chris
 */
@Controller
@RequestMapping(value = "/scene")
public class SceneController {
    
    @Inject
    SceneWebService sceneWebService;
    
    @RequestMapping(value = "/show")
    public String show(@RequestParam Integer id, Model model) {

        SceneProfileViewModel viewModel = sceneWebService.getSceneProfileViewModel(id);

        model.addAttribute("viewModel", viewModel);

        return "scene/show";
    }
    
    @RequestMapping(value = "/delete")
    public String delete(@RequestParam Integer id) {
        
        Scene scene = sceneWebService.deleteScene(id);
        
        return "redirect:/project/show?id=" + scene.getProject().getId();
    }
    
    @RequestMapping(value = "/moveUp")
    public String moveUp(@RequestParam Integer id) {
        
        Scene scene = sceneWebService.moveSceneUp(id);
        
        return "redirect:/project/show?id=" + scene.getProject().getId();
    }
    
    @RequestMapping(value = "/moveDown")
    public String moveDown(@RequestParam Integer id) {
        
        Scene scene = sceneWebService.moveSceneDown(id);
        
        return "redirect:/project/show?id=" + scene.getProject().getId();
    }
    
    // Show Form
    @RequestMapping(value = "/edit")
    public String edit(@RequestParam Integer id, Model model) {

        EditSceneViewModel viewModel = sceneWebService.getEditSceneViewModel(id);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getEditSceneCommandModel());

        return "scene/edit";
    }

    // Handle Form Submission
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit(@Valid @ModelAttribute("commandModel") EditSceneCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            EditSceneViewModel viewModel = sceneWebService.getEditSceneViewModel(commandModel.getId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "scene/edit";
        }

        Scene scene = sceneWebService.saveEditSceneCommandModel(commandModel);

        return "redirect:/scene/show?id=" + scene.getId();
    }
    
    // Show Form
    @RequestMapping(value = "/create")
    public String create(@RequestParam Integer projectId, Model model) {

        CreateSceneViewModel viewModel = sceneWebService.getCreateSceneViewModel(projectId);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getCreateSceneCommandModel());

        return "scene/create";
    }

    // Handle Form Submission
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String saveCreate(@Valid @ModelAttribute("commandModel") CreateSceneCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            CreateSceneViewModel viewModel = sceneWebService.getCreateSceneViewModel(commandModel.getProjectId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "scene/create";
        }

        Scene scene = sceneWebService.saveCreateSceneCommandModel(commandModel);

        return "redirect:/scene/show?id=" + scene.getId();
    }
}
