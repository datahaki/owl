// code by jph
package ch.alpine.owl.hull;

import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;

@ReflectionMarker
public class HullParam extends RotParam {
  @FieldSelectionArray({ "50", "100", "200", "400" })
  public Scalar count = RealScalar.of(200);
  public Boolean cuboid = false;
  public ColorDataGradients cdg = ColorDataGradients.ALPINE;
  @FieldFuse
  public transient Boolean shuffle = true;
}
