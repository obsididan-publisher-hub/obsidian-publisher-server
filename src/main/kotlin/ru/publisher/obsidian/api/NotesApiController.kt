package ru.publisher.obsidian.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.publisher.obsidian.core.contents.NoteContentService
import ru.publisher.obsidian.core.notes.NoteService
import ru.publisher.obsidian.dto.Note

@RestController
@Validated
@RequestMapping("\${api.base.path}")
class NotesApiController(
    private val notesService: NoteService,
    private val contentService: NoteContentService
) {

    @Operation(
        summary = "Получить заметку по идентификатору",
        operationId = "notesIdGet",
        description = "Идентификатор (`id`) вычисляется как **MD5-хеш** от полного пути заметки внутри Obsidian-хранилища.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Заметка успешно получена",
                content = [Content(schema = Schema(implementation = Note::class))]
            ),
            ApiResponse(responseCode = "404", description = "Заметка не найдена")]
    )
    @GetMapping(
        value = ["/notes/{id}"],
        produces = ["application/json"]
    )
    fun notesIdGet(
        @Parameter(
            description = "MD5-хеш полного пути к заметке",
            required = true
        ) @PathVariable("id") id: String
    ): ResponseEntity<Note> {
        val note: ru.publisher.obsidian.core.notes.Note = notesService.getNoteById(id)
        val noteFullContent = contentService.getFullContent(note)
        return ResponseEntity.ok(Note(note.id, note.fullName, note.fullName, listOf(), noteFullContent));
    }
}
