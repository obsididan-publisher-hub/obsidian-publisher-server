package ru.publisher.obsidian.core.contents

import ru.publisher.obsidian.core.notes.Note

/**
 * Сервис, работающий с содержимым заметок
 */
interface NoteContentService {

    /**
     * @return содержимое заметки
     */
    fun getContent(note: Note): NoteContent
}