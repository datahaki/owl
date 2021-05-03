// code by jph
package ch.alpine.owl.math.noise;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.UnitStep;

enum R2NoisePlot {
  ;
  // ---
  private static final NativeContinuousNoise NOISE = SimplexContinuousNoise.FUNCTION;
  // PolyNoise.of(SimplexNoise.FUNCTION, new double[] { 1, 0 }, new double[] { .3, 3 });
  private static final int RES = 512;
  private static final Tensor RE = Subdivide.of(0, 25, RES - 1);
  private static final Tensor IM = Subdivide.of(0, 25, RES - 1);
  @SuppressWarnings("unused")
  private static final Clip CLIP = Clips.unit();

  private static Scalar function(int x, int y) {
    return UnitStep.of(DoubleScalar.of(NOISE.at( //
        RE.Get(x).number().doubleValue(), //
        IM.Get(y).number().doubleValue())).subtract(RealScalar.of(0.9)));
  }

  public static void main(String[] args) throws Exception {
    Tensor matrix = Tensors.matrix(R2NoisePlot::function, RES, RES);
    Export.of(HomeDirectory.Pictures("perlinnoise.png"), //
        ArrayPlot.of(matrix, ColorDataGradients.COPPER));
  }
}
