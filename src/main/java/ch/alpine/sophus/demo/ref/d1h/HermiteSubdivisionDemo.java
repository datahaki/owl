// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.GridRender;
import ch.alpine.sophus.clt.ClothoidDistance;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.HermiteSubdivisions;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.math.AdjacentDistances;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.TensorIteration;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.red.Mean;

public class HermiteSubdivisionDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  public HermiteSubdivisions scheme = HermiteSubdivisions.HERMITE3;
  @FieldSlider
  @FieldPreferredWidth(width = 100)
  @FieldInteger
  @FieldClip(min = "0", max = "9")
  public Scalar refine = RealScalar.of(6);
  public Boolean diff = true;

  public HermiteSubdivisionDemo() {
    super(true, ManifoldDisplays.SE2C_SE2_R2);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
  }

  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 10, 10));

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GRID_RENDER.render(geometricLayer, graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    final Tensor tensor = getControlPointsSe2();
    POINTS_RENDER_0.show(Se2Display.INSTANCE::matrixLift, //
        Se2Display.INSTANCE.shape(), //
        tensor).render(geometricLayer, graphics);
    // renderControlPoints(geometricLayer, graphics);
    if (1 < tensor.length()) {
      ManifoldDisplay manifoldDisplay = manifoldDisplay();
      Tensor control;
      switch (manifoldDisplay.toString()) {
      case "SE2C":
      case "SE2":
        // TODO use various options: unit vector, scaled by parametric distance, ...
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya, UnitVector.of(3, 0))));
        break;
      case "R2":
        // TODO use various options: unit vector, scaled by parametric distance, ...
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya.extract(0, 2), AngleVector.of(xya.Get(2)))));
        break;
      default:
        return;
      }
      {
        Tensor distances = new AdjacentDistances(ClothoidDistance.SE2_ANALYTIC).apply(tensor);
        // Distances.of(geodesicDisplay::parametricDistance, control.get(Tensor.ALL, 0));
        if (0 < distances.length()) {
          Tensor scaling = Array.zeros(control.length());
          scaling.set(distances.get(0), 0);
          for (int index = 1; index < distances.length(); ++index)
            scaling.set((Scalar) Mean.of(distances.extract(index - 1, index + 1)), index);
          scaling.set((Scalar) Last.of(distances), control.length() - 1);
          // ---
          for (int index = 0; index < control.length(); ++index) {
            int fi = index;
            control.set(t -> t.multiply(scaling.Get(fi)), index, 1);
          }
        }
      }
      Scalar delta = RealScalar.ONE;
      HermiteSubdivision hermiteSubdivision = scheme.supply( //
          manifoldDisplay.hsManifold(), //
          manifoldDisplay.hsTransport(), //
          manifoldDisplay.biinvariantMean());
      TensorIteration tensorIteration = hermiteSubdivision.string(delta, control);
      int levels = refine.number().intValue();
      Tensor iterate = Do.of(control, tensorIteration::iterate, levels);
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      Curvature2DRender.of(curve, false, geometricLayer, graphics);
      {
        Scalar scale = RealScalar.of(0.3);
        switch (manifoldDisplay.toString()) {
        case "SE2C":
        case "SE2":
          new Se2HermitePlot(iterate, scale).render(geometricLayer, graphics);
          break;
        case "R2":
          new R2HermitePlot(iterate, scale).render(geometricLayer, graphics);
          break;
        default:
        }
      }
      // ---
      if (diff) {
        Tensor deltas = iterate.get(Tensor.ALL, 1);
        if (0 < deltas.length()) {
          JFreeChart jFreeChart = StaticHelper.listPlot(deltas, delta, levels);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
  }

  public static void main(String[] args) {
    new HermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
