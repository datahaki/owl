// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.function.Supplier;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.crv.d2.EllipsePoints;
import ch.alpine.sophus.math.BijectionFamily;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.nrm.Vector2NormSquared;

/** ellipsoid region that is moving with respect to time */
public class R2xTEllipsoidStateTimeRegion implements Region<StateTime>, RenderInterface, Serializable {
  /** number of samples to visualize ellipsoid */
  private static final int RESOLUTION = 22;
  // ---
  private final Tensor invert;
  private final Tensor polygon;
  private final BijectionFamily bijectionFamily;
  private final Supplier<Scalar> supplier;

  /** @param radius encodes principle axis of ellipsoid region
   * @param bijectionFamily with origin at center of ellipsoid region
   * @param supplier for parameter to evaluate bijectionFamily */
  public R2xTEllipsoidStateTimeRegion(Tensor radius, BijectionFamily bijectionFamily, Supplier<Scalar> supplier) {
    invert = radius.map(Scalar::reciprocal);
    this.bijectionFamily = bijectionFamily;
    this.supplier = supplier;
    polygon = EllipsePoints.of(RESOLUTION, Extract2D.FUNCTION.apply(radius));
  }

  @Override // from Region
  public boolean test(StateTime stateTime) {
    Tensor state = stateTime.state().extract(0, invert.length());
    Scalar time = stateTime.time();
    TensorUnaryOperator rev = bijectionFamily.inverse(time);
    return Scalars.lessEquals(Vector2NormSquared.of(rev.apply(state).pmul(invert)), RealScalar.ONE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar time = supplier.get();
    TensorUnaryOperator fwd = bijectionFamily.forward(time);
    Path2D path2D = geometricLayer.toPath2D(Tensor.of(polygon.stream().map(fwd)));
    path2D.closePath();
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(path2D);
    graphics.setColor(RegionRenders.BOUNDARY);
    graphics.draw(path2D);
  }
}
