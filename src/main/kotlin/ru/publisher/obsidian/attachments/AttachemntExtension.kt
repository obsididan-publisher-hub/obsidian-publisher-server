package ru.publisher.obsidian.attachments

enum class AttachmentExtension(val extentionStr: String) {
    PNG("png");

    companion object {
        fun fromExtension(extension: String): AttachmentExtension? {
            return entries.firstOrNull { it.extentionStr == extension }
        }
    }
}