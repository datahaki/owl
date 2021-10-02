// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/* package */ class KlotskiObstacleRegion implements Region<Tensor> {
  /** @param size
   * @return */
  public static Region<Tensor> fromSize(Tensor size) {
    return new KlotskiObstacleRegion( //
        Scalars.intValueExact(size.Get(0)), //
        Scalars.intValueExact(size.Get(1)));
  }

  // ---
  private final int sx;
  private final int sy;

  private KlotskiObstacleRegion(int sx, int sy) {
    this.sx = sx;
    this.sy = sy;
  }

  @Override
  public boolean test(Tensor state) {
    int[][] array = new int[sx][sy];
    for (Tensor stone : state) {
      int index = Scalars.intValueExact(stone.Get(0));
      int px = Scalars.intValueExact(stone.Get(1));
      int py = Scalars.intValueExact(stone.Get(2));
      switch (index) {
      case 0:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      case 1:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        break;
      case 2:
        ++array[px + 0][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 3:
        ++array[px + 0][py + 0];
        break;
      case 4:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 2][py + 0];
        break;
      case 5:
        for (int cx = 0; cx < 5; ++cx)
          for (int cy = 0; cy < 5; ++cy)
            ++array[px + cx][py + cy];
        break;
      case 6:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 7:
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      case 8:
        --array[px + 0][py + 0];
        --array[px + 0][py + 4];
        --array[px + 4][py + 0];
        --array[px + 4][py + 4];
        for (int cx = 0; cx < 5; ++cx)
          for (int cy = 0; cy < 5; ++cy)
            ++array[px + cx][py + cy];
        break;
      default:
        throw new RuntimeException("unknown: " + index);
      }
    }
    for (int px = 0; px < sx; ++px)
      for (int py = 0; py < sy; ++py)
        if (1 < array[px][py])
          return true;
    return false;
  }
}
