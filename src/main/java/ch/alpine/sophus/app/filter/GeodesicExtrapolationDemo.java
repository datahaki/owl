// code by ob
package ch.alpine.sophus.app.filter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.alpine.java.awt.BufferedImageSupplier;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymLinkImage;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.flt.ga.GeodesicExtrapolation;
import ch.alpine.sophus.flt.ga.GeodesicExtrapolationFilter;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;

/* package */ class GeodesicExtrapolationDemo extends AbstractDatasetKernelDemo implements BufferedImageSupplier {
  private Tensor refined = Tensors.empty();

  public GeodesicExtrapolationDemo() {
    super(GeodesicDisplays.SE2_R2, GokartPoseDataV2.INSTANCE);
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
    // ---
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicExtrapolation.of(manifoldDisplay().geodesicInterface(), spinnerKernel.getValue().get());
    refined = GeodesicExtrapolationFilter.of(tensorUnaryOperator, manifoldDisplay().geodesicInterface(), spinnerRadius.getValue()).apply(control());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    return refined;
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue().get();
    int radius = spinnerRadius.getValue();
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(SymGeodesic.INSTANCE, smoothingKernel);
    Tensor vector = Tensor.of(IntStream.range(0, radius + 1).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, SymLinkImage.FONT_SMALL);
    symLinkImage.title(smoothingKernel + "[" + (radius + 1) + "]");
    return symLinkImage.bufferedImage();
  }

  public static void main(String[] args) {
    new GeodesicExtrapolationDemo().setVisible(1000, 600);
  }
}