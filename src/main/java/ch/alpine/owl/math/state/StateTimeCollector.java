// code by jph
package ch.alpine.owl.math.state;

import java.util.Collection;

@FunctionalInterface
public interface StateTimeCollector {
  /** @return collection of accumulated {@link StateTime}s */
  Collection<StateTime> getMembers();
}
