// code by jph
package ch.alpine.owl.bot.delta;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.sca.Chop;

class DeltaMinTimeGoalManagerTest {
  @Test
  void testConstructors() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        Import.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(0.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Scalars.lessThan(RealScalar.ZERO, maxNormGradient));
    Scalar amp = RealScalar.of(2);
    Collection<Tensor> controls = new DeltaFlows(amp).getFlows(20);
    Chop._10.requireClose(DeltaControls.maxSpeed(controls), amp);
    Scalar maxMove = DeltaControls.maxSpeed(controls).add(imageGradientInterpolation.maxNormGradient());
    Chop._10.requireClose(maxMove, imageGradientInterpolation.maxNormGradient().add(amp));
    RegionWithDistance<Tensor> regionWithDistance = //
        new BallRegion(Tensors.vector(1, 1), RealScalar.ONE);
    GoalInterface dmtgm = new DeltaMinTimeGoalManager(regionWithDistance, maxMove);
    assertTrue(HeuristicQ.of(dmtgm));
  }
}
