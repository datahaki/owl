// code by jph
package ch.alpine.sophus.demo.io;

import java.io.IOException;

import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.hs.sn.S2Loxodrome;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ enum LoxodromeData {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Subdivide.of(0, 4.5, 250).map(AbsSquared.FUNCTION).map(S2Loxodrome.of(RealScalar.of(0.15)));
    Export.of(HomeDirectory.file("loxodrome_exact.csv"), tensor);
    Tensor noise = RandomVariate.of(NormalDistribution.of(0, 0.05), Dimensions.of(tensor));
    tensor = tensor.add(noise);
    tensor = Tensor.of(tensor.stream().map(Vector2Norm.NORMALIZE));
    Export.of(HomeDirectory.file("loxodrome_noise.csv"), tensor);
    Geodesic geodesicInterface = S2Display.INSTANCE.geodesic();
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      ScalarUnaryOperator smoothingKernel = windowFunctions.get();
      TensorUnaryOperator tensorUnaryOperator = //
          new CenterFilter(GeodesicCenter.of(geodesicInterface, smoothingKernel), 7);
      Tensor smooth = tensorUnaryOperator.apply(tensor);
      Export.of(HomeDirectory.file("loxodrome_" + smoothingKernel + ".csv"), smooth);
    }
  }
}
