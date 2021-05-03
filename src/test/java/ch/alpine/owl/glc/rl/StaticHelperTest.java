// code by jph
package ch.alpine.owl.glc.rl;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testEntrywiseMinEmpty() {
    Optional<Tensor> optional = StaticHelper.entrywiseMin(Stream.of());
    assertFalse(optional.isPresent());
  }

  public void testFailGetMinEmpty() {
    AssertFail.of(() -> StaticHelper.getMin(Collections.emptyList(), 2));
  }
}
