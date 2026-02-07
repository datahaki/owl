// code by jph
package ch.alpine.ascona.hull;

import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

@ReflectionMarker
public class HullParam extends RotParam {
  @FieldSelectionArray({ "50", "100", "200", "400" })
  public Scalar count = RealScalar.of(200);
  public Boolean quality = false;
  public Boolean cuboid = false;
  @FieldFuse
  public transient Boolean shuffle = true;
}
