// code by jph
package ch.alpine.owl.hull;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

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
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

@ReflectionMarker
class R3HullDemo implements ManipulateProvider, RenderInterface {
  private final ManifoldDisplay manifoldDisplay = R3Display.INSTANCE;
  private final GeometricComponent geometricComponent = new GeometricComponent();
  public final HullParam hullParam = new HullParam();
  private List<int[]> faces;
  private Tensor tensor;

  public R3HullDemo() {
    geometricComponent.addRenderInterface(this);
  }

  @Override
  public Container getContainer() {
    if (hullParam.shuffle) {
      hullParam.shuffle = false;
      int n = hullParam.count.number().intValue();
      if (hullParam.cuboid) {
        Clip[] clips = { Clips.absoluteOne(), Clips.absoluteOne(), Clips.absoluteOne() };
        CoordinateBoundingBox ccb = CoordinateBoundingBox.of(clips);
        RandomGenerator randomGenerator = ThreadLocalRandom.current();
        tensor = RandomSample.of(new BoxRandomSample(ccb), n);
        for (int index = 0; index < tensor.length(); ++index) {
          int i = randomGenerator.nextInt(3);
          if (randomGenerator.nextBoolean())
            tensor.set(clips[i].min(), index, i);
          else
            tensor.set(clips[i].max(), index, i);
        }
      } else {
        tensor = RandomSample.of(manifoldDisplay.randomSampleInterface(), n);
      }
    }
    faces = ConvexHull3D.of(tensor);
    return geometricComponent.jComponent;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor rotate = this.tensor.dot(hullParam.rotation());
    LeversRender leversRender = LeversRender.of(manifoldDisplay, rotate, null, geometricLayer, graphics);
    leversRender.renderSequence();
    SurfaceMesh surfaceMesh = new SurfaceMesh(rotate, faces);
    new SurfaceMeshRender(surfaceMesh, hullParam.cdg).render(geometricLayer, graphics);
  }

  static void main() {
    new R3HullDemo().runStandalone();
  }
}
