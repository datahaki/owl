// code by jl, jph
package ch.alpine.owl.glc.core;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.data.tree.NodesAssert;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;

public enum HeuristicAssert {
  ;
  /** Checks if the Cost and the Heuristic along the found trajectory are consistent
   * 
   * @param trajectoryPlanner */
  public static void check(TrajectoryPlanner trajectoryPlanner) {
    check(getFinalGoalNode(trajectoryPlanner));
  }

  public static void check(Optional<GlcNode> finalNode) {
    if (!finalNode.isPresent()) {
      System.out.println("No Final GoalNode, therefore no ConsistencyCheck");
      return;
    }
    check(finalNode.get());
  }

  public static void check(GlcNode finalNode) {
    List<GlcNode> trajectory = Nodes.listFromRoot(finalNode);
    // omit last Node, since last node may lie outside of goal region, as Trajectory to it was in
    NodesAssert.connectivityCheck(trajectory);
    for (int i = 1; i < trajectory.size() - 1; ++i) {
      GlcNode current = trajectory.get(i);
      GlcNode parent = current.parent();
      if (Scalars.lessEquals(current.costFromRoot(), parent.costFromRoot())) {
        System.err.println("At time " + current.stateTime().time() + " cost from root decreased from " + //
            parent.costFromRoot() + " to " + current.costFromRoot());
        StateTimeTrajectories.print(GlcNodes.getPathFromRootTo(finalNode));
        throw new RuntimeException();
      }
      // jan changed the condition to strictly less < because equal merit is permitted
      if (Scalars.lessThan(current.merit(), parent.merit())) {
        System.err.println(String.format("At time %s merit decreased\n %s\n %s", //
            current.stateTime().time(), parent.merit(), current.merit()));
        StateTimeTrajectories.print(GlcNodes.getPathFromRootTo(finalNode));
        throw TensorRuntimeException.of(current.merit(), parent.merit());
      }
      // monotonously increasing merit means, that delta(Cost) >= delta(CostToGo)
      // as: Cost(Goal)== Merit(Goal) >= (Cost(Node) + CostToGo(Node)) = Merit (Node)
    }
  }

  private static Optional<GlcNode> getFinalGoalNode(TrajectoryPlanner trajectoryPlanner) {
    return HeuristicQ.of(trajectoryPlanner.getHeuristicFunction()) //
        ? trajectoryPlanner.getBestOrElsePeek() //
        : trajectoryPlanner.getBest();
  }
}
