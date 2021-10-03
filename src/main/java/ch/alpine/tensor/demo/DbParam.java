// code by jph
package ch.alpine.tensor.demo;

import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class DbParam {
  @FieldInteger
  @FieldClip(min = "1", max = "1000")
  public Scalar count = RealScalar.of(100);
  @FieldInteger
  public Scalar dep = RealScalar.of(5);
  public CenterNorms centerNorms = CenterNorms._2;
}
