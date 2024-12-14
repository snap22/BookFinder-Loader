package com.example.loader.processors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateProcessorTest {

    private final TemplateProcessor templateProcessor = new TemplateProcessor();

    private final Path resourcesDir = Paths.get("src", "test", "resources");
    private final Path tmpDir = resourcesDir.resolve("tmp");
    private final Path templatesDir = resourcesDir.resolve("templates");

    private Path createTemplateFile(String filename, String content) throws IOException {
        Path templatePath = tmpDir.resolve(filename);
        Files.writeString(templatePath, content);
        return templatePath;
    }

    @BeforeEach
    void setUp() throws IOException {
        if (!Files.exists(tmpDir))
            Files.createDirectory(tmpDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tmpDir)
                .filter(Files::isRegularFile) // Only files
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete file: " + file, e);
                    }
                });
    }

    @Test
    void testProcessTemplateWithValidVariables() throws IOException {
        Path templatePath = createTemplateFile("template.md", "Hello, {{name}}! Welcome to {{location}}.");
        Map<String, String> variables = Map.of(
                "name", "John",
                "location", "GitHub"
        );

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("Hello, John! Welcome to GitHub.", result);
    }

    @Test
    public void testProcessTemplateWithUnusedVariables() throws IOException {
        // Arrange
        Path templatePath = createTemplateFile("template.md", "Hello, {{name}}! Welcome.");
        Map<String, String> variables = Map.of(
                "name", "John",
                "location", "GitHub" // Extra variable
        );

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("Hello, John! Welcome.", result);
    }

    @Test
    public void testProcessTemplateWithMissingVariables() throws IOException {
        // Arrange
        Path templatePath = createTemplateFile("template.md", "Hello, {{name}}! Welcome to {{location}}.");
        Map<String, String> variables = Map.of(
                "name", "John" // Missing location
        );

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("Hello, John! Welcome to .", result);
    }

    @Test
    public void testProcessTemplateWithEmptyTemplate() throws IOException {
        // Arrange
        Path templatePath = createTemplateFile("empty.md", "");
        Map<String, String> variables = Map.of();

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("", result);
    }

    @Test
    public void testProcessTemplateWithNoPlaceholders() throws IOException {
        // Arrange
        Path templatePath = createTemplateFile("no_placeholders.md", "This is a static template.");
        Map<String, String> variables = Map.of(
                "name", "John",
                "location", "GitHub"
        );

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("This is a static template.", result);
    }

    @Test
    public void testProcessTemplateWithCustomDelimiter() throws IOException {
        // Arrange
        Path templatePath = createTemplateFile("unused_placeholders.md", "{{=<% %>=}}Hello, {{name}}, welcome to <%location%>!");
        Map<String, String> variables = Map.of(
                "location", "GitHub"
        );

        // Act
        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        // Assert
        assertEquals("Hello, {{name}}, welcome to GitHub!", result);
    }

    @Test
    public void testProcessTemplateWithMarkdownTemplate() throws IOException {
        Path templatePath = templatesDir.resolve("user_preview.md");
        Map<String, String> variables = Map.of(
                "username", "John",
                "userId", "12345"
        );

        String result = templateProcessor.processTemplate(templatePath.toString(), variables);

        String expected = "\n" +
                "+++\n" +
                "title = \"Preview\"\n" +
                "url = \"/John/preview/\"\n" +
                "draft = false\n" +
                "summary = \"Preview all of the user's books\"\n" +
                "+++\n" +
                "\n" +
                "### Too many books? [Search for a specific one](/John/search/)\n" +
                "{{< json-to-table-remote \"https://drive.google.com/uc?export=download&id=12345\" >}}\n";

        assertEquals(expected, result);
    }
}