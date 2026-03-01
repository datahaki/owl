// code by jph
package ch.alpine.owl.hull;

import ch.alpine.qhull3.ConvexHull3D;
import ch.alpine.qhull3.PlatonicSolid;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.hs.rpn.HemisphereRandomSample;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.sophus.lie.rn.RnGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

enum RandomMethod {
  SPHERE {
    @Override
    Tensor vertices(int n) {
      return RandomSample.of(new Sphere(2), n);
    }
  },
  HEMIS {
    @Override
    Tensor vertices(int n) {
      return RandomSample.of(HemisphereRandomSample.of(2), n);
    }
  },
  REALS3 {
    @Override
    Tensor vertices(int n) {
      return RandomSample.of(new RnGroup(3), n);
    }
  },
  CUBE {
    @Override
    Tensor vertices(int n) {
      Clip[] clips = { Clips.absoluteOne(), Clips.absoluteOne(), Clips.absoluteOne() };
      CoordinateBoundingBox cbb = CoordinateBoundingBox.of(clips);
      return cbb.mapInside().slash(RandomSample.of(new RnGroup(3), n));
    }
  },
  STIEFEL {
    @Override
    Tensor vertices(int n) {
      StiefelManifold stiefelManifold = new StiefelManifold(n, 3);
      return Transpose.of(RandomSample.of(stiefelManifold));
    }
  },
  DODECAHEDRON {
    @Override
    Tensor vertices(int n) {
      return PlatonicSolid.DODECAHEDRON.vertices();
    }
  },
  ICOSAHEDRON {
    @Override
    Tensor vertices(int n) {
      return PlatonicSolid.ICOSAHEDRON.vertices();
    }
  };

  abstract Tensor vertices(int n);

  final SurfaceMesh mesh(int n) {
    Tensor vrt = vertices(n);
    return new SurfaceMesh(vrt, ConvexHull3D.of(vrt));
  }
}
