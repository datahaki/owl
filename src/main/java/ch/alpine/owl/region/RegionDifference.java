// code by jph
package ch.alpine.owl.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophis.math.api.Region;

public class RegionDifference<T> implements Region<T>, Serializable {
  /** @param belongs
   * @param butNot
   * @return belongs \ butNot */
  public static <T> Region<T> of(Region<T> belongs, Region<T> butNot) {
    return new RegionDifference<>( //
        Objects.requireNonNull(belongs), //
        Objects.requireNonNull(butNot));
  }

  // ---
  private final Region<T> belongs;
  private final Region<T> butNot;

  private RegionDifference(Region<T> belongs, Region<T> butNot) {
    this.belongs = belongs;
    this.butNot = butNot;
  }

  @Override
  public final boolean test(T element) {
    return belongs.test(element) //
        && !butNot.test(element);
  }
}
