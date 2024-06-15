package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ImageViewer extends JFrame {
    public ImageViewer(String photoTitle, File image) throws Exception {
        super(photoTitle);
        setBackground(new Color(0x000000));
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        setUndecorated(true);

        JLayeredPane layerpane = new JLayeredPane();
        add(layerpane);

        JPanel back = new JPanel();
        back.setLayout(new BorderLayout());
        back.setBackground(Pallette.Background);
        back.add(new ImageViewerPanel(image), BorderLayout.CENTER);
        gd.setFullScreenWindow(this);
        back.setBounds(0, 0, getWidth(), getHeight());
        layerpane.add(back, JLayeredPane.DEFAULT_LAYER);

        MyButton close = new MyButton("close.png");
        close.addActionListener(e -> dispose());
        close.setSize(50, 50);
        close.setBounds(MainFrame.mainFrame.getWidth() - 50, 0, 50, 50);

        layerpane.add(close, JLayeredPane.PALETTE_LAYER);

        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0);
        layerpane.getInputMap().put(key, "close");
        layerpane.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setVisible(true);
    }

}

class MyButton extends JButton {
    Image image;

    public MyButton(String path) {
        ImageIcon icon = new ImageIcon(path);
        image = icon.getImage();
        setOpaque(false);
        setContentAreaFilled(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
