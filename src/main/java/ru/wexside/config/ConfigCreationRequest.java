package ru.wexside.config;

public record ConfigCreationRequest(boolean serverSpecific, String serverName, String configName) {
}
