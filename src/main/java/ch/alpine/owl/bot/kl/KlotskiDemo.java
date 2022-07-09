// code by jph
package ch.alpine.owl.bot.kl;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.glc.adapter.DiscreteIntegrator;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.CTrajectoryPlanner;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.TableBuilder;

/* package */ class KlotskiDemo extends AbstractDemo {
  public static final File FOLDER_SOLUTIONS = HomeDirectory.Documents("klotski");
  private final KlotskiProblem klotskiProblem;
  private static final int RES = 128;
  // ---
  private final KlotskiPlot klotskiPlot;
  Tensor _board = null; // bad design

  public KlotskiDemo(KlotskiProblem klotskiProblem) {
    this.klotskiProblem = klotskiProblem;
    klotskiPlot = new KlotskiPlot(klotskiProblem, RES);
    timerFrame.geometricComponent.setOffset(100, 500);
  }

  KlotskiSolution compute() {
    PlannerConstraint plannerConstraint = //
        RegionConstraints.timeInvariant(KlotskiObstacleRegion.fromSize(klotskiProblem.size()));
    TableBuilder tableBuilder = new TableBuilder();
    CTrajectoryPlanner standardTrajectoryPlanner = StandardTrajectoryPlanner.create( //
        klotskiProblem.stateTimeRaster(), //
        new DiscreteIntegrator(KlotskiModel.INSTANCE), //
        new KlotskiFlows(klotskiProblem), //
        plannerConstraint, //
        new KlotskiGoalAdapter(klotskiProblem.getGoal()));
    standardTrajectoryPlanner.insertRoot(new StateTime(klotskiProblem.startState(), RealScalar.ZERO));
    int expandCount = 0;
    while (true) {
      {
        Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.orElseThrow();
          System.out.println("BEST IN GOAL $=" + glcNode.costFromRoot());
          return new KlotskiSolution( //
              klotskiProblem, //
              GlcNodes.getPathFromRootTo(glcNode), //
              tableBuilder.getTable());
        }
      }
      Optional<GlcNode> optional = standardTrajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = standardTrajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = standardTrajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        {
          _board = nextNode.state();
          tableBuilder.appendRow(Tensors.vector( //
              expandCount, domainMap.size(), queue.size(), //
              Scalars.intValueExact(nextNode.costFromRoot())));
        }
        System.out.println(String.format("#=%5d q=%3d $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
        standardTrajectoryPlanner.expand(nextNode);
        ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        return null;
      }
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = _board;
    if (Objects.nonNull(board))
      klotskiPlot.new Plot(board).render(geometricLayer, graphics);
  }

  public void close() {
    timerFrame.close();
  }

  public static File solutionFile(KlotskiProblem klotskiProblem) {
    FOLDER_SOLUTIONS.mkdir();
    return new File(FOLDER_SOLUTIONS, klotskiProblem.name() + ".object");
  }

  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Huarong.AMBUSH.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    klotskiDemo.setVisible(700, 700);
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}
