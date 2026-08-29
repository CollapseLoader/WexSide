package ru.wexside.misc;

import ru.wexside.misc.FrameInterpolator;

public class FadeAnimation {
    private float progress;
    private float speed;

    public FadeAnimation(float speed) {
        this.speed = speed;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void updateTarget(boolean visible) {
        this.progress = FrameInterpolator.lerpTowards(this.progress, visible ? 1.0f : 0.0f, this.speed);
    }

    public boolean isHidden() {
        return this.progress < 1.0E-4f;
    }

    public float getProgress() {
        return this.progress;
    }

    public void reset() {
        this.progress = 0.0f;
    }
}

