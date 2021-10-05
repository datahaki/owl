// code by jph
package ch.alpine.sophus.demo.io;

import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.io.ResourceData;
import junit.framework.TestCase;

public class GokartPoseDataTest extends TestCase {
  public void testSimple() {
    List<String> list = GokartPoseDataV2.INSTANCE.list();
    assertTrue(50 < list.size());
  }

  public void testResourceTensor() {
    Tensor tensor = ResourceData.of("/colorscheme/aurora.csv"); // resource in tensor
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }

  public void testResourceOwl() {
    Tensor tensor = ResourceData.of("/io/delta_free.png"); // resource in owl
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }
}
