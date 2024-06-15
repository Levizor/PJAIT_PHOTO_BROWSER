package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ImageViewerPanel extends JPanel {
    BufferedImage image;
    double scale = 0.6;
    double tx, ty;
    Point lastPoint;
    boolean flag = true;

    int buffcounter = 0;

    public ImageViewerPanel(File file) {
        setBackground(new Color(0x00000));
        setLayout(null);
        try {
            this.image = ImageIO.read(file);
        } catch (Exception ex) {

        }

        setPreferredSize(new Dimension());
        tx = (MainFrame.mainFrame.getWidth() - image.getWidth() * scale) / 2;
        ty = (MainFrame.mainFrame.getHeight() - image.getHeight() * scale) / 2;

        addZoom();
        addDrag();
        addKeys();

        setOpaque(false);
        setDoubleBuffered(true);

    }

    public void calculateTranslate() {
        double scalex = (double) getWidth() / image.getWidth();
        double scaley = (double) getHeight() / image.getHeight();
        scale = scalex < scaley ? scalex : scaley;
        tx = Math.abs(getWidth() - image.getWidth() * scalex) / 2;
        ty = Math.abs(getHeight() - image.getHeight() * scaley) / 2;
    }

    public void addKeys() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "zoomin");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomout");

        am.put("zoomin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                scale *= 1.1;
                repaint();
            }
        });

        am.put("zoomout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale /= 1.1;
                repaint();
            }
        });
        am.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ty += getHeight() / 25;
                repaint();
            }
        });

        am.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ty -= getHeight() / 25;
                repaint();
            }
        });

        am.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tx += getWidth() / 25;
                repaint();
            }
        });

        am.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tx -= getWidth() / 25;
                repaint();
            }
        });
    }

    public void addDrag() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }

        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    tx += e.getX() - lastPoint.getX();
                    ty += e.getY() - lastPoint.getY();

                    lastPoint = e.getPoint();
                    buffcounter++;

                    if (buffcounter == 6) {
                        repaint();
                        buffcounter = 0;
                    }
                }
            }
        });

    }

    public void addZoom() {
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (image != null) {
                    int n = e.getWheelRotation();
                    double s = scale;

                    if (n < 0) {
                        s *= 1.1;
                    } else if (n > 0) {
                        s /= 1.2;
                    }

                    tx = e.getX() - (e.getX() - tx) * (s / scale);
                    ty = e.getY() - (e.getY() - ty) * (s / scale);

                    scale = s;

                    repaint();
                    revalidate();
                }
            }
        });
    }

    public void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateTranslate();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            super.paintComponent(g);
            return;
        }
        if (flag) {
            flag = false;
            calculateTranslate();
        }
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.translate(tx, ty);
        g2.scale(scale, scale);

        g2.drawImage(image, 0, 0, null);
    }

}
