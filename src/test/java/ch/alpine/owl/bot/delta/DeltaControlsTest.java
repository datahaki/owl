// code by jph
package ch.alpine.owl.bot.delta;

import java.util.Collection;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class DeltaControlsTest extends TestCase {
  public void testFlows() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        ResourceData.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(0.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Sign.isPositive(maxNormGradient));
    Scalar amp = RealScalar.of(2);
    new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Tensor> controls = new DeltaFlows(amp).getFlows(20);
    Scalar max = DeltaControls.maxSpeed(controls);
    Chop._12.requireClose(max, amp);
  }
}
