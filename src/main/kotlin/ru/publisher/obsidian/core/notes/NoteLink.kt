package ru.publisher.obsidian.core.notes

/**
 * @param original полное имя ссылки, как встречается в заметке
 * @param path путь к заметке без alias и section
 * @param section якорь на место в заметке
 * @param alias псевдоним ссылки
 */
data class NoteLink(
    val original: String,
    val path: String,
    val section: String? = null,
    val alias: String? = null
)
