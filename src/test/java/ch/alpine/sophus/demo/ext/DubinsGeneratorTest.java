// code by jph
package ch.alpine.sophus.demo.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.ext.api.DubinsGenerator;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.MatrixQ;

public class DubinsGeneratorTest {
  @Test
  public void testSimple() {
    Tensor tensor = DubinsGenerator.of(Array.zeros(3), Tensors.fromString("{{1, 0, 0}, {1, 0, .3}}"));
    assertEquals(tensor.get(1), UnitVector.of(3, 0));
    assertTrue(MatrixQ.ofSize(tensor, 3, 3));
  }

  @Test
  public void testSingle() {
    Tensor init = Tensors.vector(1, 2, 3);
    Tensor tensor = DubinsGenerator.of(init, Tensors.empty());
    assertEquals(tensor, Tensors.of(init));
  }

  @Test
  public void testProject() {
    Tensor tensor = Tensors.fromString("{{-1, 0, 0}, {1, 1, 1}, {-1, 2, 2}, {0, 3, 5}}");
    Tensor project = DubinsGenerator.project(tensor);
    assertTrue(MatrixQ.ofSize(project, tensor.length(), 3));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> DubinsGenerator.of(Tensors.vector(1, 2, 3, 4), Tensors.fromString("{{1, 0, 0}, {1, 0, 0.3}}")));
  }
}
