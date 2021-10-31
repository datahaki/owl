// code by ynager
package ch.alpine.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.ResourceData;

/* package */ enum WaypointDistanceCostDemo {
  ;
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE);

  public static void show(Tensor waypoints) {
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        CURVE_SUBDIVISION.cyclic(waypoints), true, //
        RealScalar.ONE, RealScalar.of(7.5), new Dimension(640, 640));
    Tensor image = Raster.of(imageCostFunction.image(), ColorDataGradients.CLASSIC);
    BufferedImage bufferedImage = ImageFormat.of(image);
    JFrame frame = new JFrame() {
      @Override
      public void paint(Graphics graphics) {
        graphics.drawImage(bufferedImage, 0, 0, null);
      }
    };
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(700, 700);
    frame.setLocation(100, 100);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    show(ResourceData.of("/dubilab/waypoints/20180610.csv"));
    show(ResourceData.of("/dubilab/waypoints/20181126.csv"));
  }
}
