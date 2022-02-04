// code by jph
package ch.alpine.sophus.gds;

import java.util.Random;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class R2Display extends RnDisplay {
  private static final Scalar RADIUS = RealScalar.of(1.0);
  // ---
  public static final ManifoldDisplay INSTANCE = new R2Display();

  private R2Display() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return VectorQ.requireLength(p, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return GfxMatrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return new R2ArrayPlot(RADIUS);
  }

  @Override // from GeodesicDisplay
  public RandomSampleInterface randomSampleInterface() {
    Distribution distribution = UniformDistribution.of(RADIUS.negate(), RADIUS);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        return RandomVariate.of(distribution, random, 2).append(RealScalar.ZERO);
      }
    };
  }
}
