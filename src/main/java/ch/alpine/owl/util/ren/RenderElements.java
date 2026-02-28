// code by jph
package ch.alpine.owl.util.ren;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.ren.AxesRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.ascony.win.GeometricComponent;
import ch.alpine.owlets.data.tree.Nodes;
import ch.alpine.owlets.data.tree.StateCostNode;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcTrajectories;
import ch.alpine.owlets.glc.core.CTrajectoryPlanner;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTimeCollector;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.tensor.ext.Serialization;

public enum RenderElements {
  ;
  public static void setRrts(GeometricComponent geometricComponent, TransitionSpace transitionSpace, RrtsNode root,
      TransitionRegionQuery transitionRegionQuery) {
    try {
      Collection<RrtsNode> nodes = Nodes.ofSubtree(root);
      Collection<RrtsNode> collection = Serialization.copy(nodes);
      geometricComponent.setRenderInterfaces( //
          RenderElements.create(collection, Serialization.copy(transitionRegionQuery)));
      geometricComponent.addRenderInterface(new TransitionRender(transitionSpace).setCollection(collection));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static Collection<RenderInterface> create(TrajectoryPlanner trajectoryPlanner) {
    List<RenderInterface> list = new LinkedList<>();
    list.add(AxesRender.INSTANCE);
    // ---
    if (trajectoryPlanner instanceof CTrajectoryPlanner cTrajectoryPlanner) {
      StateTimeRaster stateTimeRaster = cTrajectoryPlanner.stateTimeRaster();
      if (stateTimeRaster instanceof EtaRaster etaRaster) {
        list.add(new EtaRender(etaRaster.eta()));
        list.add(DomainRender.of(trajectoryPlanner.getDomainMap().keySet(), etaRaster.eta()));
      }
    }
    list.add(new QueueRender(trajectoryPlanner.getQueue()));
    if (trajectoryPlanner instanceof CTrajectoryPlanner)
      list.add(new TreeRender().setCollection(trajectoryPlanner.getDomainMap().values()));
    // if (trajectoryPlanner instanceof RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    // // EdgeRender edgeRender = new EdgeRender();
    // StateTimeRaster stateTimeRaster = relaxedTrajectoryPlanner.stateTimeRaster();
    // if (stateTimeRaster instanceof EtaRaster etaRaster) {
    // list.add(DomainQueueMapRender.of(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap(), etaRaster.eta()));
    // }
    // list.add(EdgeRenders.of(relaxedTrajectoryPlanner));
    // // edgeRender.setCollection(relaxedTrajectoryPlanner.getBestOrElsePeek());
    // // list.add(new TreeRender().setCollection(trajectoryPlanner.getDomainMap().values()));
    // }
    // {
    // TrajectoryRegionQuery trq = trajectoryPlanner.getHeuristicFunction();
    // if (trq instanceof StateTimeCollector)
    // list.add(new GoalRender(((StateTimeCollector) trq).getMembers()));
    // if (trq instanceof StateTimeCollector)
    // list.add(new GoalRender(((StateTimeCollector) trq).getMembers()));
    // }
    {
      Optional<GlcNode> goalNode = trajectoryPlanner.getBest();
      if (goalNode.isPresent()) {
        List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo( //
            trajectoryPlanner.getStateIntegrator(), goalNode.get());
        TrajectoryRender trajectoryRender = new TrajectoryRender();
        trajectoryRender.trajectory(trajectory);
        list.add(trajectoryRender);
      }
    }
    return list;
  }

  public static RenderInterface create(StateTimeRaster stateTimeRaster) {
    if (stateTimeRaster instanceof EtaRaster etaRaster)
      return new EtaRender(etaRaster.eta());
    return null;
  }

  // public static RenderInterface create(PlannerConstraint plannerConstraint) {
  // if (plannerConstraint instanceof TrajectoryObstacleConstraint) {
  // TrajectoryRegionQuery trajectoryRegionQuery = //
  // ((TrajectoryObstacleConstraint) plannerConstraint).getTrajectoryRegionQuery();
  // if (trajectoryRegionQuery instanceof StateTimeCollector)
  // return new ObstacleRender(((StateTimeCollector) trajectoryRegionQuery).getMembers());
  // }
  // return null;
  // }
  public static Collection<RenderInterface> create( //
      Collection<? extends StateCostNode> collection, TransitionRegionQuery transitionRegionQuery) {
    List<RenderInterface> list = new LinkedList<>();
    list.add(AxesRender.INSTANCE);
    if (transitionRegionQuery instanceof StateTimeCollector stateTimeCollector)
      list.add(new ObstacleRender(stateTimeCollector.getMembers()));
    list.add(new TreeRender().setCollection(collection));
    return list;
  }
}
