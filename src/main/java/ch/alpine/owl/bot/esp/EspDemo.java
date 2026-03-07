// code by jph
package ch.alpine.owl.bot.esp;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owlets.glc.adapter.DiscreteIntegrator;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Export;

class EspDemo extends AbstractDemo implements RenderInterface {
  static final Tensor START = Tensors.of( //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 0, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(2, 2) //
  ).unmodifiable();

  static class Param {
    public Boolean compute = true;
  }

  private final Param param;

  public EspDemo() {
    super(param = new Param());
    geometricComponent().addRenderInterface(this);
    timerFrame.geometricComponent.setOffset(100, 600);
    timerFrame.geometricComponent.setPerPixel(RealScalar.of(100));
    fieldsEditor(0).addUniversalListener(() -> new Thread(this::compute).start());
  }

  Tensor _board = null;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = _board;
    if (Objects.nonNull(board)) {
      new EspRender(board).render(geometricLayer, graphics);
    }
  }

  void compute() {
    PlannerConstraint plannerConstraint = EmptyPlannerConstraint.INSTANCE;
    TrajectoryPlanner trajectoryPlanner = StandardTrajectoryPlanner.create( //
        EspStateTimeRaster.INSTANCE, //
        new DiscreteIntegrator(EspModel.INSTANCE), //
        EspFlows.INSTANCE, //
        plannerConstraint, //
        EspGoal.INSTANCE);
    trajectoryPlanner.insertRoot(new StateTime(START, RealScalar.ZERO));
    while (timerFrame.jFrame.isVisible()) {
      {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.orElseThrow();
          System.out.println("$=" + glcNode.costFromRoot());
          List<StateTime> list = GlcNodes.getPathFromRootTo(glcNode);
          try {
            Export.object(EspExport.SOLUTION_PATH, list);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }
      }
      Optional<GlcNode> optional = trajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = trajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = trajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.orElseThrow();
        _board = nextNode.state();
        trajectoryPlanner.expand(nextNode);
        // System.out.println(String.format("#=%3d q=%3d $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
  }

  static void main() {
    new EspDemo().runStandalone();
  }
}
