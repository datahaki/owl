// code by jph
package ch.alpine.tensor.demo.nd;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdEntry;

public class GraphicNearest<V> extends NdCollectNearest<V> {
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  protected GraphicNearest( //
      NdCenterInterface ndCenterInterface, int limit, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    super(ndCenterInterface, limit);
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
