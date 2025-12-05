package ru.publisher.obsidian.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import ru.publisher.obsidian.api.dto.OptionsResponse
import ru.publisher.obsidian.core.notes.NoteService

@RestController
@Validated
class OptionsApiController(
    @Value("\${api.base.path}") private val apiBasePath: String,
    private val noteService: NoteService
) {

    @Operation(
        summary = "Получить информацию об API",
        operationId = "optionsGet",
        description = "Возвращает базовую информацию API и стартовой заметке",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Информация успешно получена",
                content = [Content(schema = Schema(implementation = OptionsResponse::class))]
            )]
    )
    @GetMapping(
        value = ["/options"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun optionsGet(): ResponseEntity<OptionsResponse> {
        return getOptions()
    }

    @Operation(
        summary = "Получить информацию об API",
        operationId = "getOptions",
        description = "Возвращает базовую информацию об API и стартовой заметке",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Информация успешно получена",
                content = [Content(schema = Schema(implementation = OptionsResponse::class))]
            )]
    )
    @RequestMapping(
        method = [RequestMethod.OPTIONS],
        value = ["/"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getOptions(): ResponseEntity<OptionsResponse> {
        val startNote = runCatching { noteService.getStartNote() }
            .getOrElse { return ResponseEntity.notFound().build() }
        return ResponseEntity.ok(OptionsResponse(apiBasePath, startNote.id))
    }
}
