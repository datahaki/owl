// code by jph
package ch.alpine.owl.hull;

import ch.alpine.bridge.ref.Cacheable;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.sophis.srf.SurfaceMesh;

@ReflectionMarker
class MeshParam extends Cacheable {
  public RandomMethod method = RandomMethod.SPHERE;
  @FieldSelectionArray({ "50", "100", "200", "400" })
  public Integer count = 200;
  @FieldFuse
  public transient Boolean shuffle = false;

  public SurfaceMesh mesh() {
    return method.mesh(count);
  }
}
