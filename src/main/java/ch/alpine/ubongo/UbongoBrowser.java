// code by jph
package ch.alpine.ubongo;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldInteger;
import ch.alpine.bridge.ref.ann.FieldSelectionCallback;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.bridge.swing.LookAndFeels;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.img.ImageRotate;
import ch.alpine.tensor.io.ImageFormat;

/* package */ class UbongoBrowser extends AbstractDemo {
  private final UbongoBoard ubongoBoard;
  private final List<List<UbongoEntry>> list;

  @ReflectionMarker
  public static class Param {
    private final int limit;

    public Param(int limit) {
      this.limit = limit;
    }

    @FieldInteger
    @FieldSelectionCallback("index")
    public Scalar index = RealScalar.of(0);

    public List<Scalar> index() {
      return IntStream.range(0, limit).mapToObj(RealScalar::of).toList();
    }
  }

  private final Param param;

  public UbongoBrowser(UbongoBoard ubongoBoard, List<List<UbongoEntry>> list) {
    this(new Param(list.size()), ubongoBoard, list);
  }

  public UbongoBrowser(Param param, UbongoBoard ubongoBoard, List<List<UbongoEntry>> list) {
    super(param);
    this.param = param;
    this.ubongoBoard = Objects.requireNonNull(ubongoBoard);
    this.list = Objects.requireNonNull(list);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int index = param.index.number().intValue();
    List<UbongoEntry> solution = list.get(index);
    {
      int scale = 30;
      List<Integer> size = Dimensions.of(ubongoBoard.mask());
      Tensor tensor = UbongoRender.of(size, solution);
      int pix = 50;
      int piy = 120;
      graphics.drawImage(ImageFormat.of(tensor), pix, piy, size.get(1) * scale, size.get(0) * scale, null);
    }
    int pix = 0;
    for (UbongoEntry ubongoEntry : solution) {
      UbongoEntry ubongoPiece = new UbongoEntry();
      ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
      ubongoPiece.ubongo = ubongoEntry.ubongo;
      List<Integer> size = Dimensions.of(ubongoPiece.stamp);
      Tensor tensor = UbongoRender.of(size, List.of(ubongoPiece));
      // List<Integer> size2 = Dimensions.of(tensor);
      int scale = 15;
      int piw = size.get(1) * scale;
      graphics.drawImage(ImageFormat.of(tensor), 30 + pix, 30, piw, size.get(0) * scale, null);
      pix += piw + 20;
    }
  }

  public static void main(String[] args) {
    LookAndFeels.LIGHT.updateComponentTreeUI();
    UbongoBoards ubongoBoards = UbongoBoards.STANDARD;
    List<List<UbongoEntry>> list = // ubongoBoards.solve();
        UbongoLoader.INSTANCE.load(ubongoBoards);
    if (list.isEmpty()) {
      System.err.println("no solutions");
    } else {
      UbongoBrowser ubongoBrowser = new UbongoBrowser(ubongoBoards.board(), list);
      ubongoBrowser.setVisible(800, 600);
    }
  }
}
