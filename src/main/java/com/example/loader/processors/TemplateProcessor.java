package com.example.loader.processors;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateProcessor {

    private final MustacheFactory mf = new DefaultMustacheFactory();

    /**
     * Processes a Mustache template by replacing placeholders with provided variables.
     *
     * @param templatePath the file path to the Mustache template
     * @param variables the object containing key-value pairs for replacement
     * @return the processed template as a string
     * @throws IOException if the file cannot be read
     */
    public String processTemplate(String templatePath, Object variables) throws IOException {
        Path filePath = Path.of(templatePath);

        String markdownContent = Files.readString(filePath);

        Mustache mustache = mf.compile(new StringReader(markdownContent), "template");

        StringWriter writer = new StringWriter();
        mustache.execute(writer, variables).close();

        return writer.toString();
    }
}
