package ru.publisher.obsidian.core.notes

/**
 * Сервис предоставляющий доступ к сохраненным заметка obsidian хранилища
 */
interface NoteService {
    /**
     * @return заметка с переданным идентификатором
     */
    fun getNoteById(noteId: String): Note
}