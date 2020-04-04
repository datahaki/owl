// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class TransitionNdContainer {
  private final Map<Se2TransitionNdType, RrtsNodeCollection> map = new EnumMap<>(Se2TransitionNdType.class);
  private final PointsRender pointsRender = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255));
  private final Tensor tensor;
  private final int value;

  public TransitionNdContainer(Tensor lbounds, Tensor ubounds, int n, int value) {
    VectorQ.requireLength(lbounds, 2);
    VectorQ.requireLength(ubounds, 2);
    Random random = new Random(1);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        lbounds.copy().append(Pi.VALUE.negate()), //
        ubounds.copy().append(Pi.VALUE));
    tensor = Array.of(l -> randomSampleInterface.randomSample(random), n);
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.CL_SE2_R2) {
      Se2TransitionNdType se2TransitionNdType = Se2TransitionNdType.fromString(geodesicDisplay.toString());
      RrtsNodeCollection rrtsNodeCollection = se2TransitionNdType.equals(Se2TransitionNdType.R2) //
          ? new RnRrtsNodeCollection(lbounds, ubounds)
          : Se2RrtsNodeCollections.of( //
              se2TransitionNdType.transitionSpace(), lbounds, ubounds);
      for (Tensor state : tensor)
        rrtsNodeCollection.insert(RrtsNode.createRoot(geodesicDisplay.project(state), RealScalar.ZERO));
      map.put(se2TransitionNdType, rrtsNodeCollection);
    }
    this.value = value;
  }

  public void render( //
      GeodesicDisplay geodesicDisplay, //
      GeometricLayer geometricLayer, //
      Graphics2D graphics, //
      Tensor mouse) {
    RenderQuality.setQuality(graphics);
    Se2TransitionNdType se2TransitionNdType = Se2TransitionNdType.fromString(geodesicDisplay.toString());
    RrtsNodeCollection rrtsNodeCollection = map.get(se2TransitionNdType);
    Scalar sqrt = Sqrt.FUNCTION.apply(RationalScalar.of(10, rrtsNodeCollection.size()));
    Tensor shape = geodesicDisplay.shape().multiply(sqrt);
    Tensor _tensor = Tensor.of(tensor.stream().map(geodesicDisplay::project).map(N.DOUBLE::of));
    pointsRender.show(geodesicDisplay::matrixLift, shape, _tensor).render(geometricLayer, graphics);
    // ---
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mouse));
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
