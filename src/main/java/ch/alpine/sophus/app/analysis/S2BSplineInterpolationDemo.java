// code by jph
package ch.alpine.sophus.app.analysis;

import java.io.File;
import java.io.IOException;

import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.crv.spline.GeodesicBSplineInterpolation;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Round;

/* package */ enum S2BSplineInterpolationDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor target = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}}");
    File folder = HomeDirectory.Documents("s2");
    folder.mkdir();
    Export.of(new File(folder, "target.csv"), target.map(Round._6));
    Geodesic geodesic = S2Display.INSTANCE.geodesic();
    AbstractBSplineInterpolation abstractBSplineInterpolation = //
        new GeodesicBSplineInterpolation(geodesic, 2, target);
    Iteration iteration = abstractBSplineInterpolation.untilClose(Chop._08, 100);
    Tensor control = iteration.control();
    Tolerance.CHOP.requireClose(control.get(0), target.get(0));
    Tolerance.CHOP.requireClose(control.get(3), target.get(3));
    MatrixQ.require(control);
    Export.of(new File(folder, "control.csv"), control.map(Round._6));
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(geodesic, 2, control);
    Tensor curve = Subdivide.of(0, control.length() - 1, 200).map(geodesicBSplineFunction);
    Export.of(new File(folder, "curve.csv"), curve.map(Round._6));
  }
}
