package ru.publisher.obsidian.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * @param basePath Глобальный префикс для всех api методов
 * @param startNoteId MD5-хеш стартовой заметки
 */
data class OptionsResponse(

    @field:Schema(example = "/api", description = "Глобальный префикс для всех api методов")
    @get:JsonProperty("basePath") val basePath: String,

    @field:Schema(example = "5d41402abc4b2a76b9719d911017c592", description = "MD5-хеш стартовой заметки")
    @get:JsonProperty("startNoteId") val startNoteId: String
)