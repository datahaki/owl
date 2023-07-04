// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.image.BufferedImage;

import ch.alpine.owl.math.region.BufferedImageRegion;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clips;

public enum ImageRegions {
  ;
  /** @param path of image resource in repo
   * @param range vector of length 2
   * @param strict
   * @return */
  public static Region<Tensor> loadFromRepository(String path, Tensor range, boolean strict) {
    return from(ResourceData.bufferedImage(path), range, strict);
  }

  public static Region<Tensor> from(BufferedImage bufferedImage, Tensor range, boolean strict) {
    if (bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY)
      bufferedImage = Binarize.of(bufferedImage);
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1)));
    return new BufferedImageRegion(bufferedImage, coordinateBoundingBox, strict);
  }

  /** grayscale images that encode free space (as black pixels) and the location of obstacles
   * (as non-black pixels) may be stored as grayscale, or indexed color images. Images with
   * colors palette may be of smaller size than the equivalent grayscale image. Indexed color
   * images have RGBA channels. The function converts the given image to a grayscale image if
   * necessary.
   * 
   * @param image
   * @return matrix with entries from the range {0, 1, ..., 255}
   * @throws Exception if input does not represent an image */
  public static Tensor grayscale(Tensor image) {
    return switch (TensorRank.ofArray(image)) {
    case 2 -> image.copy();
    case 3 -> image.get(Tensor.ALL, Tensor.ALL, 0); // take RED channel for region member test
    default -> throw new Throw(image);
    };
  }
}
