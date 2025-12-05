package ru.publisher.obsidian.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.publisher.obsidian.search.NoteSearchService

data class SearchResponse(
    val noteIds: List<String>
)

@RestController
@Validated
@RequestMapping("\${api.base.path}")
class NoteSearchApiController(
    private val searchService: NoteSearchService
) {

    @Operation(
        summary = "Поиск заметок",
        description = "Возвращает список идентификаторов заметок, упорядоченных по релевантности",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Список найденных заметок",
                content = [Content(schema = Schema(implementation = SearchResponse::class))]
            )
        ]
    )
    @GetMapping(
        value = ["/notes/search"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun searchNotes(
        @RequestParam searchString: String,
        @RequestParam(required = false, defaultValue = "100") limit: Int
    ): ResponseEntity<SearchResponse> {

        val searchResult = searchService.search(searchString, limit)
        val noteIds = searchResult.notes.map { it.id }
        return ResponseEntity.ok(SearchResponse(noteIds))
    }
}