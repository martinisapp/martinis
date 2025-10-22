package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementation of WordImportService for importing screenplay Word documents
 *
 * @author martinis
 */
@Service
public class WordImportServiceImpl implements WordImportService {

    @Inject
    private ProjectService projectService;

    @Inject
    private SceneService sceneService;

    @Inject
    private BlockService blockService;

    @Inject
    private PersonService personService;

    // Pattern to detect scene headings (e.g., "INT. HOUSE - DAY", "EXT. STREET - NIGHT")
    private static final Pattern SCENE_HEADING_PATTERN = Pattern.compile("^(INT\\.|EXT\\.|INT/EXT\\.).*", Pattern.CASE_INSENSITIVE);

    // Pattern to detect all caps text (potential character names or scene headings)
    private static final Pattern ALL_CAPS_PATTERN = Pattern.compile("^[A-Z][A-Z0-9\\s.,'\\-()]+$");

    @Override
    public Project importWordDocument(MultipartFile file, String projectTitle) throws Exception {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".docx")) {
            throw new IllegalArgumentException("Only .docx files are supported");
        }

        // Create the project
        Project project = new Project();
        project.setTitle(projectTitle != null && !projectTitle.trim().isEmpty() ? projectTitle : "Imported Project");
        project = projectService.create(project);

        // Parse the Word document
        try (InputStream inputStream = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(inputStream);
            parseDocument(document, project);
            document.close();
        }

        return project;
    }

    private void parseDocument(XWPFDocument document, Project project) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        Scene currentScene = null;
        int sceneOrder = 1;
        int blockOrder = 1;
        Person lastCharacter = null;
        Map<String, Person> characterCache = new HashMap<>();

        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText().trim();

            // Skip empty paragraphs
            if (text.isEmpty()) {
                continue;
            }

            // Check if this is a scene heading
            if (isSceneHeading(text)) {
                // Create new scene
                Scene scene = new Scene();
                scene.setName(text);
                scene.setOrder(sceneOrder++);
                scene.setProject(project);
                currentScene = sceneService.create(scene);
                blockOrder = 1; // Reset block order for new scene
                lastCharacter = null;
            } else if (currentScene != null) {
                // Check if this is a character name
                if (isCharacterName(text)) {
                    String characterName = text.trim();
                    lastCharacter = getOrCreateCharacter(characterName, project, characterCache);
                } else {
                    // This is dialogue or action
                    Block block = new Block();
                    block.setContent(text);
                    block.setScene(currentScene);
                    block.setOrder(blockOrder++);

                    // If we have a last character, this is likely dialogue
                    if (lastCharacter != null) {
                        block.setPerson(lastCharacter);
                    }

                    blockService.create(block);

                    // Clear last character if this doesn't look like continued dialogue
                    if (text.length() > 60 || !lastCharacter.getName().equals(getPreviousCharacterName(text))) {
                        lastCharacter = null;
                    }
                }
            } else {
                // No scene yet - create a default scene
                Scene scene = new Scene();
                scene.setName("Scene " + sceneOrder);
                scene.setOrder(sceneOrder++);
                scene.setProject(project);
                currentScene = sceneService.create(scene);
                blockOrder = 1;

                // Add the text as a block
                Block block = new Block();
                block.setContent(text);
                block.setScene(currentScene);
                block.setOrder(blockOrder++);
                blockService.create(block);
            }
        }
    }

    /**
     * Determines if a line of text is a scene heading
     */
    private boolean isSceneHeading(String text) {
        // Check if it matches scene heading pattern (INT., EXT., etc.)
        if (SCENE_HEADING_PATTERN.matcher(text).matches()) {
            return true;
        }

        // Check if it's all caps and relatively short (likely a scene heading)
        if (ALL_CAPS_PATTERN.matcher(text).matches() && text.length() < 80) {
            // Additional check: scene headings often contain certain keywords
            String upperText = text.toUpperCase();
            if (upperText.contains("INT") || upperText.contains("EXT") ||
                    upperText.contains("DAY") || upperText.contains("NIGHT") ||
                    upperText.contains("MORNING") || upperText.contains("EVENING")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if a line of text is a character name
     */
    private boolean isCharacterName(String text) {
        // Character names are typically:
        // 1. All caps
        // 2. Short (usually < 30 characters)
        // 3. Not a scene heading
        if (text.length() > 30) {
            return false;
        }

        if (isSceneHeading(text)) {
            return false;
        }

        // Check if mostly uppercase (allowing for some punctuation)
        return ALL_CAPS_PATTERN.matcher(text).matches();
    }

    /**
     * Gets an existing character or creates a new one
     */
    private Person getOrCreateCharacter(String characterName, Project project, Map<String, Person> cache) {
        // Clean up character name (remove parentheticals like "(V.O.)")
        String cleanName = characterName.replaceAll("\\(.*?\\)", "").trim();

        // Check cache first
        if (cache.containsKey(cleanName)) {
            return cache.get(cleanName);
        }

        // Create new character
        Person person = new Person();
        person.setName(cleanName);
        person.setFullName(cleanName);
        person.setProject(project);
        person = personService.create(person);

        cache.put(cleanName, person);
        return person;
    }

    /**
     * Helper method to check if dialogue continues (not implemented in basic version)
     */
    private String getPreviousCharacterName(String text) {
        return "";
    }
}
