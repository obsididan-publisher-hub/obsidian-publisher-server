package ru.publisher.obsidian.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.publisher.obsidian.attachments.ATTACHMENTS
import ru.publisher.obsidian.attachments.Attachment
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteService
import ru.publisher.obsidian.html.NotesHtmlResourcesConfiguration.Companion.HTML_RESOURCES
import java.nio.file.Files
import java.nio.file.Path

@Controller
@RequestMapping("/")
class HtmlResourcesController(
    @field:Qualifier(HTML_RESOURCES) private val notes: Map<Note, Path>,
    @field:Qualifier(ATTACHMENTS) private val attachments: Map<String, Attachment>,
    private val noteService: NoteService
) {

    @GetMapping(produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun getStartNote(): ResponseEntity<String> {
        val startNote: Note = noteService.getStartNote()
        return getNoteHtmlView(startNote)
    }

    @GetMapping("notes/{id}", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun getNote(@PathVariable id: String): ResponseEntity<String> {
        val note: Note = noteService.getNoteById(id)
        return getNoteHtmlView(note)
    }

    private fun getNoteHtmlView(note: Note): ResponseEntity<String> {
        val htmlPath: Path = notes[note] ?: return ResponseEntity.notFound().build()
        val html = Files.readString(htmlPath)
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

    @GetMapping("/attachments/{id}")
    @ResponseBody
    fun getAttachment(@PathVariable id: String): ResponseEntity<ByteArray> {
        val attachment: Attachment = attachments[id] ?: return ResponseEntity.notFound().build()
        val bytes = Files.readAllBytes(attachment.path)
        val mediaType = MediaType.parseMediaType("image/" + attachment.extension.extentionStr) // например "image/png"
        return ResponseEntity.ok()
            .contentType(mediaType)
            .body(bytes)
    }
}
