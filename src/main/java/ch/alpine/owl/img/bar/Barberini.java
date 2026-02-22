// code by jph
package ch.alpine.owl.img.bar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import ch.alpine.bridge.awt.LazyMouse;
import ch.alpine.bridge.awt.LazyMouseListener;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.img.PhotoImport;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Jpeg;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.sca.Sign;

public class Barberini {
  public static final Path BASE = HomeDirectory.Pictures.resolve("perspectives");

  // ---
  public static class ImagePoly {
    public ImagePoly(String name) {
      this.name = name;
    }

    final String name;
    final Tensor tensor = Array.zeros(4, 2);

    @Override
    public String toString() {
      return name + "=" + tensor;
    }
  }

  public static final Path SRC = BASE.resolve("src");
  public static final Path DST = BASE.resolve("dst");
  public static final Path PROPFILE = BASE.resolve("index.properties");
  private final JFrame jFrame = new JFrame();
  private final JLabel jLabel = new JLabel();
  private Properties properties;
  private ImagePoly imagePoly = null;
  int index;
  private BufferedImage bufferedImage = null;
  private final JComponent jComponent = new JComponent() {
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
      graphics.setColor(Color.WHITE);
      GeometricLayer gl = new GeometricLayer(Tensors.fromString("{{0,1,0},{1,0,0},{0,0,1}}"));
      Path2D path2d = gl.toPath2D(imagePoly.tensor, true);
      Graphics2D g2d = (Graphics2D) graphics;
      g2d.draw(path2d);
    }
  };

  void load() throws IOException {
    boolean asd = false;
    for (Path file : Files.list(SRC).toList())
      if (!properties.containsKey(file.getFileName())) {
        imagePoly = new ImagePoly(file.getFileName().toString());
        asd = true;
        break;
      }
    if (!asd)
      JOptionPane.showConfirmDialog(jFrame, "done");
    index = 0;
    updateLabel();
    Path src = SRC.resolve(imagePoly.name);
    try {
      bufferedImage = PhotoImport.of(src);
      jComponent.setPreferredSize(new Dimension( //
          bufferedImage.getWidth(), //
          bufferedImage.getHeight()));
      jComponent.repaint();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Barberini() throws IOException {
    if (Files.isRegularFile(PROPFILE))
      try {
        properties = Import.properties(PROPFILE);
      } catch (Exception e) {
        e.printStackTrace();
      }
    else
      properties = new Properties();
    load();
    JScrollPane jScrollPane = new JScrollPane(jComponent, //
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, //
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPane.setBorder(null);
    JPanel jPanel = new JPanel(new BorderLayout());
    JToolBar jToolBar = new JToolBar();
    JButton jButton = new JButton("next");
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean allMatch = Flatten.scalars(imagePoly.tensor).allMatch(Sign::isPositive);
        if (allMatch) {
          properties.setProperty(imagePoly.name, imagePoly.tensor.toString());
          save();
          final BufferedImage bi = bufferedImage;
          final ImagePoly ip = imagePoly;
          if (false)
            new Thread(new Runnable() {
              @Override
              public void run() {
                BufferedImage result = ImagePerspective.rectify(bi, ip.tensor, 500_000);
                try {
                  Jpeg.put(result, HomeDirectory.Pictures.resolve("barberini", "s" + ip.name), 1);
                  System.out.println("saved");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }).start();
          try {
            load();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        } else
          System.out.println("INCOMPLETE");
      }
    });
    jToolBar.add(jButton);
    jToolBar.add(jLabel);
    jPanel.add(jToolBar, "North");
    jPanel.add(jScrollPane, "Center");
    jFrame.setContentPane(jPanel);
    // ---
    LazyMouseListener lazyMouseListener = new LazyMouseListener() {
      @Override
      public void lazyClicked(MouseEvent mouseEvent) {
        imagePoly.tensor.set(Tensors.vectorInt( //
            mouseEvent.getPoint().y, //
            mouseEvent.getPoint().x), index);
        updateLabel();
        ++index;
        index %= 4;
        jComponent.repaint();
      }
    };
    LazyMouse lazyMouse = new LazyMouse(lazyMouseListener);
    lazyMouse.addListenersTo(jComponent);
    jComponent.setFocusable(true);
    jComponent.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        int keyChar = e.getKeyChar();
        keyChar -= '1';
        if (0 <= keyChar && keyChar < 4) {
          index = keyChar;
          System.out.println("index=" + index);
        }
      }
    });
    jFrame.setBounds(20, 40, 1200, 700);
    jFrame.setVisible(true);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  void save() {
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(PROPFILE)) {
      for (Object s : properties.keySet())
        bufferedWriter.write(s + "=" + properties.getProperty((String) s) + "\n");
      bufferedWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateLabel() {
    jLabel.setText(properties.size() + " " + imagePoly.toString());
  }

  static void main() throws IOException {
    new Barberini();
  }
}
