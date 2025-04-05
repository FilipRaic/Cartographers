package hr.tvz.cartographers.documentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static hr.tvz.cartographers.documentation.DocumentationConstants.*;
import static hr.tvz.cartographers.documentation.DocumentationUtil.openInBrowser;
import static hr.tvz.cartographers.documentation.DocumentationUtil.writeToFile;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentationGenerator {

    public static void generateDocumentation() {
        StringBuilder htmlBody = new StringBuilder();
        try (Stream<Path> paths = Files.walk(Path.of(CLASS_PATH))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class") && !p.toString().endsWith("module-info.class"))
                    .forEach(p -> processClassFile(p, htmlBody));

            String htmlContent = HTML_START + htmlBody + HTML_END;
            Path docPath = Path.of(DOCUMENTATION_HTML_PATH);
            writeToFile(docPath, htmlContent);
            openInBrowser(docPath);
        } catch (IOException e) {
            log.error("Error occurred when generating documentation: ", e);
        }
    }

    private static void processClassFile(Path path, StringBuilder htmlBody) {
        String className = path.toString()
                .substring(17, path.toString().length() - 6)
                .replace("\\", ".")
                .replace("/", ".");

        try {
            Class<?> clazz = Class.forName(className);
            documentClass(clazz, htmlBody);
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", className);
        }
    }

    private static void documentClass(Class<?> clazz, StringBuilder html) {
        html.append(CLASS_BLOCK_START)
                .append("\t<h2>")
                .append(getModifiers(clazz))
                .append(clazz.isEnum() ? "enum " : "class ")
                .append(getClassNameWithExtendsAndImplements(clazz))
                .append("</h2>\n");

        appendEnumConstants(clazz, html);
        appendFields(clazz, html);
        if (!clazz.isEnum()) {
            appendConstructors(clazz, html);
        }

        appendMethods(clazz, html);
        html.append("</div>\n");
    }

    private static String getModifiers(Class<?> clazz) {
        String modifier = Modifier.toString(clazz.getModifiers());

        if (clazz.isEnum()) {
            return modifier.replace("final", "").trim() + " ";
        }

        return modifier.isEmpty() ? "" : modifier + " ";
    }

    private static void appendEnumConstants(Class<?> clazz, StringBuilder html) {
        if (clazz.isEnum()) {
            html.append("\t<h3>Constants</h3>\n<ul class='enum-list'>");
            for (Object constant : clazz.getEnumConstants()) {
                html.append(CODE_START).append(constant.toString()).append(CODE_END);
            }

            html.append(LIST_END);
        }
    }

    private static void appendFields(Class<?> clazz, StringBuilder html) {
        boolean hasFields = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (clazz.isEnum() && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                continue;

            if (!hasFields) {
                html.append("\t<h3>Fields</h3>\n<ul class='field-list'>");
                hasFields = true;
            }

            html.append(CODE_START)
                    .append(Modifier.toString(field.getModifiers())).append(" ")
                    .append(field.getType().getSimpleName()).append(" ")
                    .append(field.getName()).append(CODE_END);
        }

        if (hasFields)
            html.append(LIST_END);
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder html) {
        boolean hasConstructors = false;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Modifier.isPrivate(constructor.getModifiers()))
                continue;

            if (!hasConstructors) {
                html.append("\t<h3>Constructors</h3>\n<ul class='constructor-list'>");
                hasConstructors = true;
            }

            html.append(CODE_START)
                    .append(Modifier.toString(constructor.getModifiers())).append(" ")
                    .append(constructor.getName()).append("(");
            appendParameters(constructor.getParameters(), html);
            html.append(")").append(CODE_END);
        }

        if (hasConstructors)
            html.append(LIST_END);
    }

    private static void appendMethods(Class<?> clazz, StringBuilder html) {
        List<String> gettersSetters = new java.util.ArrayList<>();
        List<String> methods = new java.util.ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers()))
                continue;

            StringBuilder signature = new StringBuilder(CODE_START)
                    .append(Modifier.toString(method.getModifiers())).append(" ")
                    .append(method.getReturnType().getSimpleName()).append(" ")
                    .append(method.getName()).append("(");
            appendParameters(method.getParameters(), signature);
            signature.append(")").append(CODE_END);
            (method.getName().startsWith("get") || method.getName().startsWith("set") ? gettersSetters : methods)
                    .add(signature.toString());
        }

        if (!gettersSetters.isEmpty()) {
            html.append("\t<h3>Getters & Setters</h3>\n<ul class='getter-setter-list'>")
                    .append(String.join("", gettersSetters)).append(LIST_END);
        }

        if (!methods.isEmpty()) {
            html.append("\t<h3>Methods</h3>\n<ul class='method-list'>")
                    .append(String.join("", methods)).append(LIST_END);
        }
    }

    private static void appendParameters(Parameter[] parameters, StringBuilder builder) {
        for (int i = 0; i < parameters.length; i++) {
            builder.append(parameters[i].getType().getSimpleName())
                    .append(" ").append(parameters[i].getName());

            if (i < parameters.length - 1)
                builder.append(", ");
        }
    }

    private static String getClassNameWithExtendsAndImplements(Class<?> clazz) {
        StringBuilder name = new StringBuilder(clazz.getSimpleName());
        if (!clazz.isEnum() && clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            name.append(" extends ").append(clazz.getSuperclass().getSimpleName());
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            name.append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                name.append(interfaces[i].getSimpleName());
                if (i < interfaces.length - 1)
                    name.append(", ");
            }
        }

        return name.toString();
    }
}
