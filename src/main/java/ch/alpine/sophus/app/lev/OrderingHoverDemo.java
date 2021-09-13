// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

// FIXME pressing shuffle button crashes app
/* package */ class OrderingHoverDemo extends AbstractHoverDemo {
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());

  public OrderingHoverDemo() {
    {
      spinnerLength.addSpinnerListener(v -> recompute());
      spinnerLength.setList(Arrays.asList(50, 75, 100, 200, 300, 400, 500, 800));
      spinnerLength.setValue(200);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    {
      spinnerColorData.setValueSafe(ColorDataGradients.THERMOMETER);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color");
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    setLogWeighting(LogWeightings.DISTANCES);
    addSpinnerListener(v -> recompute());
    recompute();
  }

  private TensorUnaryOperator tensorUnaryOperator;

  @Override // from LogWeightingDemo
  protected void recompute() {
    System.out.println("recompute");
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    Tensor sequence = RandomVariate.of(distribution, spinnerLength.getValue(), 3);
    setControlPointsSe2(sequence);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    VectorLogManifold vectorLogManifold = manifoldDisplay.hsManifold();
    tensorUnaryOperator = //
        logWeighting().operator(biinvariant(), vectorLogManifold, variogram(), getGeodesicControlPoints());
  }

  @Override // from AbstractHoverDemo
  void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor sequence = leversRender.getSequence();
    Tensor origin = leversRender.getOrigin();
    Tensor weights = tensorUnaryOperator.apply(origin);
    // ---
    OrderingHelper.of(manifoldDisplay, origin, sequence, weights, spinnerColorData.getValue(), geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new OrderingHoverDemo().setVisible(1200, 600);
  }
}
