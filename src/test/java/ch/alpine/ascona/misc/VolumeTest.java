// code by jph
package ch.alpine.ascona.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.ascona.hull.SurfaceMeshDemo;
import ch.alpine.qhull3d.PlatonicSolid;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophis.srf.Volume;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;

class VolumeTest {
  @Test
  void testCube() {
    Scalar scalar = Volume.of(SurfaceMeshDemo.surfaceMesh(PlatonicSolid.CUBE));
    assertEquals(scalar, RealScalar.ONE);
    ExactScalarQ.require(scalar);
  }

  @Test
  void testCubeUnits() {
    SurfaceMesh surfaceMesh = SurfaceMeshDemo.surfaceMesh(PlatonicSolid.CUBE);
    surfaceMesh.vrt = surfaceMesh.vrt.map(s -> Quantity.of(s, "m"));
    Scalar scalar = Volume.of(surfaceMesh);
    assertEquals(scalar, Quantity.of(1, "m^3"));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testDodeca() {
    Scalar scalar = Volume.of(SurfaceMeshDemo.surfaceMesh(PlatonicSolid.DODECAHEDRON));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(7.663118960624632));
  }
}
