// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.Region;

public class InvertedRegion<T> implements Region<T>, Serializable {
  private final Region<T> region;

  public InvertedRegion(Region<T> region) {
    this.region = Objects.requireNonNull(region);
  }

  @Override // from Region
  public final boolean test(T tensor) {
    return !region.test(tensor);
  }
}
