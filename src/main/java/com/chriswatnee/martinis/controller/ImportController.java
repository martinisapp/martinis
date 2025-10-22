package com.chriswatnee.martinis.controller;

import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.service.WordImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

/**
 * Controller for importing screenplay files
 *
 * @author martinis
 */
@Controller
@RequestMapping(value = "/import")
public class ImportController {

    @Inject
    private WordImportService wordImportService;

    /**
     * Show the import form
     */
    @RequestMapping(value = "/word")
    public String showImportForm(Model model) {
        return "import/word";
    }

    /**
     * Handle Word file upload and import
     */
    @RequestMapping(value = "/word", method = RequestMethod.POST)
    public String importWord(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectTitle") String projectTitle,
            Model model) {

        try {
            // Validate inputs
            if (file == null || file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                return "import/word";
            }

            if (projectTitle == null || projectTitle.trim().isEmpty()) {
                model.addAttribute("error", "Please enter a project title");
                return "import/word";
            }

            // Import the document
            Project project = wordImportService.importWordDocument(file, projectTitle);

            // Redirect to the project page
            return "redirect:/project/show?id=" + project.getId();

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid file: " + e.getMessage());
            return "import/word";
        } catch (Exception e) {
            model.addAttribute("error", "Error importing file: " + e.getMessage());
            e.printStackTrace();
            return "import/word";
        }
    }
}
