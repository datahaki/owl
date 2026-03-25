// code by gjoel
package ch.alpine.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.Optional;

import ch.alpine.bridge.gfx.GeometricLayer;

@FunctionalInterface
interface TrajectoryTargetRender {
  Optional<Shape> toTarget(GeometricLayer geometricLayer);
}
