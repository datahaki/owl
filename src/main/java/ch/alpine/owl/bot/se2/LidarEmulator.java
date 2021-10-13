// code by jph
package ch.alpine.owl.bot.se2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.function.Supplier;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;

public class LidarEmulator implements RenderInterface {
  public static final Tensor DEFAULT = Subdivide.of(Degree.of(+90), Degree.of(-90), 32);
  public static final Tensor RAYDEMO = Subdivide.of(Degree.of(+90), Degree.of(-90), 5);
  // ---
  public static final Scalar RANGE_MAX = RealScalar.of(5.0);
  /** if the number of lasers are bounded by MAX_RAYS each rays is visualized as a line,
   * otherwise a polygonal area is drawn */
  private static final int MAX_RAYS = 11;
  private static final Color COLOR_LASER_RAY = new Color(255, 0, 0, 32);
  private static final Color COLOR_FREESPACE_FILL = new Color(0, 255, 0, 16);
  private static final Color COLOR_FREESPACE_DRAW = new Color(0, 255, 0, 64);
  // ---
  private final LidarRaytracer lidarRaytracer;
  private final Supplier<StateTime> supplier;
  private final TrajectoryRegionQuery raytraceQuery;

  /** @param resolution angular resolution (should be tensor)
   * @param supplier
   * @param raytraceQuery */
  public LidarEmulator(LidarRaytracer lidarRaytracer, Supplier<StateTime> supplier, TrajectoryRegionQuery raytraceQuery) {
    this.lidarRaytracer = lidarRaytracer;
    this.supplier = supplier;
    this.raytraceQuery = raytraceQuery;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor scan = lidarRaytracer.scan(supplier.get(), raytraceQuery);
    // ---
    Se2Bijection se2Bijection = new Se2Bijection(supplier.get().state());
    geometricLayer.pushMatrix(se2Bijection.forward_se2());
    Tensor polygon = lidarRaytracer.toPoints(scan);
    if (scan.length() <= MAX_RAYS) {
      graphics.setColor(COLOR_LASER_RAY);
      for (Tensor point : polygon) {
        Shape shape = geometricLayer.toLine2D(point);
        graphics.draw(shape);
      }
    } else {
      polygon.append(Array.zeros(2));
      Path2D path2D = geometricLayer.toPath2D(polygon);
      path2D.closePath();
      graphics.setColor(COLOR_FREESPACE_FILL);
      graphics.fill(path2D);
      graphics.setColor(COLOR_FREESPACE_DRAW);
      graphics.draw(path2D);
    }
    geometricLayer.popMatrix();
  }

  public Tensor getPolygon(StateTime stateTime) {
    final Tensor scan = lidarRaytracer.scan(stateTime, raytraceQuery);
    return lidarRaytracer.toPoints(scan);
  }
}
