// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.gds.GeodesicDisplayRender;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.hs.sn.SnManifold;
import ch.alpine.sophus.hs.sn.TSnProjection;
import ch.alpine.sophus.math.Exponential;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;

/* package */ class S2ExpDemo extends ControlPointsDemo {
  public S2ExpDemo() {
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
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor points = getGeodesicControlPoints(0, 1);
    if (0 < points.length()) {
      Tensor origin = points.get(0);
      Exponential exponential = SnManifold.INSTANCE.exponential(origin);
      Tensor tSnProjection = TSnProjection.of(origin);
      Tensor m = Array.of(list -> exponential.exp(Tensors.vector(list).multiply(RealScalar.of(.25)).dot(tSnProjection)), 5, 5);
      Tensor sequence = Flatten.of(m, 1);
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
      leversRender.renderOrigin();
      leversRender.renderSequence();
    }
  }

  public static void main(String[] args) {
    new S2ExpDemo().setVisible(1000, 800);
  }
}
