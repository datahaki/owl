// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.region.BallRegion;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.rot.CirclePoints;

public class BallRegionRender implements RenderInterface {
  private static final Tensor CIRCLE_POINTS = CirclePoints.of(16).unmodifiable();
  private static final float[] RATIOS = { 0.0f, 1.0f };
  private static final Color[] COLORS = { new Color(255, 0, 0, 64), new Color(255, 255, 0, 64) };
  private final BallRegion ballRegion;

  public BallRegionRender(BallRegion ballRegion) {
    this.ballRegion = ballRegion;
  }

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, BallRegion sphericalRegion) {
    Tensor polygon = CIRCLE_POINTS.multiply(sphericalRegion.radius());
    geometricLayer.pushMatrix(Se2Matrix.translation(sphericalRegion.center()));
    Point2D center = geometricLayer.toPoint2D(Array.zeros(2));
    Point2D extent = geometricLayer.toPoint2D(polygon.get(0));
    graphics.setPaint(new RadialGradientPaint( //
        center, (float) extent.distance(center), //
        RATIOS, COLORS));
    graphics.fill(geometricLayer.toPath2D(polygon));
    geometricLayer.popMatrix();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    draw(geometricLayer, graphics, ballRegion);
  }
}
