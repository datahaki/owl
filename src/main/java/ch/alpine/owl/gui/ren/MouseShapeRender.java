// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.GfxMatrix;
import ch.alpine.bridge.win.RenderInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public abstract class MouseShapeRender implements RenderInterface {
  private final Region<StateTime> region;
  private final Tensor shape;

  public MouseShapeRender(Region<StateTime> region, Tensor shape) {
    this.region = region;
    this.shape = shape;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor xya = getSe2();
    StateTime stateTime = new StateTime(xya, getTime());
    geometricLayer.pushMatrix(GfxMatrix.of(xya));
    Color color = region.test(stateTime) //
        ? new Color(255, 96, 96, 128)
        : new Color(0, 128, 255, 192);
    graphics.setColor(color);
    graphics.fill(geometricLayer.toPath2D(shape));
    geometricLayer.popMatrix();
  }

  public abstract Scalar getTime();

  public abstract Tensor getSe2();
}
