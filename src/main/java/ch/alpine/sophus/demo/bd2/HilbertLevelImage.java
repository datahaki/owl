// code by jph
package ch.alpine.sophus.demo.bd2;

import java.awt.image.BufferedImage;

import ch.alpine.java.ren.ArrayPlotRender;
import ch.alpine.sophus.ext.arp.HsArrayPlot;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
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
    HsArrayPlot geodesicArrayPlot = manifoldDisplay.geodesicArrayPlot();
    Tensor wgs = geodesicArrayPlot.raster(res, tuo, fallback);
    ArrayPlotRender arrayPlotRender = StaticHelper.arrayPlotFromTensor(wgs, 1, false, colorDataGradient);
    return StaticHelper.fuseImages(manifoldDisplay, arrayPlotRender, res, sequence_length);
  }
}
