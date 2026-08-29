package ru.wexside.misc;

import com.google.gson.JsonObject;

public record ConfigReadResult(JsonObject json, boolean needsMigration) {
}
