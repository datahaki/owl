// code by jph
package ch.alpine.owl.hull;

import java.awt.Container;
import java.awt.Graphics2D;

import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Cache;

@ReflectionMarker
class ConvexHull3DDemo implements ManipulateProvider, RenderInterface {
  public final HullParam hullParam = new HullParam();
  private final GeometricComponent geometricComponent = new GeometricComponent();
  private final Cache<MeshParam, SurfaceMesh> cache = Cache.of(MeshParam::mesh, 1);

  public ConvexHull3DDemo() {
    geometricComponent.addRenderInterface(this);
    // geometricComponent.setPerPixel(RealScalar.of(80));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    SurfaceMesh surfaceMesh = cache.apply(hullParam.meshParam.copy());
    Tensor rotate = surfaceMesh.vrt.dot(hullParam.rotParam.rotation());
    new SurfaceMeshRender(new SurfaceMesh(rotate, surfaceMesh.faces()), hullParam.cdg) //
        .render(geometricLayer, graphics);
  }

  @Override
  public Container getContainer() {
    return geometricComponent;
  }

  static void main() {
    new ConvexHull3DDemo().runStandalone();
  }
}
