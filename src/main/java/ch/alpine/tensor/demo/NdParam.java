// code by jph
package ch.alpine.tensor.demo;

import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class NdParam {
  @FieldInteger
  public Scalar leafSizeMax = RealScalar.of(5);
  @FieldInteger
  @FieldClip(min = "1", max = "10000")
  public Scalar count = RealScalar.of(1000);
  @FieldInteger
  @FieldClip(min = "1", max = "20")
  public Scalar multi = RealScalar.of(10);
  @FieldInteger
  public Scalar pCount = RealScalar.of(4);
  public Boolean nearest = false;
  public CenterNorms centerNorms = CenterNorms._2;
}
