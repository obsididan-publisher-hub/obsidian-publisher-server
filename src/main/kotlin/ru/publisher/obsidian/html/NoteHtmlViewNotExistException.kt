package ru.publisher.obsidian.html

/**
 * Исключение, возникающее, если не получилось получить представление заметки в виде html
 */
class NoteHtmlViewNotExistException(message: String) : Exception(message) {
    companion object {
        private const val serialVersionUID: Long = -8532971605480637321L
    }
}