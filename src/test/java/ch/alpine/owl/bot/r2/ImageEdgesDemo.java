// code by jph
package ch.alpine.owl.bot.r2;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;

enum ImageEdgesDemo {
  ;
  public static void main(String[] args) throws IOException {
    final Tensor tensor = ImageRegions.grayscale( //
        Import.of(HomeDirectory.Pictures("20180122_duebendorf_hangar.png"))).unmodifiable();
    // ---
    for (int ttl = 0; ttl <= 5; ++ttl) {
      Timing timing = Timing.started();
      Tensor visual = ImageEdges.extrusion(tensor, ttl);
      System.out.println(timing.seconds());
      Export.of(HomeDirectory.Pictures(String.format("hangar%02d.png", ttl)), visual);
    }
  }
}
