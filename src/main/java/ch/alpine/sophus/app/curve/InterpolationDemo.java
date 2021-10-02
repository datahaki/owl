// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.spline.BSplineLimitMatrix;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Inverse;
import ch.alpine.tensor.red.Nest;

// TODO functionality/purpose is not clear
/* package */ class InterpolationDemo extends ControlPointsDemo {
  public InterpolationDemo() {
    super(true, ManifoldDisplays.SE2C_SE2_R2);
    // ---
    addButtonDubins();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    BiinvariantMean biinvariantMean = manifoldDisplay.biinvariantMean();
    Tensor sequence = getGeodesicControlPoints();
    if (0 < sequence.length()) {
      Tensor matrix = BSplineLimitMatrix.string(sequence.length(), 3);
      Tensor invers = Inverse.of(matrix);
      Tensor tensor = Tensor.of(invers.stream().map(weights -> biinvariantMean.mean(sequence, weights)));
      CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(manifoldDisplay.geodesic());
      Tensor refine = Nest.of(curveSubdivision::string, tensor, 5);
      new PathRender(Color.BLUE).setCurve(refine, false).render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new InterpolationDemo().setVisible(1000, 800);
  }
}
