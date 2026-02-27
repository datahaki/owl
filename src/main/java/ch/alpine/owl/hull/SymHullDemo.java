// code by jph
package ch.alpine.owl.hull;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.dis.R3Display;
import ch.alpine.ascony.ren.LeversRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.ascony.win.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.qhull3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.sca.pow.Sqrt;

@ReflectionMarker
public class SymHullDemo implements ManipulateProvider, RenderInterface {
  public final SymParam hullParam = new SymParam();
  // ---
  private final ManifoldDisplay manifoldDisplay = R3Display.INSTANCE;
  private Tensor tensor;
  private List<int[]> faces;
  private final GeometricComponent geometricComponent = new GeometricComponent();

  public SymHullDemo() {
    geometricComponent.addRenderInterface(this);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int layers = hullParam.layers;
    int n = hullParam.n;
    tensor = Tensors.empty();
    for (Tensor _z : Subdivide.of(-0.9, 0.9, layers)) {
      Scalar z = (Scalar) _z;
      Scalar r = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(z.multiply(z)));
      CirclePoints.of(n).stream().map(xy -> xy.multiply(r).append(z)).forEach(tensor::append);
    }
    faces = ConvexHull3D.of(tensor);
    Tensor rotate = this.tensor.dot(hullParam.rotation());
    LeversRender leversRender = LeversRender.of(manifoldDisplay, rotate, null, geometricLayer, graphics);
    leversRender.renderSequence();
    SurfaceMesh surfaceMesh = new SurfaceMesh(rotate, faces);
    new SurfaceMeshRender(surfaceMesh, ColorDataGradients.AURORA).render(geometricLayer, graphics);
  }

  @Override
  public Container getContainer() {
    return geometricComponent.jComponent;
  }

  static void main() {
    new SymHullDemo().runStandalone();
  }
}
