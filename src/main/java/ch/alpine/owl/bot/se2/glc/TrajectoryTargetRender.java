// code by gjoel
package ch.alpine.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.Optional;

import ch.alpine.java.gfx.GeometricLayer;

@FunctionalInterface
/* package */ interface TrajectoryTargetRender {
  Optional<Shape> toTarget(GeometricLayer geometricLayer);
}
