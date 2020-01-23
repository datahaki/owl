// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.sophus.srf.subdiv.GeodesicCatmullClarkSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class CatmullClarkSubdivisionDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  CatmullClarkSubdivisionDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {0, 1, 0}, {1, 1, 0}, {2, 1, 0}}").multiply(RealScalar.of(2)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = //
        new GeodesicCatmullClarkSubdivision(Se2CoveringGeodesic.INSTANCE);
    Tensor refined = Nest.of( //
        catmullClarkSubdivision::refine, //
        ArrayReshape.of(control, 2, 3, 3), //
        spinnerRefine.getValue());
    for (Tensor points : refined)
      for (Tensor point : points) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
        geometricLayer.popMatrix();
        int rgb = 128 + 32;
        path2d.closePath();
        graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
      }
  }

  public static void main(String[] args) {
    new CatmullClarkSubdivisionDemo().setVisible(1000, 600);
  }
}
