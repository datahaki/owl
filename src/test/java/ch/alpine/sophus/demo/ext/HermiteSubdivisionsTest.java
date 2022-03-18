// code by jph
package ch.alpine.sophus.demo.ext;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.api.TensorIteration;
import ch.alpine.sophus.ext.api.HermiteSubdivisions;
import ch.alpine.sophus.lie.LieTransport;
import ch.alpine.sophus.lie.rn.RnBiinvariantMean;
import ch.alpine.sophus.lie.rn.RnManifold;
import ch.alpine.sophus.lie.se2.Se2BiinvariantMeans;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2.Se2Manifold;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

public class HermiteSubdivisionsTest {
  @Test
  public void testStringReverseRn() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          RnManifold.INSTANCE, //
          LieTransport.INSTANCE, //
          RnBiinvariantMean.INSTANCE);
      TensorIteration ti1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration ti2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = ti1.iterate();
        Tensor result2 = Reverse.of(ti2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._12.requireClose(result1, result2);
      }
    }
  }

  @Test
  public void testStringReverseSe2() {
    Tensor cp1 = RandomVariate.of(UniformDistribution.unit(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration ti1 = hermiteSubdivision.string(RealScalar.ONE, cp1);
      TensorIteration ti2 = hermiteSubdivision.string(RealScalar.ONE, Reverse.of(cp2));
      for (int count = 0; count < 3; ++count) {
        Tensor result1 = ti1.iterate();
        Tensor result2 = Reverse.of(ti2.iterate());
        result2.set(Tensor::negate, Tensor.ALL, 1);
        Chop._08.requireClose(result1, result2);
      }
    }
  }

  @Test
  public void testSe2ConstantReproduction() {
    Tensor control = ConstantArray.of(Tensors.fromString("{{2, 3, 1}, {0, 0, 0}}"), 10);
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      Tensor iterate = Do.of(tensorIteration::iterate, 2);
      Chop._13.requireAllZero(iterate.get(Tensor.ALL, 1));
    }
  }

  @Test
  public void testSe2LinearReproduction() {
    Tensor pg = Tensors.vector(1, 2, 3);
    Tensor pv = Tensors.vector(0.3, -0.2, -0.1);
    Tensor control = Tensors.empty();
    for (int count = 0; count < 10; ++count) {
      control.append(Tensors.of(pg, pv));
      pg = Se2Group.INSTANCE.element(pg).combine(Se2CoveringExponential.INSTANCE.exp(pv));
    }
    control = control.unmodifiable();
    for (HermiteSubdivisions hermiteSubdivisions : HermiteSubdivisions.values()) {
      // System.out.println(hermiteSubdivisions);
      HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
          Se2Manifold.INSTANCE, //
          LieTransport.INSTANCE, //
          Se2BiinvariantMeans.LINEAR);
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      Tensor iterate = Do.of(tensorIteration::iterate, 2);
      for (Tensor rv : iterate.get(Tensor.ALL, 1))
        Chop._13.requireClose(pv, rv);
    }
  }
}
