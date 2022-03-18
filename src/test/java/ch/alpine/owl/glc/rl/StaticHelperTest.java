// code by jph
package ch.alpine.owl.glc.rl;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;

public class StaticHelperTest {
  @Test
  public void testEntrywiseMinEmpty() {
    Optional<Tensor> optional = StaticHelper.entrywiseMin(Stream.of());
    assertFalse(optional.isPresent());
  }

  @Test
  public void testFailGetMinEmpty() {
    AssertFail.of(() -> StaticHelper.getMin(Collections.emptyList(), 2));
  }
}
