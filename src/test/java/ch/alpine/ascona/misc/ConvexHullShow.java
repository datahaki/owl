// code by jph
package ch.alpine.ascona.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.random.RandomGenerator;

import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Clips;

// 3
/* package */ enum ConvexHullShow {
  ;
  private static Tensor image(int seed) {
    RandomGenerator randomGenerator = new Random(seed);
    Tensor points = RandomVariate.of(NormalDistribution.of(0.5, .28), randomGenerator, 30, 2).map(Clips.unit());
    Tensor hull = CirclePoints.of(17); // TODO SOPHUS demo is placed incorrectly
    // ConvexHull2D.of(points);
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
      geometricLayer.pushMatrix(Se2Matrix.translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return ImageFormat.from(bufferedImage);
  }

  static void main() throws IOException {
    Path folder = HomeDirectory.Pictures.resolve(ConvexHullShow.class.getSimpleName());
    Files.createDirectories(folder);
    for (int seed = 0; seed < 51; ++seed) {
      Tensor image = image(seed);
      Export.of(folder.resolve(String.format("%03d.png", seed)), image);
    }
    {
      Export.of(HomeDirectory.Pictures.resolve(ConvexHullShow.class.getSimpleName() + ".png"), image(3));
    }
  }
}
