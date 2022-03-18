// code by jph
package ch.alpine.sophus.demo.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.io.ResourceData;

public class GokartPoseDataTest {
  @Test
  public void testSimple() {
    List<String> list = GokartPoseDataV2.INSTANCE.list();
    assertTrue(50 < list.size());
  }

  @Test
  public void testResourceTensor() {
    Tensor tensor = ResourceData.of("/colorscheme/aurora.csv"); // resource in tensor
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }

  @Test
  public void testResourceOwl() {
    Tensor tensor = ResourceData.of("/io/delta_free.png"); // resource in owl
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }
}
