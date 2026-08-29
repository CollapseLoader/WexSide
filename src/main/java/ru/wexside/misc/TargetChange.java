package ru.wexside.misc;

public record TargetChange<T>(T previous, T current, boolean changed, boolean disappearing) {
}
