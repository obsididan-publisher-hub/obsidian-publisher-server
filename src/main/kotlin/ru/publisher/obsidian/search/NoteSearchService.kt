package ru.publisher.obsidian.search

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.springframework.stereotype.Service
import ru.publisher.obsidian.core.notes.Note
import ru.publisher.obsidian.core.notes.NoteService

data class SearchResult(
    val notes: List<Note>,
    val totalHits: Int
)

@Service
class NoteSearchService(
    private val directory: Directory,
    private val analyzer: Analyzer,
    private val noteService: NoteService
) {

    fun search(queryString: String, limit: Int = 20, page: Int = 0): SearchResult {
        if (!DirectoryReader.indexExists(directory)) return SearchResult(emptyList(), 0)

        DirectoryReader.open(directory).use { reader ->
            val searcher = IndexSearcher(reader)
            val parser = QueryParser("body", analyzer)
            val query = parser.parse(queryString)

            val start = page * limit
            val hits = searcher.search(query, start + limit).scoreDocs

            val pagedHits = hits.drop(start).take(limit)

            val notes = pagedHits.mapNotNull { hit ->
                val doc = searcher.storedFields().document(hit.doc)
                val id = doc.get("id")
                runCatching { noteService.getNoteById(id) }.getOrNull()
            }

            return SearchResult(notes, hits.size)
        }
    }
}
