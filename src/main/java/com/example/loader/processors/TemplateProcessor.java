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

    public String processTemplate(String templatePath, Object variables) throws IOException {
        // Path to your markdown file
        Path filePath = Path.of(templatePath);

        // Read file content
        String markdownContent = Files.readString(filePath);

        Mustache mustache = mf.compile(new StringReader(markdownContent), "template");

        // Placeholder values
        StringWriter writer = new StringWriter();
        mustache.execute(writer, variables).close();

        return writer.toString();
    }
}
