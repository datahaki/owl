// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sqrt;

public class TransitionNdContainer {
  private final Map<TransitionNdTypes, RrtsNodeCollection> map = new EnumMap<>(TransitionNdTypes.class);
  private final PointsRender pointsRender = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255));
  private final Tensor tensor;
  private final int value;

  public TransitionNdContainer(NdBox ndBox, int n, int value) {
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(ndBox.min(), Pi.VALUE.negate()), //
        Append.of(ndBox.max(), Pi.VALUE));
    tensor = RandomSample.of(randomSampleInterface, n);
    for (ManifoldDisplay manifoldDisplay : ManifoldDisplays.CL_SE2_R2) {
      TransitionNdTypes se2TransitionNdType = TransitionNdTypes.fromString(manifoldDisplay);
      RrtsNodeCollection rrtsNodeCollection = se2TransitionNdType.equals(TransitionNdTypes.RN) //
          ? new RnRrtsNodeCollection(ndBox)
          : Se2RrtsNodeCollections.of( //
              se2TransitionNdType.transitionSpace(), ndBox);
      for (Tensor state : tensor)
        rrtsNodeCollection.insert(RrtsNode.createRoot(manifoldDisplay.project(state), RealScalar.ZERO));
      map.put(se2TransitionNdType, rrtsNodeCollection);
    }
    this.value = value;
  }

  public void render( //
      ManifoldDisplay manifoldDisplay, //
      GeometricLayer geometricLayer, //
      Graphics2D graphics, //
      Tensor mouse) {
    RenderQuality.setQuality(graphics);
    TransitionNdTypes se2TransitionNdType = TransitionNdTypes.fromString(manifoldDisplay);
    RrtsNodeCollection rrtsNodeCollection = map.get(se2TransitionNdType);
    Scalar sqrt = Sqrt.FUNCTION.apply(RationalScalar.of(10, rrtsNodeCollection.size()));
    Tensor shape = manifoldDisplay.shape().multiply(sqrt);
    Tensor _tensor = Tensor.of(tensor.stream().map(manifoldDisplay::project).map(N.DOUBLE::of));
    pointsRender.show(manifoldDisplay::matrixLift, shape, _tensor).render(geometricLayer, graphics);
    // ---
    {
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(mouse));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(new Color(128, 128, 255, 128));
      graphics.fill(path2d);
      graphics.setColor(new Color(128, 128, 255, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    TransitionSpace transitionSpace = se2TransitionNdType.transitionSpace();
    graphics.setStroke(new BasicStroke(1.5f));
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(5));
    {
      Timing timing = Timing.started();
      Collection<RrtsNode> collection = rrtsNodeCollection.nearTo(mouse, value);
      timing.stop();
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString("" + Round._4.apply(Quantity.of(timing.seconds(), "s")), 0, 20);
      graphics.setColor(new Color(255, 0, 0, 128));
      for (RrtsNode rrtsNode : collection) {
        Transition transition = transitionSpace.connect(rrtsNode.state(), mouse);
        graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
      }
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Transition transition = transitionSpace.connect(mouse, rrtsNode.state());
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
  }
}
