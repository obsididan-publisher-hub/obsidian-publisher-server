package ru.publisher.obsidian.core.notes

class NoteNotExistException(message: String) : Exception(message) {
    companion object {
        private const val serialVersionUID: Long = -5195414305692601326L
    }
}