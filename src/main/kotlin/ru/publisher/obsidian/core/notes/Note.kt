package ru.publisher.obsidian.core.notes

/**
 * Класс заметки.
 *
 * @property id уникальный строковый идентификатор
 * @property fullName полное имя заметки
 * @property outgoingNotes идентификаторы исходящих заметок
 * @property incomingNotes идентификаторы входящих заметок
 *
 * @author fenya
 * @since 2025.09.20
 */
data class Note(
    val id: String,
    val fullName: String,
    val outgoingNotes: MutableSet<String>,
    val incomingNotes: MutableSet<String>
)
