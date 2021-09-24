// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdClusterRadius;
import ch.alpine.tensor.opt.nd.NdEntry;

public class GraphicSpherical<V> extends NdClusterRadius<V> {
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
  public boolean isViable(NdBox ndBox) {
    StaticHelper.draw(ndBox, geometricLayer, graphics);
    return super.isViable(ndBox);
  }

  @Override
  public void consider(NdEntry<V> ndEntry) {
    StaticHelper.draw(ndEntry.location(), geometricLayer, graphics);
    super.consider(ndEntry);
  }
}
