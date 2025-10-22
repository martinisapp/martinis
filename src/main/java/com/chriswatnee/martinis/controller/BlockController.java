/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.controller;

import com.chriswatnee.martinis.commandmodel.block.createblock.CreateBlockCommandModel;
import com.chriswatnee.martinis.commandmodel.block.createblockbelow.CreateBlockBelowCommandModel;
import com.chriswatnee.martinis.commandmodel.block.editblock.EditBlockCommandModel;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.viewmodel.block.createblock.CreateBlockViewModel;
import com.chriswatnee.martinis.viewmodel.block.createblockbelow.CreateBlockBelowViewModel;
import com.chriswatnee.martinis.viewmodel.block.editblock.EditBlockViewModel;
import com.chriswatnee.martinis.webservice.BlockWebService;
import java.util.LinkedList;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author chris
 */
@Controller
@RequestMapping(value = "/block")
public class BlockController {
    
    @Inject
    BlockWebService blockWebService;
    
    @RequestMapping(value = "/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes redirectAttributes) {

        Block block = blockWebService.deleteBlock(id);

        // Get or create the undo stack in session
        @SuppressWarnings("unchecked")
        LinkedList<Block> deletedBlocksStack = (LinkedList<Block>) session.getAttribute("deletedBlocksStack");
        if (deletedBlocksStack == null) {
            deletedBlocksStack = new LinkedList<>();
            session.setAttribute("deletedBlocksStack", deletedBlocksStack);
        }

        // Add deleted block to the stack
        deletedBlocksStack.push(block);

        // Add flash attribute to show undo notification with count
        redirectAttributes.addFlashAttribute("blockDeleted", true);
        redirectAttributes.addFlashAttribute("undoCount", deletedBlocksStack.size());

        return "redirect:/scene/show?id=" + block.getScene().getId();
    }

    @RequestMapping(value = "/undo")
    public String undo(HttpSession session, RedirectAttributes redirectAttributes) {

        // Get the undo stack from session
        @SuppressWarnings("unchecked")
        LinkedList<Block> deletedBlocksStack = (LinkedList<Block>) session.getAttribute("deletedBlocksStack");

        if (deletedBlocksStack != null && !deletedBlocksStack.isEmpty()) {
            // Pop the most recently deleted block
            Block deletedBlock = deletedBlocksStack.pop();
            Integer sceneId = deletedBlock.getScene().getId();
            blockWebService.restoreBlock(deletedBlock);

            // If there are more blocks to undo, keep showing the undo notification
            if (!deletedBlocksStack.isEmpty()) {
                redirectAttributes.addFlashAttribute("blockDeleted", true);
                redirectAttributes.addFlashAttribute("undoCount", deletedBlocksStack.size());
            }

            return "redirect:/scene/show?id=" + sceneId;
        }

        // If no deleted blocks in session, redirect to project list
        return "redirect:/project/list";
    }
    
    @RequestMapping(value = "/moveUp")
    public String moveUp(@RequestParam Integer id) {
        
        Block block = blockWebService.moveBlockUp(id);
        
        return "redirect:/scene/show?id=" + block.getScene().getId();
    }
    
    @RequestMapping(value = "/moveDown")
    public String moveDown(@RequestParam Integer id) {

        Block block = blockWebService.moveBlockDown(id);

        return "redirect:/scene/show?id=" + block.getScene().getId();
    }

