package hr.tvz.cartographers.documentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentationConstants {
    public static final String HTML_START = """
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
    public static final String HTML_END = """
            </body>
            </html>
            """;
    public static final String CLASS_PATH = "./target/classes/";
    public static final String DOCUMENTATION_HTML_PATH = "docs/documentation.html";
    public static final String CLASS_BLOCK_START = "\n<div class='class-block'>\n";
    public static final String LIST_END = "</ul>\n";
    public static final String CODE_START = "<li><code>";
    public static final String CODE_END = "</code></li>\n";
}
