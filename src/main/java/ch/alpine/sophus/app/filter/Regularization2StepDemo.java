// code by jph
package ch.alpine.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.app.BufferedImageSupplier;
import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymLinkImage;
import ch.alpine.sophus.app.sym.SymLinkImages;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.flt.ga.Regularization2Step;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.N;

/* package */ final class Regularization2StepDemo extends AbstractSpectrogramDemo implements BufferedImageSupplier {
  /** regularization parameter in the interval [0, 1] */
  private final JSlider jSlider = new JSlider(0, 1000, 600);

  Regularization2StepDemo() {
    super(ManifoldDisplays.SE2_R2, GokartPoseDataV2.INSTANCE);
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateState();
  }

  @Override // from AbstractDatasetFilterDemo
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    return Regularization2Step.string( //
        manifoldDisplay().geodesic(), //
        N.DOUBLE.apply(sliderRatio())).apply(control());
  }

  @Override // from UniformDatasetFilterDemo
  protected String plotLabel() {
    return "Regularization2Step " + sliderRatio();
  }

  private Scalar sliderRatio() {
    return RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    Scalar factor = sliderRatio();
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.string(SymGeodesic.INSTANCE, factor);
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.get(1), SymLinkImages.FONT_SMALL);
    symLinkImage.title("Regularization2Step [" + factor + "]");
    return symLinkImage.bufferedImage();
  }

  public static void main(String[] args) {
    new Regularization2StepDemo().setVisible(1200, 600);
  }
}
