// code by jph
package ch.alpine.ubongo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UbongoTest {
  @Test
  void testSimple() {
    assertEquals(Ubongo.values().length, 12);
    assertEquals(Ubongo.C2.count(), 5);
  }

  @Test
  void testStamps() {
    for (Ubongo ubongo : Ubongo.values()) {
      // System.out.println();
      System.out.println(ubongo + " " + ubongo.stamps().size());
    }
  }
}
