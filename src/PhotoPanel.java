package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PhotoPanel extends JPanel {

    private Photo photo;
    private JLabel title;

    public PhotoPanel(Photo photo) {
        super(new BorderLayout());
        this.photo = photo;
        Image imageScaled;
        try {
            BufferedImage bufImage = ImageIO.read(photo.getFile());
            double ratio = (double) bufImage.getWidth() / bufImage.getHeight();

            int width = (int) (bufImage.getWidth() / MainPanel.sizeModifier);
            imageScaled = createThumbnail(bufImage, width,
                    (int) (width / ratio));

            JLabel image = new JLabel(new ImageIcon(imageScaled));
            add(image, BorderLayout.CENTER);
            addTitle();
            addListeners();
        } catch (Exception e) {
            return;
        }

    }

    public void addTitle() {
        if (title != null) {
            remove(title);
        }
        title = new JLabel(photo.getTitle());
        add(title, BorderLayout.SOUTH);
    }

    public Photo getPhoto() {
        return photo;
    }

    private BufferedImage createThumbnail(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    public void addListeners() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem delete = new JMenuItem("Delete");
        contextMenu.add(delete);
        delete.addActionListener(e -> {
            Collection.colList.getSelectedValue().remove(photo);
            if (Collection.colList.getSelectedValue() == Collection.allPhotosCollection) {
                for (Collection col : Collection.allCollections) {
                    col.remove(photo);
                }
            }
            JPanel colPanel = MainPanel.colPanel;
            Component[] photoPanels = colPanel.getComponents();
            for (Component comp : photoPanels) {
                if ((PhotoPanel) comp == this) {
                    colPanel.remove(comp);
                    colPanel.revalidate();
                    colPanel.repaint();
                    break;
                }
            }
        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
        addMouseListener(new LeftClick());
    }

    public static PhotoPanel getPhotoPanel(Photo photo) {
        JPanel colPanel = MainPanel.colPanel;
        Component[] photoPanels = colPanel.getComponents();
        for (Component comp : photoPanels) {
            if (((PhotoPanel) comp).getPhoto() == photo) {
                return (PhotoPanel) comp;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PhotoPanel))
            return false;
        return photo == ((PhotoPanel) object).getPhoto();
    }

    public class LeftClick extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                try {
                    new ImageViewer(photo.getTitle(), photo.getFile());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (e.getClickCount() == 1) {
                MainPanel.showPhotoInfo(photo);
            }
        }

    }

}
