// code by jph
package ch.alpine.owl.gui.region;

import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.math.region.ImageRegion;

public enum ImageRegionRender {
  ;
  public static RenderInterface create(ImageRegion imageRegion) {
    return ImageRender.scale(RegionRenders.image(imageRegion.image()), imageRegion.scale());
  }
}
