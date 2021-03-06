// code by jph
package ch.alpine.owl.bot.rn;

import java.io.IOException;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.Vector2Norm;
import junit.framework.TestCase;

public class RnTransitionTest extends TestCase {
  public void testSampled() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = //
        Serialization.copy(RnTransitionSpace.INSTANCE.connect(start, end));
    Tensor tensor = rnTransition.sampled(RealScalar.of(0.1));
    assertEquals(tensor.length(), 90);
  }

  public void testLinearized() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
    Tensor linearized = rnTransition.linearized(RealScalar.of(0.1));
    assertEquals(linearized, Tensors.fromString("{{1, 2}, {10, 2}}"));
  }

  public void testWrapped() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
    TransitionWrap transitionWrap = rnTransition.wrapped(RealScalar.of(0.1));
    Tensor samples = transitionWrap.samples();
    Tensor spacing = transitionWrap.spacing();
    assertEquals(samples.length(), spacing.length());
    ExactTensorQ.of(spacing);
    Tensor diffnor = Tensor.of(Differences.of(samples).stream().map(Vector2Norm::of));
    assertEquals(spacing.extract(0, diffnor.length()), diffnor);
  }

  public void testFail() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
    rnTransition.sampled(RealScalar.of(100));
    AssertFail.of(() -> rnTransition.sampled(RealScalar.ZERO));
    AssertFail.of(() -> rnTransition.sampled(RealScalar.of(-0.1)));
  }
}
