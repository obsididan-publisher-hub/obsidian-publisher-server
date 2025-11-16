package ru.publisher.obsidian.html

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "notes.ignored")
class IgnoredDirectories(val directories: MutableList<String>)