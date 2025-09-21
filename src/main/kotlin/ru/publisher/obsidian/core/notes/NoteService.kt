package ru.publisher.obsidian.core.notes

/**
 * Сервис предоставляющий доступ к графу заметок obsidian и описанию заметки
 */
interface NoteService {
    /**
     * @return заметка с переданным идентификатором
     */
    fun getNoteById(noteId: String): Note
}