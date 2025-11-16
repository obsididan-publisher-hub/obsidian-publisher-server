package ru.publisher.obsidian.html

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.publisher.obsidian.core.contents.NoteContentService
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteService
import java.nio.file.Files
import java.nio.file.Path

@Configuration
class NotesHtmlResourcesConfiguration {

    private val LOG: Logger = LoggerFactory.getLogger(Companion::class.java)

    companion object {
        const val HTML_RESOURCES_BEAN = "notesHtmlResources"

        private const val HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <!-- KaTeX стили -->
                <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css">
                <!-- KaTeX JS -->
                <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js"></script>
                <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js"
                    onload="renderMathInElement(document.body, {
                        delimiters: [
                            {left: '$$', right: '$$', display: true},
                            {left: '$', right: '$', display: false}
                        ]
                    });"></script>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        padding: 1rem;
                        line-height: 1.5;
                    }
                </style>
            </head>
            <body>
            %s
            </body>
            </html>
            """
    }

    @Bean(HTML_RESOURCES_BEAN)
    fun notesHtmlResources(
        noteService: NoteService,
        noteContentService: NoteContentService,
        @Value("\${notes.htmlConverted.path}") resourcesPath: String
    ): Map<Note, Path> {
        val resourcesDir = Path.of(resourcesPath)
        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir)
        }

        val markdownParser: Parser = createParser()
        val renderer: HtmlRenderer = HtmlRenderer.builder().build()

        return noteService.getAllNotes()
            .asSequence()
            .associateWith { note ->
                LOG.info("processing {}", note.fullName)
                val (_, body: String) = noteContentService.getContent(note)
                val file = resourcesDir.resolve("${note.id}.html")
                val document = markdownParser.parse(body)
                val htmlBody: String = renderer.render(document)

                // Собираем полный HTML с шаблоном
                val html = String.format(HTML_TEMPLATE, note.fullName, htmlBody)
                Files.writeString(file, html)
            }
            .toMap()
    }

    private fun createParser(): Parser {
        val options = MutableDataSet()
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        return Parser.builder(options).build()
    }
}
