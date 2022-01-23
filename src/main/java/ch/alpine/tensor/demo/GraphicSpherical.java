// code by jph
package ch.alpine.tensor.demo;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdCollectRadius;
import ch.alpine.tensor.opt.nd.NdEntry;

public class GraphicSpherical<V> extends NdCollectRadius<V> {
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  protected GraphicSpherical( //
      NdCenterInterface ndCenterInterface, Scalar radius, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    super(ndCenterInterface, radius);
    this.geometricLayer = geometricLayer;
    this.graphics = graphics;
  }

  @Override
  public boolean isViable(CoordinateBoundingBox box) {
    StaticHelper.draw(box, geometricLayer, graphics);
    return super.isViable(box);
  }

  @Override
  public void consider(NdEntry<V> ndEntry) {
    StaticHelper.draw(ndEntry.location(), geometricLayer, graphics);
    super.consider(ndEntry);
  }
}
