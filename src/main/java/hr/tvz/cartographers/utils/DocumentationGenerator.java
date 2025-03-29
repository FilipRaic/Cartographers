package hr.tvz.cartographers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentationGenerator {

    public static void generateDocumentation() {
        String htmlPageStart = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Cartographers Code Documentation</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            padding: 20px;
                        }
                
                        h2 {
                            color: #2c3e50;
                            border-bottom: 2px solid #2c3e50;
                            padding-bottom: 5px;
                        }
                
                        h3 {
                            color: #34495e;
                            margin-top: 15px;
                        }
                
                        ul {
                            list-style-type: none;
                            padding-left: 20px;
                        }
                
                        li {
                            margin: 5px 0;
                        }
                
                        .class-block {
                            margin-bottom: 20px;
                            padding: 10px;
                            border: 1px solid #ccc;
                            border-radius: 5px;
                            background-color: #f9f9f9;
                        }
                
                        code {
                            background-color: #eee;
                            padding: 2px 5px;
                            border-radius: 3px;
                        }
                
                        .enum-list, .field-list, .constructor-list, .getter-setter-list, .method-list {
                            padding-left: 10px;
                        }
                    </style>
                </head>
                <body>
                <div>
                    <h1>Cartographers Code Documentation</h1>
                </div>
                """;

        String htmlPageEnd = """
                </body>
                </html>
                """;

        StringBuilder htmlBodyBuilder = new StringBuilder();

        String classPath = "./target/classes/";

        try (Stream<Path> pathStream = Files.walk(Path.of(classPath))) {
            List<Path> classFiles = pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().endsWith("module-info.class")) // Ignore module-info files
                    .toList();

            for (Path p : classFiles) {
                String fullClassName = p.toString()
                        .substring(17, p.toString().length() - 6)
                        .replace("\\", ".")
                        .replace("/", ".");

                try {
                    Class<?> reflectionClass = Class.forName(fullClassName);
                    documentClass(reflectionClass, htmlBodyBuilder);
                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + fullClassName);
                }
            }

            String htmlFileContent = htmlPageStart + htmlBodyBuilder + htmlPageEnd;
            Path documentationFilePath = Path.of("docs/documentation.html");
            writeToDocumentationFile(documentationFilePath, htmlFileContent);
            openInBrowser(documentationFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void documentClass(Class<?> clazz, StringBuilder htmlBodyBuilder) {
        boolean isEnum = clazz.isEnum();
        String modifiers = Modifier.toString(clazz.getModifiers());

        // Remove "final" if it's an enum
        if (isEnum) {
            modifiers = modifiers.replace("final", "").trim();
        }

        htmlBodyBuilder.append("\n<div class='class-block'>\n")
                .append("\t<h2>")
                .append(modifiers.isEmpty() ? "" : modifiers + " ")  // Add modifiers only if present
                .append(isEnum ? "enum " : "class ")
                .append(clazz.getSimpleName())
                .append("</h2>\n");

        // Superclass (Exclude for enums)
        if (!isEnum && clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            htmlBodyBuilder.append("\t<p><strong>Extends:</strong> ")
                    .append(clazz.getSuperclass().getSimpleName())
                    .append("</p>\n");
        }

        // Implemented Interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            htmlBodyBuilder.append("\t<p><strong>Implements:</strong> ");
            for (Class<?> i : interfaces) {
                htmlBodyBuilder.append(i.getSimpleName()).append(", ");
            }
            htmlBodyBuilder.setLength(htmlBodyBuilder.length() - 2); // Remove last comma
            htmlBodyBuilder.append("</p>\n");
        }

        // Enum Constants
        if (isEnum) {
            htmlBodyBuilder.append("\t<h3>Enum Constants</h3>\n<ul class='enum-list'>");
            Object[] enumConstants = clazz.getEnumConstants();
            for (Object constant : enumConstants) {
                htmlBodyBuilder.append("<li><code>").append(constant.toString()).append("</code></li>\n");
            }
            htmlBodyBuilder.append("</ul>\n");
        }

        // Fields
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            htmlBodyBuilder.append("\t<h3>Fields</h3>\n<ul class='field-list'>");
            for (Field field : fields) {
                htmlBodyBuilder.append("<li><code>")
                        .append(Modifier.toString(field.getModifiers())).append(" ")
                        .append(field.getType().getSimpleName()).append(" ")
                        .append(field.getName())
                        .append("</code></li>\n");
            }
            htmlBodyBuilder.append("</ul>\n");
        }

        // Constructors (Exclude for enums)
        if (!isEnum) {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 0) {
                htmlBodyBuilder.append("\t<h3>Constructors</h3>\n<ul class='constructor-list'>");
                for (Constructor<?> constructor : constructors) {
                    htmlBodyBuilder.append("<li><code>")
                            .append(Modifier.toString(constructor.getModifiers())).append(" ")
                            .append(constructor.getName()).append("(");
                    appendParameters(constructor.getParameters(), htmlBodyBuilder);
                    htmlBodyBuilder.append(")</code></li>\n");
                }
                htmlBodyBuilder.append("</ul>\n");
            }
        }

        // Categorize methods into Getters/Setters and Other Methods
        List<String> gettersSetters = new ArrayList<>();
        List<String> otherMethods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            String methodSignature = "<li><code>" + Modifier.toString(method.getModifiers()) + " "
                    + method.getReturnType().getSimpleName() + " "
                    + method.getName() + "(";
            appendParameters(method.getParameters(), new StringBuilder(methodSignature));
            methodSignature += ")</code></li>\n";

            if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
                gettersSetters.add(methodSignature);
            } else {
                otherMethods.add(methodSignature);
            }
        }

        // Getters and Setters
        if (!gettersSetters.isEmpty()) {
            htmlBodyBuilder.append("\t<h3>Getters & Setters</h3>\n<ul class='getter-setter-list'>");
            for (String method : gettersSetters) {
                htmlBodyBuilder.append(method);
            }
            htmlBodyBuilder.append("</ul>\n");
        }

        // Other Methods
        if (!otherMethods.isEmpty()) {
            htmlBodyBuilder.append("\t<h3>Other Methods</h3>\n<ul class='method-list'>");
            for (String method : otherMethods) {
                htmlBodyBuilder.append(method);
            }
            htmlBodyBuilder.append("</ul>\n");
        }

        htmlBodyBuilder.append("</div>\n");
    }


    private static void appendParameters(Parameter[] parameters, StringBuilder builder) {
        for (int i = 0; i < parameters.length; i++) {
            builder.append(parameters[i].getType().getSimpleName()).append(" ")
                    .append(parameters[i].getName());
            if (i < parameters.length - 1) {
                builder.append(", ");
            }
        }
    }

    private static void writeToDocumentationFile(Path documentationFilePath, String htmlFileContent) throws IOException {
        try {
            Files.createDirectories(documentationFilePath.getParent());
            Files.createFile(documentationFilePath);
            Files.writeString(documentationFilePath, htmlFileContent);
        } catch (FileAlreadyExistsException e) {
            try {
                Files.createFile(documentationFilePath);
                Files.writeString(documentationFilePath, htmlFileContent);
            } catch (FileAlreadyExistsException e1) {
                Files.writeString(documentationFilePath, htmlFileContent);
            }
        }
    }

    private static void openInBrowser(Path filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(filePath.toUri()); // Open in default browser
            } else {
                System.err.println("Desktop browsing is not supported on this system.");
            }
        } catch (Exception e) {
            System.err.println("Failed to open documentation in browser: " + e.getMessage());
        }
    }
}
