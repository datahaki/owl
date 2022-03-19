// code by jph
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Objects;

public enum AssertFail {
  ;
  public static void of(Runnable runnable) {
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
