package ru.wexside.config;

import java.io.File;
import ru.wexside.misc.TextureResource;

public record LocalConfigEntry(String name, String author, String updatedAt, String server, File file, TextureResource avatar) {
}
