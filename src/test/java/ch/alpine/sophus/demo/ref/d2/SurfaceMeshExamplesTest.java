package ch.alpine.sophus.demo.ref.d2;

import ch.alpine.sophus.lie.rn.RnBiinvariantMean;
import ch.alpine.sophus.ref.d2.SurfaceMeshRefinement;
import ch.alpine.sophus.srf.SurfaceMesh;
import junit.framework.TestCase;

public class SurfaceMeshExamplesTest extends TestCase {
  public void testMixed7() {
    for (SurfaceMeshRefinements smr : SurfaceMeshRefinements.values()) {
      SurfaceMeshRefinement refinement = smr.operator(RnBiinvariantMean.INSTANCE);
      SurfaceMesh refine = refinement.refine(SurfaceMeshExamples.mixed7());
      refinement.refine(refine);
    }
  }

  public void testMixed11() {
    for (SurfaceMeshRefinements smr : SurfaceMeshRefinements.values()) {
      SurfaceMeshRefinement refinement = smr.operator(RnBiinvariantMean.INSTANCE);
      SurfaceMesh refine = refinement.refine(SurfaceMeshExamples.mixed11());
      refinement.refine(refine);
    }
  }
}
