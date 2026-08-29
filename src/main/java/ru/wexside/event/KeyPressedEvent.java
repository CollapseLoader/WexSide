package ru.wexside.event;

public record KeyPressedEvent(int key, int scancode) implements Event {
}
