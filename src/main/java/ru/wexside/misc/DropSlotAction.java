package ru.wexside.misc;

public record DropSlotAction(int slot, boolean entireStack) implements InventoryAction {
}
