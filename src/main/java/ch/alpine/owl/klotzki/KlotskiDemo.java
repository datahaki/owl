// code by jph
package ch.alpine.owl.klotzki;

import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owlets.glc.adapter.DiscreteIntegrator;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.CTrajectoryPlanner;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.TableBuilder;

public class KlotskiDemo extends AbstractDemo implements RenderInterface {
  public static final ThreadLocal<Boolean> debugPrint = ThreadLocal.withInitial(() -> false);
  public static final Path FOLDER_SOLUTIONS = HomeDirectory.Ephemeral.mk_dirs("klotski");
  private final KlotskiProblem klotskiProblem;
  private static final int RES = 128;
  // ---
  private final KlotskiPlot klotskiPlot;
  Tensor _board = null; // bad design

  public KlotskiDemo(KlotskiProblem klotskiProblem) {
    this.klotskiProblem = klotskiProblem;
    klotskiPlot = new KlotskiPlot(klotskiProblem, RES);
    geometricComponent().addRenderInterface(this);
    Tensor pvm = PvmBuilder.rhs().setOffset(100, 500).setPerPixel(80).digest();
    geometricComponent().setModel2Pixel(pvm);
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
        KlotskiGoalAdapter.of(klotskiProblem.goal()));
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
        if (debugPrint.get())
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
    getWindow().close();
  }

  public static Path solutionFile(KlotskiProblem klotskiProblem) {
    return FOLDER_SOLUTIONS.resolve(klotskiProblem.name() + ".object");
  }

  static void main() throws IOException {
    KlotskiProblem klotskiProblem = Huarong.AMBUSH.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    klotskiDemo.runStandalone();
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}
