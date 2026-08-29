package ru.wexside.prediction;

import java.util.List;
import net.minecraft.class_243;

public record TrajectoryPrediction(List<class_243> points, ProjectileImpact impact, int flightTicks) {
}
