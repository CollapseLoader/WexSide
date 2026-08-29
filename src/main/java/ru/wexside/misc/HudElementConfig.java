package ru.wexside.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.Setting;

public final class HudElementConfig
implements ConfigSerializable {
    private static final String DEFAULT_SCALE = "100%";
    private static final float SCALE_ANIMATION_SPEED = 20.0f;
    private final String elementId;
    private final ModeSetting scaleSetting = ((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("scale")).name("\u041c\u0430\u0441\u0448\u0442\u0430\u0431")).options("75%", "100%", "125%", "150%", "175%", "200%").defaultOption("100%").build();
    private final List<Setting> settings = new ArrayList<Setting>(List.of(this.scaleSetting));
    private float animatedScale = 1.0f;

    public HudElementConfig(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public String getConfigId() {
        return "hud_visual/" + this.elementId;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    @Override
    public void writeConfig(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.settings.size());
        for (Setting setting : this.settings) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            setting.writeConfig(new DataOutputStream(byteArrayOutputStream));
            byte[] byArray = byteArrayOutputStream.toByteArray();
            dataOutputStream.writeUTF(setting.getId());
            dataOutputStream.writeInt(byArray.length);
            dataOutputStream.write(byArray);
        }
    }

    @Override
    public void readConfig(DataInputStream dataInputStream) throws IOException {
        LinkedHashMap<String, Setting> linkedHashMap = new LinkedHashMap<String, Setting>();
        for (Setting setting : this.settings) {
            linkedHashMap.put(setting.getId(), setting);
        }
        int n = dataInputStream.readInt();
        for (int i = 0; i < n; ++i) {
            String string = dataInputStream.readUTF();
            int n2 = dataInputStream.readInt();
            if (n2 < 0 || n2 > dataInputStream.available()) {
                String string2 = string;
                int n3 = n2;
                throw new IOException("Payload length " + n3 + " for " + string2 + " does not fit the config");
            }
            byte[] byArray = new byte[n2];
            dataInputStream.readFully(byArray);
            Setting setting = (Setting)linkedHashMap.get(string);
            if (setting == null) continue;
            try {
                setting.readConfig(new DataInputStream(new ByteArrayInputStream(byArray)));
                continue;
            }
            catch (IOException | RuntimeException exception) {
                // empty catch block
            }
        }
    }

    public void addSetting(Setting setting) {
        if (setting != null && !this.settings.contains(setting)) {
            this.settings.add(setting);
        }
    }

    public float getScale() {
        return this.animatedScale;
    }

    public void updateScaleAnimation() {
        this.animatedScale = FrameInterpolator.lerpTowards(this.animatedScale, this.getTargetScale(), 20.0f);
    }

    private float getTargetScale() {
        String scaleLabel = this.scaleSetting.getSelectedOption();
        if (scaleLabel == null || scaleLabel.isBlank()) {
            return 1.0f;
        }
        try {
            return (float)Integer.parseInt(scaleLabel.replace("%", "").trim()) / 100.0f;
        }
        catch (NumberFormatException numberFormatException) {
            return 1.0f;
        }
    }

    public ModeSetting getScaleSetting() {
        return this.scaleSetting;
    }

    public String getElementId() {
        return this.elementId;
    }
}

