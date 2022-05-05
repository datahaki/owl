// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.owl.ani.api.ProviderRank;
import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.gui.ren.EdgeRender;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Degree;

/** test if api is sufficient to model gokart */
public class GokartEntity extends CarEntity {
  static final Tensor PARTITION_SCALE = Tensors.of(RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(2.5);
  static final Scalar LOOKAHEAD = RealScalar.of(3.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(15);
  static final Scalar MAX_TURNING_RATE = Degree.of(23);
  static final FlowsInterface CARFLOWS = Se2CarFlows.forward(SPEED, MAX_TURNING_PLAN);
  public static final Tensor SHAPE = ResourceData.of("/gokart/footprint/20171201.csv");
  // ---
  private final EdgeRender edgeRender = new EdgeRender();
  /** simulation of occasional feedback from localization algorithm */
  private final EntityControl localizationFeedback = new EntityControl() {
    private final Random random = new Random();
    private final Distribution distribution = NormalDistribution.standard();
    private boolean trigger = false;

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.GODMODE;
    }

    @Override
    public Optional<Tensor> control(StateTime tail, Scalar now) {
      Optional<Tensor> optional = Optional.empty();
      if (trigger)
        optional = Optional.of(RandomVariate.of(distribution, 3));
      trigger = 0 == random.nextInt(20);
      return optional;
    }
  };

  public GokartEntity(StateTime stateTime) {
    super(stateTime, createPurePursuitControl(), PARTITION_SCALE, CARFLOWS, SHAPE);
    // ---
    add(localizationFeedback);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
  }

  @Override // from PlannerCallback
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // System.out.println(trajectoryPlanner.getDomainMap().values().size());
    edgeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }

  public EdgeRender getEdgeRender() {
    return this.edgeRender;
  }
}
