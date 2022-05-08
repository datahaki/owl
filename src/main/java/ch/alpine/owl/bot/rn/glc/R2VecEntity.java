// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.rn.RnMinTimeGoalManager;
import ch.alpine.owl.glc.adapter.LexicographicRelabelDecision;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.order.DiscretizedLexicographic;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.util.ren.EdgeRender;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;

/* package */ class R2VecEntity extends R2Entity {
  public R2VecEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    System.out.println("goal=" + goal);
    Collection<Tensor> controls = createControls(); // TODO OWL API design no good
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface minTimeGoal = RnMinTimeGoalManager.create(goalRegion, controls); //
    List<CostFunction> costs = new ArrayList<>();
    getPrimaryCost().map(costs::add);
    costs.add(minTimeGoal);
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, goalRegion);
    Tensor slack = Array.zeros(costs.size()); // slack equal to zero for now
    Comparator<Tensor> comparator = DiscretizedLexicographic.of(slack);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, //
        plannerConstraint, goalInterface, new LexicographicRelabelDecision(comparator));
  }

  public Optional<CostFunction> getPrimaryCost() {
    return Optional.empty();
  }

  private final EdgeRender edgeRender = new EdgeRender();

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    edgeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    super.render(geometricLayer, graphics);
  }
}
