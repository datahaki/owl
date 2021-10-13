// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.lev.LeversRender;
import ch.alpine.sophus.gds.GeodesicDisplayRender;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class S2LogDemo extends ControlPointsDemo {
  public S2LogDemo() {
    super(true, ManifoldDisplays.S2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public ManifoldDisplay getGeodesicDisplay() {
        return manifoldDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor points = getGeodesicControlPoints(0, 1);
    if (0 < points.length()) {
      Tensor origin = points.get(0);
      Tensor sequence = getGeodesicControlPoints(1, Integer.MAX_VALUE);
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay(), sequence, origin, geometricLayer, graphics);
      leversRender.renderLevers();
      leversRender.renderOrigin();
      leversRender.renderSequence();
      leversRender.renderTangentsPtoX(true);
      leversRender.renderTangentsXtoP(true);
    }
  }

  public static void main(String[] args) {
    new S2LogDemo().setVisible(1000, 800);
  }
}
