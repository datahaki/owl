// code by jph, ob
package ch.alpine.sophus.demo.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.demo.BufferedImageSupplier;
import ch.alpine.sophus.demo.io.GokartPoseDataV1;
import ch.alpine.sophus.demo.opt.GeodesicFilters;
import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.sym.SymLinkImages;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Nest;

/* package */ class GeodesicFiltersDatasetDemo extends AbstractDatasetKernelDemo implements BufferedImageSupplier {
  private final SpinnerLabel<GeodesicFilters> spinnerFilters = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();

  public GeodesicFiltersDatasetDemo() {
    super(ManifoldDisplays.SE2_R2, GokartPoseDataV1.INSTANCE);
    {
      spinnerFilters.setArray(GeodesicFilters.values());
      spinnerFilters.setIndex(0);
      spinnerFilters.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "filter type");
      spinnerFilters.addSpinnerListener(type -> updateState());
    }
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    spinnerLabelString.setIndex(15);
    // ---
    updateState();
  }

  @Override // from UniformDatasetFilterDemo
  protected void updateState() {
    super.updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    ScalarUnaryOperator smoothingKernel = spinnerKernel.getValue().get();
    GeodesicFilters geodesicFilters = spinnerFilters.getValue();
    TensorUnaryOperator tensorUnaryOperator = geodesicFilters.from(manifoldDisplay, smoothingKernel);
    return Nest.of( //
        CenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
        control(), spinnerConvolution.getValue());
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    GeodesicFilters geodesicFilters = spinnerFilters.getValue();
    switch (geodesicFilters) {
    case GEODESIC:
      return SymLinkImages.ofGC(spinnerKernel.getValue().get(), spinnerRadius.getValue()).bufferedImage();
    default:
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
  }

  public static void main(String[] args) {
    new GeodesicFiltersDatasetDemo().setVisible(1000, 800);
  }
}
