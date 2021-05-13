// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.gui.win.DubinsGenerator;
import ch.alpine.sophus.lie.r3.MinTriangleAreaSquared;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.sophus.ply.PolygonCentroid;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/** Reference:
 * "Polygon Laplacian Made Simple"
 * by Astrid Bunge, Philipp Herholz, Misha Kazhdan, Mario Botsch, 2020 */
/* package */ class MinTriangleAreaSquaredDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRender = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public MinTriangleAreaSquaredDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
    timerFrame.geometricComponent.addRenderInterface(pathRender);
    // ---
    Tensor blub = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {2, 0, 2.5708}, {1, 0, 2.1}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
        Tensor.of(blub.stream().map(Tensors.vector(2, 1, 1)::pmul))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    final ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    pathRender.setCurve(sequence, true);
    if (0 < sequence.length()) {
      Tensor polygon = Tensor.of(sequence.stream().map(AppendOne.FUNCTION));
      Tensor weights = MinTriangleAreaSquared.INSTANCE.origin(polygon);
      {
        Tensor origin = weights.dot(polygon).extract(0, 2);
        LeversRender leversRender = //
            LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderWeights(weights);
        leversRender.renderOrigin();
        leversRender.renderLevers(weights);
      }
      {
        Tensor origin = PolygonCentroid.of(sequence);
        LeversRender leversRender = //
            LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderOrigin();
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new MinTriangleAreaSquaredDemo().setVisible(1000, 800);
  }
}
