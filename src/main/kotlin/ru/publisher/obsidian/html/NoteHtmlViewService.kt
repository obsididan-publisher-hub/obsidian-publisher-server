package ru.publisher.obsidian.html

import ru.publisher.obsidian.core.notes.Note

/**
 * Сервис, возвращающий представление заметок в виде html страниц
 */
interface NoteHtmlViewService {
    /**
     * @return содержимое заметки в виде html
     */
    fun getContent(note: Note): String
}