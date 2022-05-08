// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.function.Supplier;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.BijectionFamily;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** check if input tensor is inside a polygon */
public class R2xTPolygonStateTimeRegion implements Region<StateTime>, RenderInterface, Serializable {
  private final BijectionFamily bijectionFamily;
  private final Supplier<Scalar> supplier;
  private final Tensor polygon;
  private final PolygonRegion polygonRegion;

  /** @param polygon
   * @param bijectionFamily
   * @param supplier */
  public R2xTPolygonStateTimeRegion(Tensor polygon, BijectionFamily bijectionFamily, Supplier<Scalar> supplier) {
    this.polygon = polygon;
    polygonRegion = new PolygonRegion(polygon);
    this.bijectionFamily = bijectionFamily;
    this.supplier = supplier;
  }

  @Override // from Region
  public boolean test(StateTime stateTime) {
    Tensor state = Extract2D.FUNCTION.apply(stateTime.state());
    Scalar time = stateTime.time();
    TensorUnaryOperator rev = bijectionFamily.inverse(time);
    return polygonRegion.test(rev.apply(state));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar time = supplier.get();
    TensorUnaryOperator forward = bijectionFamily.forward(time);
    Path2D path2D = geometricLayer.toPath2D(Tensor.of(polygon.stream().map(forward)));
    path2D.closePath();
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(path2D);
    graphics.setColor(RegionRenders.BOUNDARY);
    graphics.draw(path2D);
  }
}
