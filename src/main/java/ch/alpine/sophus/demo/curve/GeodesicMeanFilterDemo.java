// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.DubinsGenerator;
import ch.alpine.sophus.flt.ga.GeodesicMeanFilter;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.ref.d1.BSpline4CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.red.Times;

public class GeodesicMeanFilterDemo extends ControlPointsDemo {
  @FieldInteger
  @FieldSelectionArray(value = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" })
  public Scalar radius = RealScalar.of(9);

  GeodesicMeanFilterDemo() {
    super(true, ManifoldDisplays.ALL);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(Times.operator(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    int _radius = radius.number().intValue();
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    TensorUnaryOperator geodesicMeanFilter = GeodesicMeanFilter.of(manifoldDisplay.geodesic(), _radius);
    Tensor refined = geodesicMeanFilter.apply(control);
    Tensor curve = Nest.of(BSpline4CurveSubdivision.split2lo(manifoldDisplay.geodesic())::string, refined, 7);
    Tensor render = Tensor.of(curve.stream().map(manifoldDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    renderPoints(manifoldDisplay, refined, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new GeodesicMeanFilterDemo().setVisible(1000, 600);
  }
}
