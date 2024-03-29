// code by jph
package ch.alpine.owl.bot.delta;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class DeltaControlsTest {
  @Test
  void testFlows() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        Import.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(0.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Sign.isPositive(maxNormGradient));
    Scalar amp = RealScalar.of(2);
    Collection<Tensor> controls = new DeltaFlows(amp).getFlows(20);
    Scalar max = DeltaControls.maxSpeed(controls);
    Chop._12.requireClose(max, amp);
  }
}
