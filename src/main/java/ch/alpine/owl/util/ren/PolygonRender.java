// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Graphics2D;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.tensor.Tensor;

public record PolygonRender(Tensor polygon) implements RenderInterface {
  public static PolygonRender of(PolygonRegion polygonRegion) {
    return new PolygonRender(polygonRegion.polygon());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(geometricLayer.toPath2D(polygon));
  }
}
