// code by jph
package ch.alpine.sophus.app.bd2;

import java.awt.image.BufferedImage;

import ch.alpine.sophus.gds.GeodesicArrayPlot;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.ArrayPlotRender;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradient;

/* package */ enum HilbertLevelImage {
  ;
  public static BufferedImage of(ManifoldDisplay manifoldDisplay, Tensor sequence, int res, ColorDataGradient colorDataGradient, int max) {
    TensorUnaryOperator tuo = IterativeGenesis.counts(manifoldDisplay.hsManifold(), sequence, max);
    int sequence_length = IterativeGenesis.values().length;
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence_length);
    GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay.geodesicArrayPlot();
    Tensor wgs = geodesicArrayPlot.raster(res, tuo, fallback);
    ArrayPlotRender arrayPlotRender = StaticHelper.arrayPlotFromTensor(wgs, 1, false, colorDataGradient);
    return StaticHelper.fuseImages(manifoldDisplay, arrayPlotRender, res, sequence_length);
  }
}
