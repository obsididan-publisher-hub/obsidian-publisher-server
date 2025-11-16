package ru.publisher.obsidian.html

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.html.NotesHtmlResourcesConfiguration.Companion.HTML_RESOURCES_BEAN
import java.nio.file.Files
import java.nio.file.Path

@Service
class NoteHtmlViewServiceImpl(@param:Qualifier(HTML_RESOURCES_BEAN) val htmlResources: Map<Note, Path>) :
    NoteHtmlViewService {
    override fun getContent(note: Note): String = Files.readString(htmlResources.getOrElse(note) {
        throw NoteHtmlViewNotExistException("Can't find html view for ${note.fullName}")
    })
}