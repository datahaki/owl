// code by jph
package ch.alpine.sophus.ext.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class ControlMidpointsTest {
  @Test
  public void testSimple() {
    CurveSubdivision curveSubdivision = ControlMidpoints.of(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(Tensors.vector(1, 2, 3));
    assertEquals(tensor.toString(), "{1, 3/2, 5/2, 3}");
  }
}
