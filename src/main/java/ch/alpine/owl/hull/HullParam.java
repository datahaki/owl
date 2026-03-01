// code by jph
package ch.alpine.owl.hull;

import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.img.ColorDataGradients;

@ReflectionMarker
public class HullParam {
  public final MeshParam meshParam = new MeshParam();
  public Boolean cuboid = false;
  public final RotParam rotParam = new RotParam();
  public ColorDataGradients cdg = ColorDataGradients.ALPINE;
  /** IMPORTANT: the value shuffle == true is used to initialized */
}
