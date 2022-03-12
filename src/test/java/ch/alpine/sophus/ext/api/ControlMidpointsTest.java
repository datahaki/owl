// code by jph
package ch.alpine.sophus.ext.api;

import ch.alpine.sophus.ext.api.ControlMidpoints;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class ControlMidpointsTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = ControlMidpoints.of(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(Tensors.vector(1, 2, 3));
    assertEquals(tensor.toString(), "{1, 3/2, 5/2, 3}");
  }
}
