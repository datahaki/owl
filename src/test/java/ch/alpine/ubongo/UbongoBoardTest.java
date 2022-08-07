// code by jph
package ch.alpine.ubongo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UbongoBoardTest {
  @Test
  void testSimple() {
    assertEquals(UbongoBoards.STANDARD.solve().size(), 13);
    assertEquals(UbongoBoards.SHOTGUN1.solve().size(), 7);
  }
}
