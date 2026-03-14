// code by jph
package ch.alpine.owl.bot.ip;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.ren.GridRender;
import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.FreeBoundedIntervalRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** inverted pendulum */
class IpDemo extends AbstractDemo {
  IpDemo() {
    GeometricComponent geometricComponent = geometricComponent();
    geometricComponent.addRenderInterfaceBackground(new GridRender(geometricComponent::getSize));
//    RenderElements.create(trajectoryPlanner).forEach(geometricComponent::addRenderInterface);
    Tensor digest = PvmBuilder.rhs().setOffset(50, 680) //
        .setPerPixel(Quantity.of(100, "m^-1"), Quantity.of(100, "m^-1*s")).digest();
    geometricComponent.setModel2Pixel(digest);
  }

  static void main() {
    new IpDemo().runStandalone();
  }
}
