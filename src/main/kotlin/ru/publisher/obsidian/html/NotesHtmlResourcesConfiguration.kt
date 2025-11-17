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
import ru.publisher.obsidian.core.notes.NoteUtils
import ru.publisher.obsidian.core.notes.NoteUtils.Companion.NOTE_LINK_REGEX
import java.io.File
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
        LOG.info("htmlNotesViews() bean called") // breakpoint сюда точно сработает

        val root = File(vaultPath)
        require(root.exists()) { "Vault directory $vaultPath does not exist" }

        val resourcesDir = Path.of(resourcesPath)
        if (!Files.exists(resourcesDir)) Files.createDirectories(resourcesDir)

        val markdownParser: Parser = createParser()
        val renderer: HtmlRenderer = HtmlRenderer.builder().build()

        val notesMap = HashMap<Note, Path>()

        root.walkTopDown()
            .onEnter { !it.isHidden }
            .filter { it.isFile && it.extension == NOTE_EXTENSION }
            .forEach { file ->
                try {
                    val fullName = file.relativeTo(root).path
                    LOG.info("Converting note: $fullName")

                    val noteId = NoteUtils.calculateResourceId(fullName.substringBeforeLast('.'))
                    val note: Note = noteService.getNoteById(noteId)
                    val (_, body) = noteContentService.getContent(note)

                    val processedBody = replaceLinks(body)
                    val document = markdownParser.parse(processedBody)
                    val htmlBody = renderer.render(document)

                    val html = String.format(HTML_TEMPLATE, fullName, note.fullName, htmlBody)
                    val outputFile = resourcesDir.resolve("$noteId.html")
                    Files.writeString(outputFile, html)

                    notesMap[note] = outputFile
                } catch (e: Exception) {
                    LOG.error("Failed to process note: ${file.absolutePath}", e)
                }
            }

        LOG.info("Vault processing ended. ${notesMap.size} notes processed.")
        return notesMap
    }

    private fun replaceLinks(markdown: String): String {
        return NOTE_LINK_REGEX.replace(markdown) { match ->
            val path = match.groupValues[1].trim()
            val section = match.groupValues.getOrNull(2)?.takeIf { it.isNotEmpty() }
            val alias = match.groupValues.getOrNull(3)?.takeIf { it.isNotEmpty() }

            val extension = path.substringAfterLast('.', "")
            val isNote =
                extension == NOTE_EXTENSION || extension.isEmpty() || AttachmentExtension.fromExtension(extension) == null

            var id: String? = null
            if (isNote) {
                val pathWithExtension = path + "." + NOTE_EXTENSION
                var foundNote: Note? = noteService.getAllNotes().find { it.fullName == pathWithExtension }

                if (foundNote == null) {
                    foundNote = noteService.getAllNotes().find { it.fullName.endsWith(pathWithExtension) }
                }

                if (foundNote != null) {
                    id = foundNote.id
                } else {
                    LOG.warn("Note not found for link: '{}'", path)
                }
            } else {
                var foundAttachment: Attachment? = attachmentService.getAllAttachments().find { it.fullName == path }
                if (foundAttachment == null) {
                    foundAttachment = attachmentService.getAllAttachments().find { it.fullName.endsWith(path) }
                }
                if (foundAttachment != null) {
                    id = foundAttachment.attachmentId
                } else {
                    id = path
                    LOG.warn("Attachment not found for link: '{}'", path)
                }
            }

            val label = alias ?: path.substringBeforeLast('.')

            val href = when {
                id != null && isNote -> "/notes/$id" + (section?.let { "#$it" } ?: "")
                AttachmentExtension.fromExtension(extension) != null -> "/attachments/$id"
                else -> null
            }

            when {
                isNote && href != null -> "[$label]($href)"
                AttachmentExtension.fromExtension(extension) != null && href != null -> "![${label}]($href)"
                else -> match.value
            }
        }
    }

    private fun createParser(): Parser {
        val options = MutableDataSet()
        return Parser.builder(options).build()
    }
}
