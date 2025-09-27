package ru.publisher.obsidian.core.contents.frontmatter

import java.math.BigDecimal

/**
 * Значение поля заметки
 */
sealed interface NoteFieldValue {

    /**
     * Строковое значение.
     */
    data class StringValue(
        val value: String
    ) : NoteFieldValue

    /**
     * Числовое значение.
     */
    data class NumberValue(
        val value: BigDecimal
    ) : NoteFieldValue

    /**
     * Логическое значение.
     */
    data class BooleanValue(
        val value: Boolean
    ) : NoteFieldValue

    /**
     * Список строк.
     */
    data class ListValue(
        val value: List<String>
    ) : NoteFieldValue
}
