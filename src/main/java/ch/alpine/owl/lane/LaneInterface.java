// code by gjoel, jph
package ch.alpine.owl.lane;

import ch.alpine.tensor.Tensor;

public interface LaneInterface {
  /** @return points used to generate/describe lane, may return null */
  // TODO OWL API longterm design: lane should not have to provide control points
  Tensor controlPoints();

  /** @return points of center line of lane */
  Tensor midLane();

  /** @return points of left lane boundary */
  Tensor leftBoundary();

  /** @return points of right lane boundary */
  Tensor rightBoundary();

  /** @return distances to boundary boundary from {@link LaneInterface#midLane()} */
  Tensor margins();
  // maybe also add curvature at some point
}
