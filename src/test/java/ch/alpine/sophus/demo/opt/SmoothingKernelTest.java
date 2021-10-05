// code by jph
package ch.alpine.sophus.demo.opt;

import java.util.function.Function;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.math.SymmetricVectorQ;
import ch.alpine.sophus.math.win.UniformWindowSampler;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.win.DirichletWindow;
import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class SmoothingKernelTest extends TestCase {
  private static Tensor constant(int i) {
    int width = 2 * i + 1;
    Scalar weight = RationalScalar.of(1, width);
    return Tensors.vector(k -> weight, width);
  }

  public void testInstance() {
    assertEquals(WindowFunctions.DIRICHLET.get(), DirichletWindow.FUNCTION);
  }

  public void testConstant() {
    Function<Integer, Tensor> uniformWindowSampler = UniformWindowSampler.of(WindowFunctions.DIRICHLET.get());
    for (int radius = 0; radius < 5; ++radius) {
      Tensor tensor = uniformWindowSampler.apply(radius * 2 + 1);
      assertEquals(tensor, constant(radius));
      ExactTensorQ.require(tensor);
      assertEquals(Total.of(tensor), RealScalar.ONE);
    }
  }

  public void testHann() {
    Function<Integer, Tensor> centerWindowSampler = UniformWindowSampler.of(WindowFunctions.HANN.get());
    ExactTensorQ.require(centerWindowSampler.apply(1));
  }

  public void testAll() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
      Function<Integer, Tensor> uniformWindowSampler = UniformWindowSampler.of(smoothingKernel.get());
      for (int radius = 0; radius < 5; ++radius) {
        Tensor tensor = uniformWindowSampler.apply(radius * 2 + 1);
        SymmetricVectorQ.require(tensor);
        Chop._13.requireClose(Total.of(tensor), RealScalar.ONE);
        assertFalse(Scalars.isZero(tensor.Get(0)));
        assertFalse(Scalars.isZero(tensor.Get(tensor.length() - 1)));
        assertEquals(tensor.length(), 2 * radius + 1);
      }
    }
  }

  public void testSymmetric() {
    for (int size = 0; size < 5; ++size) {
      Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 2, 3, 4);
      for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
        Tensor v1 = tensor.map(smoothingKernel.get());
        Tensor v2 = tensor.negate().map(smoothingKernel.get());
        assertEquals(v1, v2);
      }
    }
  }

  public void testContinuity() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
      Scalar scalar = smoothingKernel.get().apply(RationalScalar.HALF);
      String string = smoothingKernel.get() + "[1/2]=" + scalar;
      string.length();
      Function<Integer, Tensor> uniformWindowSampler = UniformWindowSampler.of(smoothingKernel.get());
      Tensor vector = uniformWindowSampler.apply(1);
      assertEquals(vector, Tensors.of(RealScalar.ONE));
      assertTrue(Scalars.lessThan(RealScalar.of(1e-3), Abs.of(vector.Get(0))));
    }
  }

  public void testZeroFail() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
      Function<Integer, Tensor> uniformWindowSampler = UniformWindowSampler.of(smoothingKernel.get());
      AssertFail.of(() -> uniformWindowSampler.apply(0));
    }
  }

  public void testAllFail() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values()) {
      Function<Integer, Tensor> centerWindowSampler = UniformWindowSampler.of(smoothingKernel.get());
      AssertFail.of(() -> centerWindowSampler.apply(-1));
    }
  }

  public void testAllFailQuantity() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      AssertFail.of(() -> smoothingKernel.get().apply(Quantity.of(1, "s")));
  }
}
