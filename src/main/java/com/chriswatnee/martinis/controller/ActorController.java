/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.controller;

import com.chriswatnee.martinis.commandmodel.actor.createactor.CreateActorCommandModel;
import com.chriswatnee.martinis.commandmodel.actor.editactor.EditActorCommandModel;
import com.chriswatnee.martinis.dto.Actor;
import com.chriswatnee.martinis.viewmodel.actor.actorlist.ActorListViewModel;
import com.chriswatnee.martinis.viewmodel.actor.actorprofile.ActorProfileViewModel;
import com.chriswatnee.martinis.viewmodel.actor.createactor.CreateActorViewModel;
import com.chriswatnee.martinis.viewmodel.actor.editactor.EditActorViewModel;
import com.chriswatnee.martinis.webservice.ActorWebService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
@RequestMapping(value = "/actor")
public class ActorController {
    
    @Inject
    ActorWebService actorWebService;
    
    @RequestMapping(value = "/list")
    public String list(Model model) {

        ActorListViewModel viewModel = actorWebService.getActorListViewModel();

        model.addAttribute("viewModel", viewModel);

        return "actor/list";
    }
    
    @RequestMapping(value = "/show")
    public String show(@RequestParam Integer id, Model model) {

        ActorProfileViewModel viewModel = actorWebService.getActorProfileViewModel(id);

        model.addAttribute("viewModel", viewModel);

        return "actor/show";
    }
    
    @RequestMapping(value = "/delete")
    public String delete(@RequestParam Integer id) {
        
        actorWebService.deleteActor(id);
        
        return "redirect:/actor/list";
    }
    
    // Show Form
    @RequestMapping(value = "/edit")
    public String edit(@RequestParam Integer id, Model model) {

        EditActorViewModel viewModel = actorWebService.getEditActorViewModel(id);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getEditActorCommandModel());

        return "actor/edit";
    }

    // Handle Form Submission
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit(@Valid @ModelAttribute("commandModel") EditActorCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            EditActorViewModel viewModel = actorWebService.getEditActorViewModel(commandModel.getId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "actor/edit";
        }

        Actor actor = actorWebService.saveEditActorCommandModel(commandModel);

        return "redirect:/actor/show?id=" + actor.getId();
    }
    
    // Show Form
    @RequestMapping(value = "/create")
    public String create(Model model) {

        CreateActorViewModel viewModel = actorWebService.getCreateActorViewModel();

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getCreateActorCommandModel());

        return "actor/create";
    }

    // Handle Form Submission
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String saveCreate(@Valid @ModelAttribute("commandModel") CreateActorCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            CreateActorViewModel viewModel = actorWebService.getCreateActorViewModel();

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "actor/create";
        }

        Actor actor = actorWebService.saveCreateActorCommandModel(commandModel);

        return "redirect:/actor/show?id=" + actor.getId();
    }
}
