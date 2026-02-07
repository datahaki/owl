package ch.alpine.owl.img;

import java.io.IOException;

import javax.imageio.ImageIO;

import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Import;

public enum FranceSetShow {
  ;
  static void main() throws IOException {
    IbericoImports ibericoImports = new IbericoImports(false);
    IbericoSetShow ibericoSetShow = new IbericoSetShow( //
        Import.of(HomeDirectory.Pictures.resolve("france_tab.csv")), //
        ImageIO.read(HomeDirectory.Pictures.resolve("france.png").toFile()));
    ibericoSetShow.add(ibericoImports.winter.get(1).tensor);
    ibericoSetShow.addP(ibericoImports.winter.get(1).tensor);
    ImageIO.write(ibericoSetShow.bufferedImage, "png", HomeDirectory.path("france_track.png").toFile());
    ibericoSetShow.show();
  }
}
