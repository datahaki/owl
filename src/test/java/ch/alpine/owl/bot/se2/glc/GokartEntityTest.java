// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.MatrixQ;

class GokartEntityTest {
  @Test
  void testFootprint() {
    assertTrue(MatrixQ.of(GokartEntity.SHAPE));
  }
}
