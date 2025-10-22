package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dto.Project;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for importing Word documents into Martinis projects
 *
 * @author martinis
 */
public interface WordImportService {

    /**
     * Imports a Word document (.docx) and creates a new project with scenes and blocks
     *
     * @param file The uploaded Word document file
     * @param projectTitle The title for the new project
     * @return The created Project
     * @throws Exception if import fails
     */
    public Project importWordDocument(MultipartFile file, String projectTitle) throws Exception;

}
