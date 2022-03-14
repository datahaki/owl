// code by jph
package ch.alpine.sophus.demo.ref.d2;

import java.util.function.Function;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.ref.d2.CatmullClarkRefinement;
import ch.alpine.sophus.ref.d2.DooSabinRefinement;
import ch.alpine.sophus.ref.d2.TriQuadLinearRefinement;
import ch.alpine.sophus.ref.d2.SurfaceMeshRefinement;

public enum SurfaceMeshRefinements {
  LINEAR(TriQuadLinearRefinement::new), //
  DOO_SABIN(DooSabinRefinement::new), //
  CATMULL_CLARK(CatmullClarkRefinement::new), //
  ;

  private final Function<BiinvariantMean, SurfaceMeshRefinement> function;

  SurfaceMeshRefinements(Function<BiinvariantMean, SurfaceMeshRefinement> function) {
    this.function = function;
  }
  
  public SurfaceMeshRefinement operator(BiinvariantMean biinvariantMean) {
    return function.apply(biinvariantMean);
  }
}
