package ru.publisher.obsidian.core.notes

/**
 * Класс заметки.
 *
 * @property id уникальный строковый идентификатор
 * @property fullName полное имя заметки (от корня хранилища, включая расширение)
 *
 * @author fenya
 * @since 2025.09.20
 */
data class Note(val id: String, val fullName: String)
