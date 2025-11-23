package ru.publisher.obsidian.core.notes

/**
 * Ссылка внутри тела заметки
 *
 * @param original полное имя ссылки, как встречается в заметке
 * @param path путь к ресурсу без alias и section
 * @param section якорь на место в заметке
 * @param alias псевдоним ссылки
 */
data class Link(
    val original: String,
    val path: String?,
    val section: String? = null,
    val alias: String? = null
)
