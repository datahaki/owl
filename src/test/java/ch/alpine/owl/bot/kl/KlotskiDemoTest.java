// code by jph
package ch.alpine.owl.bot.kl;

import java.io.IOException;
import java.util.List;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class KlotskiDemoTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    KlotskiProblem klotskiProblem = Serialization.copy(Huarong.ONLY_18_STEPS.create());
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    List<StateTime> list = klotskiDemo.compute().list;
    assertEquals(list.size(), 28);
    klotskiDemo.close();
  }

  public void testPennant() throws ClassNotFoundException, IOException {
    KlotskiProblem klotskiProblem = Serialization.copy(Pennant.PUZZLE.create());
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    List<StateTime> list = klotskiDemo.compute().list;
    assertEquals(list.size(), 84);
    klotskiDemo.close();
  }
  // public void testTrafficJam() {
  // KlotskiProblem klotskiProblem = TrafficJam.INSTANCE.create();
  // KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
  // List<StateTime> list = klotskiDemo.compute();
  // assertEquals(list.size(), 84);
  // klotskiDemo.close();
  // }
}
