package ru.publisher.obsidian.attachments

import java.nio.file.Path


data class Attachment(
    val attachmentId: String,
    val fullName: String,
    val path: Path,
    val extension: AttachmentExtension
)
