package src;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import src.MainPanel.Split;
import java.util.ArrayList;

public class MainFrame extends JFrame implements ActionListener {
    public static Path data = Paths.get("appData.dat");
    public static JFrame mainFrame;
    public static MainPanel panel;
    public static View currentView = View.GALLERY;
    public static Split currentSplit = Split.WESTSPLIT;

    public MainFrame() {
        super("Fbrow");
        try {
            Collection.allCollections = (ArrayList<Collection>) Saver.load(data);
            Collection.allPhotosCollection = Collection.allCollections.get(0);
        } catch (Exception e) {

        }
        System.out.println("Loaded");
        setLocationRelativeTo(null);
        MainFrame.mainFrame = this;
        ImageIcon icon = new ImageIcon("icon.png");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setIconImage(icon.getImage());

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu("File");
        menuBar.add(file);

        JMenu view = new JMenu("View");
        menuBar.add(view);
        JMenuItem list = new JMenuItem("List");
        JMenuItem gallery = new JMenuItem("Gallery");
        JMenuItem search = new JMenuItem("Search");
        search.addActionListener(e -> {
            new SearchFrame();
        });

        file.add(search);
        list.addActionListener(this);
        gallery.addActionListener(this);
        view.add(list);
        view.add(gallery);

        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(new FileDialog());
        file.add(open);

        add(new MainPanel());

        setPreferredSize(new Dimension(1200, 900));
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Saver.save(Collection.allCollections, data);
                dispose();
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JMenuItem) (e.getSource())).getText()) {
            case "Gallery":
                currentView = View.GALLERY;
                break;
            case "List":
                currentView = View.LIST;
                break;
        }
        MainPanel.updateCollectionView();
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
