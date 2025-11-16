package ru.publisher.obsidian.core.notes

/**
 * Сервис предоставляющий доступ к графу заметок obsidian и описанию заметки
 */
interface NoteService {
    /**
     * @return стартовая заметка
     */
    fun getStartNote(): Note

    /**
     * @return заметка с переданным идентификатором
     */
    fun getNoteById(noteId: String): Note

    /**
     * @return все заметки в хранилище
     */
    fun getAllNotes(): Set<Note>
}