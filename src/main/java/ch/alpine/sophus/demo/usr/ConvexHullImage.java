// code by jph
package ch.alpine.sophus.demo.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.lie.r2.ConvexHull;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Clips;

// 3
/* package */ enum ConvexHullImage {
  ;
  private static Tensor image(int seed) {
    Random random = new Random(seed);
    Tensor points = RandomVariate.of(NormalDistribution.of(0.5, .28), random, 30, 2).map(Clips.unit());
    Tensor hull = ConvexHull.of(points);
    GeometricLayer geometricLayer = new GeometricLayer(StaticHelper.SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    RenderQuality.setQuality(graphics);
    {
      graphics.setColor(Color.BLUE);
      Path2D path2d = geometricLayer.toPath2D(hull);
      path2d.closePath();
      graphics.draw(path2d);
    }
    graphics.setColor(Color.RED);
    for (Tensor point : points) {
      geometricLayer.pushMatrix(GfxMatrix.translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures(ConvexHullImage.class.getSimpleName());
    folder.mkdir();
    for (int seed = 0; seed < 51; ++seed) {
      Tensor image = image(seed);
      Export.of(new File(folder, String.format("%03d.png", seed)), image);
    }
    {
      Export.of(HomeDirectory.Pictures(ConvexHullImage.class.getSimpleName() + ".png"), image(3));
    }
  }
}
