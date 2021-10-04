// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.app.opt.Spearhead;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/* package */ class SpearheadDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(128);

  public SpearheadDemo() {
    super(false, ManifoldDisplays.SE2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.5, -0.5, 0.3}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    Tensor curve = Spearhead.of(control.get(0), RealScalar.of(geometricLayer.pixel2modelWidth(10)));
    graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
    graphics.fill(geometricLayer.toPath2D(curve));
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
        .setCurve(curve, false) //
        .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new SpearheadDemo().setVisible(1000, 800);
  }
}
