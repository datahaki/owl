// code by jph
package ch.alpine.owl.glc.rl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;

class StaticHelperTest {
  @Test
  void testEntrywiseMinEmpty() {
    Optional<Tensor> optional = StaticHelper.entrywiseMin(Stream.of());
    assertFalse(optional.isPresent());
  }

  @Test
  void testFailGetMinEmpty() {
    assertThrows(Exception.class, () -> StaticHelper.getMin(Collections.emptyList(), 2));
  }
}
