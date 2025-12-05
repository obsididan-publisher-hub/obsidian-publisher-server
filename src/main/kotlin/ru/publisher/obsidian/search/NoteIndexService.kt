package ru.publisher.obsidian.search

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.contents.NoteContentService
import ru.publisher.obsidian.core.contents.frontmatter.NoteFieldValue
import ru.publisher.obsidian.core.notes.NoteService

@Service
class NoteIndexService(
    private val noteService: NoteService,
    private val noteContentService: NoteContentService,
    private val analyzer: Analyzer,
    private val directory: Directory
) {

    private val logger: Logger = LoggerFactory.getLogger(NoteIndexService::class.java)

    private val writer: IndexWriter by lazy {
        IndexWriter(directory, IndexWriterConfig(analyzer))
    }

    /**
     * Строит индекс заново.
     */
    fun rebuildIndex() {
        logger.info("Start rebuilding Lucene index")
        writer.deleteAll()

        val notes = noteService.getAllNotes()
        logger.info("Indexing ${notes.size} notes")

        notes.forEach { note ->
            val content = noteContentService.getContent(note)

            val doc = Document().apply {
                add(StringField("id", note.id, Field.Store.YES))
                add(TextField("fullName", note.fullName, Field.Store.YES))
                add(TextField("body", content.body, Field.Store.YES))

                content.frontmatter.fields.forEach { field ->
                    when (field.value) {
                        is NoteFieldValue.StringValue ->
                            add(TextField(field.name, field.value.value, Field.Store.YES))

                        is NoteFieldValue.NumberValue ->
                            add(TextField(field.name, field.value.value.toPlainString(), Field.Store.YES))

                        is NoteFieldValue.BooleanValue ->
                            add(TextField(field.name, field.value.value.toString(), Field.Store.YES))

                        is NoteFieldValue.ListValue ->
                            add(TextField(field.name, field.value.value.joinToString(" "), Field.Store.YES))
                    }
                }
            }

            writer.addDocument(doc)
            logger.info("Indexed note: ${note.fullName}")
        }

        writer.commit()
        logger.info("Lucene index rebuild finished")
    }

    /**
     * Автоматически строим индекс при старте приложения
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        rebuildIndex()
    }
}
