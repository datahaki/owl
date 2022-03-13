package ch.alpine.sophus.demo.ref.d2;

import java.util.List;

import ch.alpine.sophus.lie.rn.RnBiinvariantMean;
import ch.alpine.sophus.math.DirectedEdge;
import ch.alpine.sophus.ref.d2.DooSabinRefinement;
import ch.alpine.sophus.ref.d2.SurfaceMeshRefinement;
import ch.alpine.sophus.srf.MeshStructure;
import ch.alpine.sophus.srf.SurfaceMesh;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class DooSabinDemoTest extends TestCase {
  public void testSimple() {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        new DooSabinRefinement(RnBiinvariantMean.INSTANCE);
    SurfaceMesh surfaceMesh = SurfaceMeshExamples.mixed7();
    // surfaceMeshRefinement.refine(surfaceMesh);
  }

  public void testSimple2() {
    SurfaceMesh surfaceMesh = SurfaceMeshExamples.mixed7();
    assertEquals(surfaceMesh.vrt.get(3), Tensors.vector(2, 2, 0));
    assertEquals(Tensors.vectorInt(surfaceMesh.face(0)), Tensors.vectorInt(0, 2, 3, 1));
    // surfaceMeshRefinement.refine(surfaceMesh);
    MeshStructure meshStructure = new MeshStructure(surfaceMesh);
    List<DirectedEdge> list = meshStructure.ring(new DirectedEdge(3, 1));
    assertEquals(list.stream().distinct().count(), list.size());
  }
}
