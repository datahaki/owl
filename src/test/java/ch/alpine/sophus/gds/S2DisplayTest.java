// code by jph
package ch.alpine.sophus.gds;

import java.util.Arrays;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import junit.framework.TestCase;

public class S2DisplayTest extends TestCase {
  public void testSimple() {
    Tensor tensor = S2Display.tangentSpace(Tensors.vector(0, 1, 0));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 3));
  }

  public void testInvariant() {
    ManifoldDisplay manifoldDisplay = S2Display.INSTANCE;
    Tensor xyz = manifoldDisplay.project(Tensors.vector(1, 2, 0));
    Tensor xy = manifoldDisplay.toPoint(xyz);
    Tolerance.CHOP.requireClose(Vector2Norm.of(xy), RealScalar.ONE);
  }

  public void testTangent() {
    Tensor xyz = Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 0.3, 0.5));
    Tensor matrix = S2Display.tangentSpace(xyz);
    assertEquals(Dimensions.of(matrix), Arrays.asList(2, 3));
    Tolerance.CHOP.requireAllZero(matrix.dot(xyz));
  }

  public void testProjTangent() {
    S2Display s2GeodesicDisplay = (S2Display) S2Display.INSTANCE;
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(NormalDistribution.standard(), 3);
      Tensor xyz = s2GeodesicDisplay.project(xya);
      Tensor tan = s2GeodesicDisplay.createTangent(xya);
      Tolerance.CHOP.requireAllZero(xyz.dot(tan));
    }
  }

  public void testFail() {
    AssertFail.of(() -> S2Display.tangentSpace(Tensors.vector(1, 1, 1)));
  }
}
