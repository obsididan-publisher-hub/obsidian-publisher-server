package ru.publisher.obsidian.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 * Поле заметки
 * @param name Название поля
 * @param type Тип значения поля
 * @param &#x60;value&#x60;
 */
data class NoteField(

    @field:Schema(example = "createdAt", description = "Название поля")
    @get:JsonProperty("name") val name: String? = null,

    @field:Schema(example = "date", description = "Тип значения поля")
    @get:JsonProperty("type") val type: Type? = null,

    @field:Valid
    @field:Schema(example = "null", description = "")
    @get:JsonProperty("value") val `value`: NoteFieldValue? = null
) {

    /**
     * Тип значения поля
     * Values: string,number,boolean,date,list
     */
    enum class Type(@get:JsonValue val value: String) {

        STRING("string"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        DATE("date"),
        LIST("list");

        companion object {
            @JvmStatic
            @JsonCreator
            fun forValue(value: String): Type {
                return Type.entries.firstOrNull { it.value == value }
                    ?: throw IllegalArgumentException("Unexpected value '$value' for enum 'NoteField'")
            }
        }
    }
}

