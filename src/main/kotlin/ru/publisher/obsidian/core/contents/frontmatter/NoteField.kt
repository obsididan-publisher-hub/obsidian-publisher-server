package ru.publisher.obsidian.core.contents.frontmatter

/**
 * Поле заметки
 *
 * @param name имя поля
 * @param type тип поля
 * @param value значение поля
 */
data class NoteField(val name: String, val type :NoteFieldType, val value: NoteFieldValue)
