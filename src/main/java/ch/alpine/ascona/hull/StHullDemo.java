// code by jph
package ch.alpine.ascona.hull;

import java.awt.Graphics2D;
import java.util.List;

import ch.alpine.ascony.ren.AxesRender;
import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.qhull3d.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.sophus.hs.st.TStMemberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

public class StHullDemo extends AbstractDemo {
  @ReflectionMarker
  public static class ExpGen {
    @FieldClip(min = "0", max = "10")
    @FieldSlider(showValue = true, showRange = true)
    public Scalar split = RealScalar.ZERO;
  }

  public final ExpGen expGen;
  private StiefelManifold stiefelManifold = new StiefelManifold(25, 3);
  private Tensor p;
  private Tensor v;

  public StHullDemo(ExpGen expGen) {
    super(expGen);
    this.expGen = expGen;
    p = RandomSample.of(stiefelManifold);
    v = new TStMemberQ(p).projection(RandomVariate.of(NormalDistribution.of(0.0, 0.1), Dimensions.of(p)));
  }

  public StHullDemo() {
    this(new ExpGen());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor pointst = stiefelManifold.exponential(p).exp(v.multiply(expGen.split));
    Tensor points = Transpose.of(pointst);
    List<int[]> faces = ConvexHull3D.of(points);
    SurfaceMesh surfaceMesh = new SurfaceMesh(points, faces);
    new SurfaceMeshRender(surfaceMesh).render(geometricLayer, graphics);
  }

  static void main() {
    new StHullDemo().runStandalone();
  }
}
