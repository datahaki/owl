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
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.api.TensorIteration;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.HermiteSubdivisions;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.NumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.N;

public class SeriesHermiteSubdivisionDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  public HermiteSubdivisions scheme = HermiteSubdivisions.HERMITE1;
  @FieldSlider
  @FieldPreferredWidth(100)
  @FieldInteger
  @FieldClip(min = "0", max = "8")
  public Scalar refine = RealScalar.of(4);
  @FieldPreferredWidth(300)
  public Tensor coeffs = Tensors.fromString("{2, 1, -1/5, -1/10}");
  // ---
  public Boolean derivatives = true;

  public SeriesHermiteSubdivisionDemo() {
    super(false, ManifoldDisplays.R2_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar).addUniversalListener(this::compute);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    compute();
  }

  Tensor _control = Tensors.empty();

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    if (1 < _control.length()) {
      ManifoldDisplay manifoldDisplay = manifoldDisplay();
      HermiteSubdivision hermiteSubdivision = //
          scheme.supply( //
              manifoldDisplay.hsManifold(), //
              manifoldDisplay.hsTransport(), //
              manifoldDisplay.biinvariantMean());
      Tensor control = N.DOUBLE.of(_control);
      Scalar delta = RealScalar.ONE;
      TensorIteration tensorIteration = hermiteSubdivision.string(delta, control);
      int levels = refine.number().intValue();
      Tensor iterate = Do.of(control, tensorIteration::iterate, levels);
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      Curvature2DRender.of(curve, false, geometricLayer, graphics);
      // ---
      if (derivatives) {
        Tensor deltas = iterate.get(Tensor.ALL, 1);
        if (0 < deltas.length()) {
          JFreeChart jFreeChart = StaticHelper.listPlot(deltas, delta, levels);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
  }

  private void compute() {
    Tensor _coeffs = coeffs;
    if (VectorQ.of(_coeffs) && //
        NumberQ.all(_coeffs)) {
      Polynomial f0 = Polynomial.of(_coeffs);
      ScalarUnaryOperator f1 = f0.derivative();
      Tensor vx0 = Range.of(-4, 5);
      Tensor vd0 = vx0.map(f0);
      Tensor vx1 = ConstantArray.of(RealScalar.ONE, vx0.length());
      Tensor vd1 = vx0.map(f1);
      Tensor p0 = Transpose.of(Tensors.of(vx0, vd0));
      Tensor p1 = Transpose.of(Tensors.of(vx1, vd1));
      _control = Transpose.of(Tensors.of(p0, p1));
      setControlPointsSe2(Tensor.of(p0.stream().map(Tensor::copy).map(r -> r.append(RealScalar.ZERO))));
    }
  }

  public static void main(String[] args) {
    LookAndFeels.DARK.updateUI();
    new SeriesHermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
