// code by jph
package ch.alpine.ascona.hull;

import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.sophus.lie.so.Rodrigues;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

@ReflectionMarker
public class RotParam {
  public Boolean ccw = false;
  @FieldSlider
  @FieldClip(min = "-2", max = "+2")
  public Scalar t0 = RealScalar.of(0);
  @FieldSlider
  @FieldClip(min = "-2", max = "+2")
  public Scalar t1 = RealScalar.of(0);
  @FieldSlider
  @FieldClip(min = "-2", max = "+2")
  public Scalar t2 = RealScalar.of(0);

  public Tensor rotation() {
    return Rodrigues.vectorExp(Tensors.of(t0, t1, t2));
  }
}
