// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.app.opt.LogWeightings;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

/* package */ class OrderingPlaceDemo extends LogWeightingDemo {
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("shuffle");
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());

  public OrderingPlaceDemo() {
    super(true, ManifoldDisplays.MANIFOLDS, LogWeightings.list());
    {
      spinnerLength.addSpinnerListener(v -> shuffleSnap());
      spinnerLength.setList(Arrays.asList(50, 75, 100, 200, 300, 400, 500));
      spinnerLength.setValue(200);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    jButton.addActionListener(l -> shuffleSnap());
    timerFrame.jToolBar.add(jButton);
    {
      spinnerColorData.setValueSafe(ColorDataGradients.THERMOMETER);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color");
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    setLogWeighting(LogWeightings.DISTANCES);
    shuffleSnap();
  }

  private void shuffleSnap() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    Tensor sequence = RandomVariate.of(distribution, spinnerLength.getValue(), 3);
    sequence.set(OrderingHelper.FACTOR::multiply, Tensor.ALL, 1);
    sequence.set(Scalar::zero, 0, Tensor.ALL);
    setControlPointsSe2(sequence);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      VectorLogManifold vectorLogManifold = manifoldDisplay.hsManifold();
      TensorUnaryOperator tensorUnaryOperator = //
          logWeighting().operator(biinvariant(), vectorLogManifold, variogram(), sequence);
      Tensor weights = tensorUnaryOperator.apply(origin);
      OrderingHelper.of(manifoldDisplay, origin, sequence, weights, spinnerColorData.getValue(), geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new OrderingPlaceDemo().setVisible(1200, 600);
  }
}
