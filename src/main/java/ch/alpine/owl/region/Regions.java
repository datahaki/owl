// code by jph
package ch.alpine.owl.region;

import java.io.Serializable;

import ch.alpine.sophis.math.api.Region;
import ch.alpine.tensor.chq.MemberQ;

/** class design stolen from java.util.Collections */
public enum Regions {
  ;
  private static class EmptyRegion<T> implements Region<T>, Serializable {
    @Override // from Region
    public boolean test(T type) {
      return false;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final Region EMPTY_REGION = new EmptyRegion<>();

  @SuppressWarnings("unchecked")
  public static final <T> Region<T> emptyRegion() {
    return EMPTY_REGION;
  }

  public static final MemberQ EMPTY = _ -> false;

  // ---
  private static class CompleteRegion<T> implements Region<T>, Serializable {
    @Override
    public boolean test(T type) {
      return true;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final Region COMPLETE_REGION = new CompleteRegion<>();

  @SuppressWarnings("unchecked")
  public static final <T> Region<T> completeRegion() {
    return COMPLETE_REGION;
  }
}
