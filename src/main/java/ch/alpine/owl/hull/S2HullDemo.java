// code by jph
package ch.alpine.owl.hull;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.dis.S2Display;
import ch.alpine.ascony.ren.LeversRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.ascony.win.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.qhull3.ConvexHull3D;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSample;

@ReflectionMarker
class S2HullDemo implements ManipulateProvider, RenderInterface {
  private final ManifoldDisplay manifoldDisplay = S2Display.INSTANCE;
  private final GeometricComponent geometricComponent = new GeometricComponent();
  private Tensor tensor;
  public final HullParam hullParam = new HullParam();
  private List<int[]> faces;

  public S2HullDemo() {
    geometricComponent.addRenderInterface(this);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor rotate = this.tensor.dot(hullParam.rotation());
    LeversRender leversRender = LeversRender.of(manifoldDisplay, rotate, null, geometricLayer, graphics);
    leversRender.renderSequence();
    SurfaceMesh surfaceMesh = new SurfaceMesh(rotate, faces);
    new SurfaceMeshRender(surfaceMesh, hullParam.cdg).render(geometricLayer, graphics);
  }

  @Override
  public Container getContainer() {
    if (hullParam.shuffle) {
      hullParam.shuffle = false;
      int n = hullParam.count.number().intValue();
      tensor = RandomSample.of(manifoldDisplay.randomSampleInterface(), n);
      faces = ConvexHull3D.of(tensor);
    }
    return geometricComponent.jComponent;
  }

  static void main() {
    new S2HullDemo().runStandalone();
  }
}
