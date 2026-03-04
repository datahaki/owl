// code by jph
package ch.alpine.owl.hull;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import ch.alpine.ascony.ren.GridRender;
import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.qhull3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.sophus.hs.st.TStMemberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

@ReflectionMarker
class StHullDemo implements ManipulateProvider, RenderInterface {
  @FieldSelectionArray({ "20", "30", "40", "100" })
  public Integer n = 25;
  @FieldClip(min = "-10", max = "10")
  @FieldSlider(showValue = true, showRange = true)
  public Scalar split = RealScalar.ZERO;
  public ColorDataGradients cdg = ColorDataGradients.SOLAR;
  // ---
  private final GeometricComponent geometricComponent = new GeometricComponent();

  public StHullDemo() {
    geometricComponent.addRenderInterface(this);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    new GridRender(geometricComponent.jComponent::getSize).render(geometricLayer, graphics);
    RandomGenerator randomGenerator = new Random(3);
    StiefelManifold stiefelManifold = new StiefelManifold(n, 3);
    Tensor p = RandomSample.of(stiefelManifold, randomGenerator);
    Tensor v = new TStMemberQ(p).projection( //
        RandomVariate.of(NormalDistribution.of(0.0, 0.1), randomGenerator, Dimensions.of(p)));
    Tensor pointst = stiefelManifold.tangentSpace(p).exp(v.multiply(split));
    Tensor points = Transpose.of(pointst);
    List<int[]> faces = ConvexHull3D.of(points);
    SurfaceMesh surfaceMesh = new SurfaceMesh(points, faces);
    new SurfaceMeshRender(surfaceMesh, cdg).render(geometricLayer, graphics);
  }

  @Override
  public Container getContainer() {
    return geometricComponent.jComponent;
  }

  static void main() {
    new StHullDemo().runStandalone();
  }
}
