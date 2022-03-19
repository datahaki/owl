// code by jph
package ch.alpine.owl.bot.rn.glc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.TrajectorySample;

public class R2DemoTest {
  @Test
  public void testSimpleEmpty() {
    GlcNode glcNode = R2Demo.simpleEmpty().getBest().get();
    List<TrajectorySample> trajectorySamples = GlcTrajectories.detailedTrajectoryTo(R2Demo.STATE_INTEGRATOR, glcNode);
    assertEquals(trajectorySamples.size(), 31);
  }

  @Test
  public void testSimpleR2Bubbles() {
    GlcNode glcNode = R2Demo.simpleR2Bubbles().getBest().get();
    List<TrajectorySample> trajectorySamples = GlcTrajectories.detailedTrajectoryTo(R2Demo.STATE_INTEGRATOR, glcNode);
    assertEquals(trajectorySamples.size(), 31);
  }
}
