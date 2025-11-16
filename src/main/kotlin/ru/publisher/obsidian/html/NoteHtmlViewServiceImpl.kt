package ru.publisher.obsidian.html

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.contents.NoteContent
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteNotExistException
import ru.publisher.obsidian.html.NotesHtmlResourcesConfiguration.Companion.HTML_RESOURCES_BEAN
import java.nio.file.Path

@Service
class NoteHtmlViewServiceImpl(@Qualifier(HTML_RESOURCES_BEAN) val htmlResources: Map<Note, Path>) :
    NoteHtmlViewService {

    override fun getContent(note: Note):  {
        return htmlResources.getOrElse(note) {
            throw new
        }
    }
}