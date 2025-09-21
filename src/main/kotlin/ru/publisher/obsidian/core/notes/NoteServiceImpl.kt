package ru.publisher.obsidian.core.notes

/**
 * Расширение файла заметки
 */
const val NOTE_EXTENSION = "md"

/**
 * Сервис, хранящий заметки хранилища в памяти
 *
 * @fenya
 * @since 2025.09.20
 */
class NoteServiceImpl(private var notesGraph: Map<String, Note>) : NoteService {

    override fun getNoteById(noteId: String): Note {
        return notesGraph.getOrElse(noteId) {
            throw NoteNotExistException("Note '$noteId' not found.")
        }
    }
}