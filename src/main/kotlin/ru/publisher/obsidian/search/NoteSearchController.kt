package ru.publisher.obsidian.search

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.publisher.obsidian.core.notes.Note

@Controller
class NoteSearchMvcController(
    private val searchService: NoteSearchService
) {

    @GetMapping("/search")
    fun showSearchForm(): String {
        return "search-form"
    }

    @GetMapping("/notes/search")
    fun searchNotes(
        @RequestParam searchString: String,
        @RequestParam(required = false, defaultValue = "100") limit: Int,
        model: Model
    ): String {
        val searchResult = searchService.search(searchString, limit)
        val notes: List<Note> = searchResult.notes

        model.addAttribute("notes", notes)
        model.addAttribute("searchString", searchString)

        return "search"
    }
}
