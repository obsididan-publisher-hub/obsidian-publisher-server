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
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldType
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldValue
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteNotExistException
import ru.publisher.obsidian.core.notes.NoteService
import ru.publisher.obsidian.dto.NoteDto
import ru.publisher.obsidian.model.NoteFieldDto

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
        description = "Идентификатор (`id`) вычисляется как **MD5-хеш** от полного пути заметки (без расширения) внутри Obsidian-хранилища. ",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Заметка успешно получена",
                content = [Content(schema = Schema(implementation = NoteDto::class))]
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
    ): ResponseEntity<NoteDto> {
        var note: Note
        try {
            note = notesService.getNoteById(id)
        } catch (_: NoteNotExistException) {
            return ResponseEntity.notFound().build();
        }
        val noteContent = contentService.getContent(note)
        val mappedFields: List<NoteFieldDto> = noteContent.frontmatter.fields.map { field ->
            NoteFieldDto(
                name = field.name,
                type = when (field.type) {
                    NoteFieldType.STRING -> NoteFieldDto.Type.STRING
                    NoteFieldType.NUMBER -> NoteFieldDto.Type.NUMBER
                    NoteFieldType.BOOLEAN -> NoteFieldDto.Type.BOOLEAN
                    NoteFieldType.LIST -> NoteFieldDto.Type.LIST
                },
                value = when (val v = field.value) {
                    is NoteFieldValue.StringValue -> v.value
                    is NoteFieldValue.NumberValue -> v.value
                    is NoteFieldValue.BooleanValue -> v.value
                    is NoteFieldValue.ListValue -> v.value
                }
            )
        }
        return ResponseEntity.ok(NoteDto(note.id, note.fullName, note.fullName, mappedFields, noteContent.body));
    }
}
