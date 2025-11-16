package ru.publisher.obsidian.core.notes

/**
 * Заключение, возникающее, если не удалось найти страницу html, соответвующую опрелеленной заметке [[Note]]
 */
class PageNotExistException(message: String) : Exception(message) {
}