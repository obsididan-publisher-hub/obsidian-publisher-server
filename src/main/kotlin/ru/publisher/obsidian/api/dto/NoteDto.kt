package ru.publisher.obsidian.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import ru.publisher.obsidian.model.NoteFieldDto

/**
 * Заметка Obsidian
 * @param id MD5-хеш полного пути заметки
 * @param path Полный путь к заметке в хранилище
 * @param title Название заметки (без расширения)
 * @param fields Список полей заметки
 * @param content Markdown-содержимое заметки
 */
data class NoteDto(

    @field:Schema(example = "5d41402abc4b2a76b9719d911017c592", description = "MD5-хеш полного пути заметки")
    @get:JsonProperty("id") val id: String,

    @field:Schema(example = "Projects/Obsidian/ExampleNote.md", description = "Полный путь к заметке в хранилище")
    @get:JsonProperty("path") val path: String,

    @field:Schema(example = "ExampleNote", description = "Название заметки (без расширения)")
    @get:JsonProperty("title") val title: String,

    @field:Valid
    @field:Schema(example = "[]", description = "Список полей заметки")
    @get:JsonProperty("fields") val fields: List<NoteFieldDto>,

    @field:Schema(example = "# ЗаголовокТекст заметки...", description = "Markdown-содержимое заметки")
    @get:JsonProperty("content") val content: String
)
