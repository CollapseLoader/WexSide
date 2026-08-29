package ru.wexside.mixin;

import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.wexside.module.hud.BetterChatModule;

@Mixin(value={class_338.class})
public abstract class ChatHudRenderMixin {
    @Shadow
    @Final
    private List<class_303> field_2061;

    @Shadow
    protected abstract void method_44813();

    @ModifyVariable(method={"method_44811"}, at=@At(value="HEAD"), argsOnly=true)
    private class_2561 wexside$collapseRepeatedMessage(class_2561 message) {
        String content = message.getString();
        int repeatCount = BetterChatModule.compute(content);
        if (repeatCount <= 1) {
            return message;
        }
        String repeatedPrefix = content + " \u00d7";
        this.field_2061.removeIf(line -> {
            String previous = line.comp_893().getString();
            return previous.equals(content) || previous.startsWith(repeatedPrefix);
        });
        this.method_44813();
        return message.method_27661().method_10852((class_2561)class_2561.method_43470((String)(" \u00d7" + repeatCount)).method_27692(class_124.field_1080));
    }
}

