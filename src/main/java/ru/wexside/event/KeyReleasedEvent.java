package ru.wexside.event;

public record KeyReleasedEvent(int key, int scancode) implements Event {
}
