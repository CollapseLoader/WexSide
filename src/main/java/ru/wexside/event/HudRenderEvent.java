package ru.wexside.event;

import net.minecraft.class_332;
import net.minecraft.class_9779;
import ru.wexside.event.Event;

public record HudRenderEvent(class_332 context, class_9779 tickCounter) implements Event
{
}