    @RequestMapping(value = "/reorder", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> reorder(@RequestBody List<Integer> blockIds) {
        try {
            blockWebService.reorderBlocks(blockIds);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // htmx endpoint - get edit form
    @RequestMapping(value = "/editForm", method = RequestMethod.GET)
    public String getEditForm(@RequestParam Integer id, Model model) {
        Block block = blockWebService.getBlock(id);
        model.addAttribute("block", block);
        model.addAttribute("persons", blockWebService.getPersonsForScene(block.getScene().getId()));
        return "fragments/block-edit-form";
    }

    // htmx endpoint - get display view (for canceling edits)
    @RequestMapping(value = "/displayView", method = RequestMethod.GET)
    public String getDisplayView(@RequestParam Integer id, Model model) {
        Block block = blockWebService.getBlock(id);
        model.addAttribute("block", block);
        return "fragments/block-display";
    }

    // htmx endpoint - update block and return display view
    @RequestMapping(value = "/updateInline", method = RequestMethod.POST)
    public String updateInline(@ModelAttribute EditBlockCommandModel commandModel, Model model) {
        if (commandModel.getContent() == null || commandModel.getContent().trim().isEmpty()) {
            return "error";
        }
        Block block = blockWebService.saveEditBlockCommandModel(commandModel);
        model.addAttribute("block", block);
        return "fragments/block-display";
    }

    // Legacy JSON endpoint for backwards compatibility
    @RequestMapping(value = "/updateInlineJson", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateInlineJson(@RequestBody EditBlockCommandModel commandModel) {
        try {
            if (commandModel.getContent() == null || commandModel.getContent().trim().isEmpty()) {
                return new ResponseEntity<>("Content cannot be empty", HttpStatus.BAD_REQUEST);
            }
            blockWebService.saveEditBlockCommandModel(commandModel);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Show Form
    @RequestMapping(value = "/edit")
    public String edit(@RequestParam Integer id, Model model) {

        EditBlockViewModel viewModel = blockWebService.getEditBlockViewModel(id);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getEditBlockCommandModel());

        return "block/edit";
    }

    // Handle Form Submission
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit(@Valid @ModelAttribute("commandModel") EditBlockCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            EditBlockViewModel viewModel = blockWebService.getEditBlockViewModel(commandModel.getId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "block/edit";
        }

        Block block = blockWebService.saveEditBlockCommandModel(commandModel);

        return "redirect:/scene/show?id=" + block.getScene().getId();
    }
    
    // Show Form
    @RequestMapping(value = "/create")
    public String create(@RequestParam Integer sceneId, Model model) {

        CreateBlockViewModel viewModel = blockWebService.getCreateBlockViewModel(sceneId);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getCreateBlockCommandModel());

        return "block/create";
    }

    // Handle Form Submission
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String saveCreate(@Valid @ModelAttribute("commandModel") CreateBlockCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            CreateBlockViewModel viewModel = blockWebService.getCreateBlockViewModel(commandModel.getSceneId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "block/create";
        }

        Block block = blockWebService.saveCreateBlockCommandModel(commandModel);

        return "redirect:/scene/show?id=" + block.getScene().getId();
    }
    
    // Show Form
    @RequestMapping(value = "/createBelow")
    public String createBelow(@RequestParam Integer id, Model model) {

        CreateBlockBelowViewModel viewModel = blockWebService.getCreateBlockBelowViewModel(id);

        model.addAttribute("viewModel", viewModel);
        model.addAttribute("commandModel", viewModel.getCreateBlockBelowCommandModel());

        return "block/createBelow";
    }

    // Handle Form Submission
    @RequestMapping(value = "/createBelow", method = RequestMethod.POST)
    public String saveCreateBelow(@Valid @ModelAttribute("commandModel") CreateBlockBelowCommandModel commandModel, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            CreateBlockBelowViewModel viewModel = blockWebService.getCreateBlockBelowViewModel(commandModel.getSceneId());

            model.addAttribute("viewModel", viewModel);
            model.addAttribute("commandModel", commandModel);

            return "block/createBelow";
        }

        Block block = blockWebService.saveCreateBlockBelowCommandModel(commandModel);

        return "redirect:/scene/show?id=" + block.getScene().getId();
    }

    // Test endpoint to verify JSON is working
    @RequestMapping(value = "/testJson", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> testJson(@RequestBody String rawBody) {
        System.out.println("=== TEST JSON ENDPOINT ===");
        System.out.println("Raw body received: " + rawBody);
        return new ResponseEntity<>("Received: " + rawBody, HttpStatus.OK);
    }

    // AJAX endpoint for inline block creation
    @RequestMapping(value = "/createInline", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createInline(@RequestBody CreateBlockCommandModel commandModel) {
        try {
            System.out.println("=== DEBUG /createInline ===");
            System.out.println("  content: " + commandModel.getContent());
            System.out.println("  personId: " + commandModel.getPersonId());
            System.out.println("  sceneId: " + commandModel.getSceneId());

            if (commandModel.getContent() == null || commandModel.getContent().trim().isEmpty()) {
                return new ResponseEntity<>("Content cannot be empty", HttpStatus.BAD_REQUEST);
            }
            if (commandModel.getSceneId() == null) {
                return new ResponseEntity<>("Scene ID is required", HttpStatus.BAD_REQUEST);
            }
            Block block = blockWebService.saveCreateBlockCommandModel(commandModel);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("=== ERROR in /createInline ===");
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // AJAX endpoint for inline block creation below existing block
    @RequestMapping(value = "/createBelowInline", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createBelowInline(@RequestBody CreateBlockBelowCommandModel commandModel) {
        try {
            System.out.println("=== DEBUG /createBelowInline ===");
            System.out.println("  id: " + commandModel.getId());
            System.out.println("  content: " + commandModel.getContent());
            System.out.println("  personId: " + commandModel.getPersonId());
            System.out.println("  sceneId: " + commandModel.getSceneId());

            if (commandModel.getContent() == null || commandModel.getContent().trim().isEmpty()) {
                return new ResponseEntity<>("Content cannot be empty", HttpStatus.BAD_REQUEST);
            }
            if (commandModel.getId() == null) {
                return new ResponseEntity<>("Block ID is required for createBelow", HttpStatus.BAD_REQUEST);
            }
            Block block = blockWebService.saveCreateBlockBelowCommandModel(commandModel);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("=== ERROR in /createBelowInline ===");
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
