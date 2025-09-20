package ru.publisher.obsidian.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("\${api.base.path}")
class TagsApiController() {

    @Operation(
        summary = "Получить все теги",
        operationId = "tagsGet",
        description = """Вернуть список всех тегов, присутствующих в хранилище Obsidian.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Список тегов",
            )]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/tags"],
        produces = ["application/json"]
    )
    fun tagsGet(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(listOf("db", "функан"));
    }
}
