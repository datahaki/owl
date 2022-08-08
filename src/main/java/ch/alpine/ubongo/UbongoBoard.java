// code by jph
package ch.alpine.ubongo;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Primitives;

/* package */ class UbongoBoard {
  public static final Scalar FREE = RealScalar.ONE.negate();
  public static final int free = -1;

  public static UbongoBoard of(String... strings) {
    final int n = strings[0].length();
    Tensor prep = Tensors.empty();
    for (String string : strings) {
      Tensor row = Array.zeros(n);
      for (int index = 0; index < string.length(); ++index) {
        if (string.charAt(index) == 'o')
          row.set(FREE, index);
      }
      prep.append(row);
    }
    return new UbongoBoard(prep);
  }

  public static UbongoBoard of(Tensor prep) {
    return new UbongoBoard(prep.copy());
  }

  // ---
  private final Tensor mask;
  private final int dim1;
  private final int[] _mask;
  private final List<Integer> board_size;
  private final int count;
  private final Map<UbongoStamp, List<Point>> map = new HashMap<>();

  private UbongoBoard(Tensor prep) {
    mask = prep.unmodifiable();
    board_size = Dimensions.of(prep);
    dim1 = board_size.get(1);
    count = (int) mask.flatten(-1).filter(FREE::equals).count();
    _mask = Primitives.toIntArray(mask);
    // ---
    for (Ubongo ubongo : Ubongo.values())
      for (UbongoStamp ubongoStamp : ubongo.stamps()) {
        map.put(ubongoStamp, new ArrayList<>());
        Tensor stamp = ubongoStamp.stamp;
        List<Integer> size = Dimensions.of(stamp);
        for (int bi = 0; bi <= board_size.get(0) - size.get(0); ++bi) {
          for (int bj = 0; bj <= board_size.get(1) - size.get(1); ++bj) {
            boolean status = true;
            for (int si = 0; si < size.get(0); ++si)
              for (int sj = 0; sj < size.get(1); ++sj) {
                boolean occupied = stamp.get(si, sj).equals(RealScalar.ONE);
                if (occupied)
                  status &= prep.get(bi + si, bj + sj).equals(FREE);
              }
            if (status)
              map.get(ubongoStamp).add(new Point(bi, bj));
          }
        }
      }
  }

  public Tensor mask() {
    return mask;
  }

  public List<List<UbongoEntry>> filter0(int use) {
    List<List<Ubongo>> values = Ubongo.candidates(use, count);
    List<List<UbongoEntry>> solutions = new LinkedList<>();
    for (List<Ubongo> list : values) {
      List<Ubongo> _list = new ArrayList<>(list);
      Collections.sort(_list, (u1, u2) -> {
        return Integer.compare(u2.count(), u1.count());
      });
      System.out.println(_list);
      Solve solve = new Solve(_list);
      if (solve.solutions.size() == 1) {
        List<UbongoEntry> list2 = solve.solutions.get(0);
        solutions.add(list2);
        System.out.println(list2);
      }
    }
    return solutions;
  }

  class Solve {
    private final List<List<UbongoEntry>> solutions = new LinkedList<>();

    public Solve(List<Ubongo> list) {
      solve(_mask.clone(), list, Collections.emptyList());
    }

    private void solve(int[] board, List<Ubongo> list, List<UbongoEntry> entries) {
      if (list.isEmpty()) {
        solutions.add(entries);
      } else {
        final Ubongo ubongo = list.get(0); // piece
        for (UbongoStamp ubongoStamp : ubongo.stamps()) {
          List<Point> points = map.get(ubongoStamp);
          Tensor stamp = ubongoStamp.stamp;
          for (Point point : points) {
            int bi = point.x;
            int bj = point.y;
            int[] nubrd = board.clone();
            boolean status = true;
            for (int si = 0; si < ubongoStamp.size0; ++si)
              for (int sj = 0; sj < ubongoStamp.size1; ++sj) {
                boolean occupied = stamp.get(si, sj).equals(RealScalar.ONE);
                if (occupied) {
                  int index = (bi + si) * dim1 + bj + sj;
                  if (nubrd[index] == free) {
                    nubrd[index] = 0;
                  } else {
                    status = false;
                    break;
                  }
                }
              }
            // ---
            if (status) {
              UbongoEntry ubongoEntry = new UbongoEntry();
              ubongoEntry.i = bi;
              ubongoEntry.j = bj;
              ubongoEntry.ubongo = ubongo;
              ubongoEntry.stamp = stamp;
              List<UbongoEntry> arrayList = new ArrayList<>(entries);
              arrayList.add(ubongoEntry);
              solve(nubrd, Lists.rest(list), arrayList);
            }
          }
        }
      }
    }
  }

  // TODO not called
  @Deprecated
  public static void export(File folder, Tensor mask, List<List<UbongoEntry>> solutions) throws IOException {
    folder.mkdir();
    int index = 0;
    for (List<UbongoEntry> solution : solutions) {
      Tensor tensor = UbongoRender.of(Dimensions.of(mask), solution);
      Export.of(new File(folder, String.format("ub%03d.png", index)), ImageResize.nearest(tensor, 10));
      ++index;
    }
  }
}
