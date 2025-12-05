package ru.publisher.obsidian.html

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.publisher.obsidian.attachments.Attachment
import ru.publisher.obsidian.attachments.AttachmentExtension
import ru.publisher.obsidian.attachments.AttachmentService
import ru.publisher.obsidian.core.contents.NoteContentService
import ru.publisher.obsidian.core.notes.NOTE_EXTENSION
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteService
import ru.publisher.obsidian.core.notes.NoteUtils.Companion.NOTE_LINK_REGEX
import java.nio.file.Files
import java.nio.file.Path


@Configuration
class NotesHtmlResourcesConfiguration {

    private val LOG: Logger = LoggerFactory.getLogger(NotesHtmlResourcesConfiguration::class.java)

    companion object {
        const val HTML_RESOURCES = "notesHtmlResources"

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
            <h1>%s</h1>
            %s
            </body>
            </html>
        """
    }

    @Value("\${obsidian.vault.path}")
    lateinit var vaultPath: String

    @Value("\${notes.htmlConverted.path}")
    lateinit var resourcesPath: String

    @Autowired
    lateinit var noteService: NoteService

    @Autowired
    lateinit var noteContentService: NoteContentService

    @Autowired
    lateinit var attachmentService: AttachmentService

    @Bean(HTML_RESOURCES)
    fun htmlNotesViews(): Map<Note, Path> {
        val resourcesDir = Path.of(resourcesPath)
        if (!Files.exists(resourcesDir)) Files.createDirectories(resourcesDir)

        val markdownParser: Parser = createParser()
        val renderer: HtmlRenderer = HtmlRenderer.builder().build()

        val notesMap = HashMap<Note, Path>()

        noteService.getAllNotes().forEach {
            val (_, body) = noteContentService.getContent(it)
            val noteName: String = noteService.getNoteName(it)
            val processedBody = replaceLinks(it, body)
            val document = markdownParser.parse(processedBody)
            val htmlBody = renderer.render(document)
            val html = String.format(HTML_TEMPLATE, noteName, noteName, htmlBody)
            val outputFile = resourcesDir.resolve("${it.id}.html")
            Files.writeString(outputFile, html)
            notesMap[it] = outputFile
        }

        LOG.info("Html converting ended. ${notesMap.size} notes converted.")
        return notesMap
    }

    /**
     * Заменяет ссылки в формате вики ([[link|label]]) на markdown ссылки вида [label](link)
     */
    private fun replaceLinks(note: Note, markdown: String): String {
        return NOTE_LINK_REGEX.replace(markdown) { match ->
            val resource = match.groupValues[1].trim()
            val section = match.groupValues[2].trim()
            val alias = match.groupValues[3].trim()
            if (resource.isEmpty()) // если ссылка пустая, это ссылка на текущую заметку
            {
                if (section.isEmpty()) {
                    return@replace "/notes/${note.id}"
                }
                return@replace "/notes/${note.id}#$section"
            }


            val extension = resource.substringAfterLast('.', "")
            val isNote = extension == NOTE_EXTENSION
                    || extension.isEmpty()
                    || AttachmentExtension.fromExtension(extension) == null

            var resourceId: String? = null
            if (isNote) {
                val pathWithExtension = if (extension != NOTE_EXTENSION) {
                    "$resource.$NOTE_EXTENSION"
                } else {
                    resource
                }

                val foundNotes: List<Note> =
                    noteService.getAllNotes().asSequence().filter { it.fullName.endsWith(pathWithExtension) }.toList()

                if (foundNotes.isEmpty()) {
                    LOG.warn("Note not found for link: '{}'", resource)
                } else if (foundNotes.size == 1) {
                    resourceId = foundNotes[0].id
                } else {
                    val filteredNotes =
                        foundNotes.asSequence().filter { it.fullName.substringAfterLast("/").startsWith(resource) }
                            .toList()
                    if (filteredNotes.size == 1) {
                        resourceId = foundNotes[0].id
                    } else {
                        LOG.warn("Note not found for link: '{}'", resource)
                    }
                }
            } else {
                val foundAttachment: Attachment? =
                    attachmentService.getAllAttachments().find { it.fullName.endsWith(resource) }
                if (foundAttachment != null) {
                    resourceId = foundAttachment.id
                } else {
                    resourceId = resource
                    LOG.warn("Attachment not found for link: '{}'", resource)
                }
            }

            val label: String = alias.ifEmpty {
                resource
            }

            val href =
                if (resourceId != null && isNote) {
                    "/notes/$resourceId" + (section?.let { "#$it" } ?: "")
                } else if (AttachmentExtension.fromExtension(extension) != null) {
                    "/attachments/$resourceId"
                } else {
                    null
                }

            val result =
                if (isNote && href != null) {
                    "[$label]($href)"
                } else if (AttachmentExtension.fromExtension(extension) != null && href != null) {
                    "![${label}]($href)"
                } else {
                    match.value
                }
            return@replace result
        }
    }

    private fun createParser(): Parser {
        val options = MutableDataSet()
        return Parser.builder(options).build()
    }
}
