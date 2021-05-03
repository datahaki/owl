// code by jph
package ch.alpine.sophus.app.subdiv;

import junit.framework.TestCase;

public class CurveSubdivisionSchemesTest extends TestCase {
  public void testSimple() {
    int count = 0;
    for (CurveSubdivisionSchemes curveSubdivisionSchemes : CurveSubdivisionSchemes.values())
      if (curveSubdivisionSchemes.isStringSupported())
        ++count;
    assertTrue(10 < count);
  }
}
