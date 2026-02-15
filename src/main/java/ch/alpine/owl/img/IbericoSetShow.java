// code by jph
package ch.alpine.owl.img;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.cal.GeoPosition;
import ch.alpine.bridge.fig.ImagePlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.ShowWindow;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.img.ColorFormat;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class IbericoSetShow {
  private static final Stroke STROKE = //
      new BasicStroke(4.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 6 }, 0);
  Tensor q_xy;
  Sedarim sedarim;
  Graphics2D graphics;
  ColorDataGradient colorDataGradient030 = ColorDataGradients.NEON.deriveWithOpacity(RealScalar.of(0.6));
  ColorDataGradient colorDataGradient100 = ColorDataGradients.NEON.deriveWithOpacity(RealScalar.of(1.0));
  ColorDataIndexed colorDataIndexed = ColorDataLists._097.strict().deriveWithAlpha(128 + 64);
  final BufferedImage bufferedImage;

  public IbericoSetShow(Tensor tab, BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    Tensor p_s2 = Tensor.of(tab.stream().map(row -> row.extract(2, 4)).map(GeoPosition::of));
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(SnManifold.INSTANCE);
    sedarim = biinvariant.coordinate(InversePowerVariogram.of(2), p_s2);
    q_xy = Tensor.of(tab.stream().map(row -> row.extract(0, 2)));
    // GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3));
    graphics = bufferedImage.createGraphics();
  }

  int index = 0;

  public void add(Tensor routes) {
    Graphics2D g = (Graphics2D) graphics.create();
    RenderQuality.setQuality(g);
    g.setColor(colorDataIndexed.getColor(index));
    g.setStroke(STROKE);
    GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3));
    Tensor polygon = Tensor.of(routes.stream() //
        .filter(route -> route.get(3).length() == 2) //
        .map(route -> route.get(3)) //
        .map(GeoPosition::of) //
        .map(sedarim::sunder) //
        .map(weights -> LinearBiinvariantMean.INSTANCE.mean(q_xy, weights)));
    Path2D path2d = geometricLayer.toPath2D(polygon);
    g.draw(path2d);
    g.dispose();
    ++index;
  }

  public void drawGrid() {
    Graphics2D g = (Graphics2D) graphics.create();
    RenderQuality.setQuality(g);
    g.setColor(new Color(0, 0, 0, 128));
    g.setStroke(new BasicStroke());
    GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3));
    int lat_min = 36;
    int lat_max = 44;
    int lon_min = -10;
    int lon_max = 4;
    for (int lon = lon_min; lon <= lon_max; ++lon) {
      final int flon = lon;
      Tensor routes = Array.of(l -> Tensors.vectorInt(lat_min + l.get(0), flon), lat_max - lat_min + 1);
      // System.out.println(routes);
      Tensor polygon = Tensor.of(routes.stream() //
          .map(GeoPosition::of) //
          .map(sedarim::sunder) //
          .map(weights -> LinearBiinvariantMean.INSTANCE.mean(q_xy, weights)));
      Path2D path2d = geometricLayer.toPath2D(polygon);
      g.draw(path2d);
    }
    for (int lat = lat_min; lat <= lat_max; ++lat) {
      final int flat = lat;
      Tensor routes = Array.of(l -> Tensors.vectorInt(flat, lon_min + l.get(0)), lon_max - lon_min + 1);
      // System.out.println(routes);
      Tensor polygon = Tensor.of(routes.stream() //
          .map(GeoPosition::of) //
          .map(sedarim::sunder) //
          .map(weights -> LinearBiinvariantMean.INSTANCE.mean(q_xy, weights)));
      Path2D path2d = geometricLayer.toPath2D(polygon);
      g.draw(path2d);
    }
    g.dispose();
  }

  public void addP(Tensor routes) {
    Graphics2D g = (Graphics2D) graphics.create();
    for (Tensor route : routes) {
      Tensor p0 = LinearBiinvariantMean.INSTANCE.mean(q_xy, sedarim.sunder(GeoPosition.of(route.get(3))));
      Tensor p1 = LinearBiinvariantMean.INSTANCE.mean(q_xy, sedarim.sunder(GeoPosition.of(route.get(4))));
      Scalar len = route.Get(1);
      Scalar asc = route.Get(2);
      Scalar scalar = UnitSystem.SI().apply(asc.divide(len)).multiply(RealScalar.of(20)); // to 10 %
      scalar = Clips.unit().apply(scalar);
      int pix0 = p0.Get(0).number().intValue();
      int piy0 = p0.Get(1).number().intValue();
      int pix1 = p1.Get(0).number().intValue();
      int piy1 = p1.Get(1).number().intValue();
      g.setStroke(new BasicStroke(10.5f));
      g.setColor(ColorFormat.toColor(colorDataGradient030.apply(scalar)));
      g.draw(new Line2D.Double(pix0, piy0, pix1, piy1));
      g.setStroke(new BasicStroke());
      double rad = Math.sqrt(QuantityMagnitude.SI().in("km").apply(len).number().doubleValue()) * 3;
      g.setColor(ColorFormat.toColor(colorDataGradient100.apply(scalar)));
      g.fill(new Ellipse2D.Double(pix1 - rad / 2, piy1 - rad / 2, rad, rad));
    }
    g.dispose();
  }

  public void show() {
    graphics.dispose();
    Show show = new Show();
    show.add(ImagePlot.of(bufferedImage));
    // JFrame j = new JFrame();
    JDialog jDialog = ShowWindow.asDialog(List.of(show));
    jDialog.setBounds(0, 0, 1400, 1000);
  }

  static void main() throws IOException {
    IbericoImports ibericoImports = new IbericoImports(false);
    IbericoSetShow ibericoSetShow = new IbericoSetShow( //
        Import.of(HomeDirectory.Pictures.resolve("iberico_match.tsv")), //
        ImageIO.read(HomeDirectory.Pictures.resolve("iberico.png").toFile()));
    // ibericoSetShow.drawGrid();
    ibericoSetShow.add(ibericoImports.winter.get(0).tensor);
    ibericoSetShow.add(ibericoImports.winter.get(2).tensor);
    ibericoSetShow.addP(ibericoImports.winter.get(0).tensor);
    ibericoSetShow.addP(ibericoImports.winter.get(2).tensor);
    ImageIO.write(ibericoSetShow.bufferedImage, "png", HomeDirectory.Ephemeral.resolve("iberico_track.png").toFile());
    ibericoSetShow.show();
  }
}
