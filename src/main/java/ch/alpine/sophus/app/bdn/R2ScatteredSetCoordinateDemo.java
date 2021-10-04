// code by jph
package ch.alpine.sophus.app.bdn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.app.opt.LogWeightings;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.ArrayPlotRender;
import ch.alpine.sophus.gui.ren.ArrayRender;
import ch.alpine.sophus.lie.rn.RnManifold;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.red.Entrywise;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
// TODO possibly only recompute when points have changed
/* package */ class R2ScatteredSetCoordinateDemo extends AbstractScatteredSetWeightingDemo {
  private static final double RANGE = 5;
  // ---
  private final JToggleButton jToggleButtonAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshot;

  public R2ScatteredSetCoordinateDemo() {
    super(true, ManifoldDisplays.SE2C_SE2, LogWeightings.list());
    {
      jToggleButtonAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleButtonAxes);
    }
    {
      jToggleAnimate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (jToggleAnimate.isSelected())
            snapshot = getControlPointsSe2();
          else
            setControlPointsSe2(snapshot);
        }
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{2, -3, 1.5}, {3, 5, 1}, {-4, -3, 1}, {-5, 3, 2}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-1.217, -2.050, 1.309}, {1.783, 1.917, 0.262}, {-3.583, 0.300, -0.262}, {2.200, -0.283, 0.262}, {-4.000, -3.000, 1.000}, {-1.900, 2.117, 1.309}}"));
    jToggleButtonAxes.setSelected(false);
    timerFrame.geometricComponent.setOffset(500, 500);
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2) * 2);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ColorDataGradient colorDataGradient = colorDataGradient();
    if (jToggleButtonAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    if (jToggleAnimate.isSelected()) {
      double toc = timing.seconds() * 0.3;
      int n = snapshot.length();
      Tensor control = Tensors.reserve(n);
      for (int index = 0; index < n; ++index) { //
        control.append(snapshot.get(index).add(random(toc, index)));
      }
      setControlPointsSe2(control);
    }
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    BiinvariantMean biinvariantMean = manifoldDisplay.biinvariantMean();
    if (2 < controlPoints.length()) {
      Tensor domain = Tensor.of(controlPoints.stream().map(manifoldDisplay::toPoint));
      RenderQuality.setQuality(graphics);
      // ---
      TensorUnaryOperator tensorUnaryOperator = operator(RnManifold.INSTANCE, domain);
      Tensor min = Entrywise.min().of(domain).map(RealScalar.of(0.01)::add);
      Tensor max = Entrywise.max().of(domain).map(RealScalar.of(0.01)::subtract).negate();
      min = Tensors.vector(-RANGE, -RANGE);
      max = Tensors.vector(+RANGE, +RANGE);
      {
        Tensor sq = Tensors.matrixDouble(new double[][] { { -RANGE, -RANGE }, { RANGE, -RANGE }, { RANGE, RANGE }, { -RANGE, RANGE } });
        Path2D path2d = geometricLayer.toPath2D(sq, true);
        graphics.setColor(Color.GRAY);
        graphics.draw(path2d);
      }
      Tensor sX = Subdivide.of(min.Get(0), max.Get(0), refinement());
      Tensor sY = Subdivide.of(max.Get(1), min.Get(1), refinement());
      int n = sX.length();
      Tensor[][] array = new Tensor[n][n];
      Tensor[][] point = new Tensor[n][n];
      Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n, n, domain.length());
      IntStream.range(0, sX.length()).parallel().forEach(c0 -> {
        Tensor x = sX.get(c0);
        int c1 = 0;
        for (Tensor y : sY) {
          Tensor px = Tensors.of(x, y);
          Tensor weights = tensorUnaryOperator.apply(px);
          wgs.set(weights, c1, c0);
          Tensor mean = biinvariantMean.mean(controlPoints, weights);
          array[c0][c1] = mean;
          point[c0][c1] = manifoldDisplay.toPoint(mean);
          ++c1;
        }
        ++c0;
      });
      // ---
      new ArrayRender(point, colorDataGradient.deriveWithOpacity(RationalScalar.HALF)).render(geometricLayer, graphics);
      // ---
      if (jToggleHeatmap.isSelected()) { // render basis functions
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        ArrayPlotRender.rescale(_wgs, colorDataGradient, 3).render(geometricLayer, graphics);
      }
      // render grid lines functions
      if (jToggleArrows.isSelected()) {
        graphics.setColor(Color.LIGHT_GRAY);
        Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(Math.min(1, 3.0 / Math.sqrt(refinement()))));
        for (int i0 = 0; i0 < array.length; ++i0)
          for (int i1 = 0; i1 < array.length; ++i1) {
            Tensor mean = array[i0][i1];
            geometricLayer.pushMatrix(manifoldDisplay.matrixLift(mean));
            graphics.setColor(new Color(128, 128, 128, 64));
            graphics.fill(geometricLayer.toPath2D(shape, true));
            geometricLayer.popMatrix();
          }
      }
    }
    LeversRender leversRender = //
        LeversRender.of(manifoldDisplay, controlPoints, null, geometricLayer, graphics);
    leversRender.renderSequence();
    leversRender.renderIndexP("q");
  }

  public static void main(String[] args) {
    new R2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
