package src;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

class MainPanel extends JPanel {

    public static JPanel colList;
    public static JPanel photoInfo;
    public static double sizeModifier = 5;
    public static JSplitPane eastSplit;
    public static JSplitPane westSplit;
    public static JPanel mainPanel;
    public static JPanel viewPanel;
    public static CollectionLoader colLoader;
    public static Object loaderLock = new Object();
    public static JPanel colPanel;

    public static boolean infoClosed = true;

    public static Point scrollPos = new Point();

    enum Split {
        EASTSPLIT, WESTSPLIT
    }

    public MainPanel() {
        super(new BorderLayout());
        mainPanel = this;
        viewPanel = new JPanel(new BorderLayout());

        photoInfo = new JPanel(new BorderLayout());
        JScrollPane colList = new JScrollPane(Collection.colList);
        colList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        colList.setMinimumSize(new Dimension(100, 0));
        westSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                colList, viewPanel);

        photoInfo.setPreferredSize(new Dimension(100, 0));

        eastSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                westSplit, photoInfo);
        eastSplit.setResizeWeight(1);
        eastSplit.setDividerLocation(2000);
        westSplit.setOneTouchExpandable(true);
        westSplit.setDividerLocation(getWidth());

        add(eastSplit, BorderLayout.CENTER);

        Collection.colList.setSelectedIndex(0);
        Collection.colList.updateUI();

        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 20);
        sizeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int value = sizeSlider.getValue();
                MainPanel.sizeModifier = 100d / value;
                showCollection(Collection.colList.getSelectedValue());
            }
        });

        JPanel south = new JPanel(new GridLayout(1, 2));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add photos");
        JButton delete = new JButton("Delete collection");

        add.addActionListener(new FileDialog());
        delete.addActionListener(e -> {
            int index = Collection.colList.getSelectedIndex();
            if (Collection.colList.getSelectedValue().getTitle().equals(Collection.allPhotosCollection.getTitle())) {
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "You can't delete All Photos collection");
                return;
            }
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this collection?");
            if (response == JOptionPane.YES_OPTION) {
                Collection.colList.getModel().removeAt(index);
                Collection.colList.updateUI();
                Collection.colList.setSelectedIndex(0);
                updateCollectionView();

            } else {
                return;
            }

        });
        buttons.add(add);
        buttons.add(delete);
        south.add(buttons);

        JPanel slid = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        slid.add(sizeSlider);
        south.add(slid);

        add(south, BorderLayout.SOUTH);
    }

    public static void showPhotoInfo(Photo photo) {

        if (infoClosed) {
            eastSplit.setDividerLocation(mainPanel.getWidth() - mainPanel.getWidth() / 2);
            infoClosed = false;
        }
        photoInfo.setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridLayout(2, 1));

        JPanel image = new ImageViewerPanel(photo.getFile());
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    try {
                        new ImageViewer(photo.getTitle(), photo.getFile());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(photo.getTitle());
        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEFT));
        title.add(titleLabel);
        panel.add(title);
        panel.add(titleField);

        JLabel dateLabel = new JLabel("Date:");
        JPanel date = new JPanel(new FlowLayout(FlowLayout.LEFT));
        date.add(dateLabel);
        JTextField dateField = new JTextField(Photo.sdf.format(photo.getDate()));
        panel.add(date);
        panel.add(dateField);

        JLabel descriptionLabel = new JLabel("Description:");
        JPanel description = new JPanel(new FlowLayout(FlowLayout.LEFT));
        description.add(descriptionLabel);
        JTextArea descriptionArea = new JTextArea(photo.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        panel.add(description);
        panel.add(descriptionScrollPane);

        JLabel tagsLabel = new JLabel("Tags");
        JTextField tagsField = new JTextField(String.join(", ", photo.getTags()));
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tags.add(tagsLabel);
        panel.add(tags);
        panel.add(tagsField);

        JButton save = new JButton("Save Changes");
        JButton close = new JButton("Close");
        JButton delete = new JButton("Delete photo");
        delete.addActionListener(e -> {
            eastSplit.setDividerLocation(mainPanel.getWidth());
            infoClosed = true;
            Collection.colList.getSelectedValue().remove(photo);
            if (Collection.colList.getSelectedValue() == Collection.allPhotosCollection) {
                for (Collection col : Collection.allCollections) {
                    col.remove(photo);
                }
            }
            JPanel colPanel = MainPanel.colPanel;
            Component[] photoPanels = colPanel.getComponents();
            for (Component comp : photoPanels) {
                if (((PhotoPanel) comp).getPhoto().equals(photo)) {
                    colPanel.remove(comp);
                    colPanel.revalidate();
                    colPanel.repaint();
                    break;
                }
            }
        });
        delete.addActionListener(e -> {
        });
        close.addActionListener(e -> {
            eastSplit.setDividerLocation(mainPanel.getWidth());
            infoClosed = true;
        });
        save.addActionListener(e -> {
            photo.setTitle(titleField.getText());
            try {
                photo.setDate(dateField.getText());
            } catch (ParseException parseEx) {
                JOptionPane.showMessageDialog(panel, "Error while parsing date",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            photo.setDescription(descriptionArea.getText());
            photo.setTags(List.of(tagsField.getText().split("\\s*,\\s*")));
            Saver.save(Collection.allCollections, MainFrame.data);
            JOptionPane.showMessageDialog(panel, "Changes saved!");

            PhotoPanel asociatedPanel = PhotoPanel.getPhotoPanel(photo);
            asociatedPanel.addTitle();
            asociatedPanel.revalidate();
            asociatedPanel.repaint();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(save);
        buttons.add(close);
        buttons.add(delete);
        panel.add(buttons);

        main.add(image);
        main.add(panel);

        photoInfo.removeAll();
        photoInfo.add(main);
        photoInfo.revalidate();
        photoInfo.repaint();

    }

    public static void updateCollectionView() {
        Collection collection = Collection.colList.getSelectedValue();
        showCollection(collection);
    }

    public static synchronized void showCollection(Collection collection) {
        if (colLoader != null) {
            colLoader.cancel();
        }
        viewPanel.removeAll();
        if (collection == null) {
            return;
        }
        View view = MainFrame.currentView;

        if (view == View.GALLERY) {
            colLoader = new CollectionLoader(collection);
            colLoader.execute();
        } else if (view == View.LIST) {
            JList<Photo> photoList = new JList<Photo>();
            photoList.setModel(collection);
            photoList.addListSelectionListener(collection);
            photoList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Photo photo = photoList.getSelectedValue();
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

            });
            JScrollPane scrollPane = new JScrollPane(photoList);
            viewPanel.add(scrollPane, BorderLayout.CENTER);
            viewPanel.revalidate();
            viewPanel.repaint();
        }
    }

    private static class CollectionLoader extends Thread {
        private List<Photo> photos;
        private int numThreads;
        private Thread[] threads;
        private List<Dimension> rowsDimensions;
        private int row = 0;
        JScrollPane scrollPane;

        public CollectionLoader(Collection collection) {
            this.photos = new ArrayList<>(collection.getPhotos());
            rowsDimensions = new ArrayList<>();
            rowsDimensions.add(new Dimension());

            colPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            scrollPane = new JScrollPane(colPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            viewPanel.add(scrollPane, BorderLayout.CENTER);
            viewPanel.revalidate();
            viewPanel.repaint();
        }

        private synchronized void addToPanel(PhotoPanel photoPanel) {
            calculateRowDimension(photoPanel);
            colPanel.setPreferredSize(getPanelDimension());
            colPanel.add(photoPanel);
            colPanel.revalidate();
            colPanel.repaint();
        }

        private Dimension getPanelDimension() {
            int height = 0;
            for (Dimension d : rowsDimensions) {
                height += d.height;
            }
            return new Dimension(viewPanel.getWidth(), height + 100);
        }

        private synchronized void calculateRowDimension(PhotoPanel photoPanel) {
            Dimension rd = rowsDimensions.get(row);
            Dimension ppd = photoPanel.getPreferredSize();

            if (rd.width + ppd.width < viewPanel.getWidth()) {
                if (ppd.height > rd.height) {
                    rd.height = ppd.height;
                }
                rd.width += ppd.width;
            } else {
                row++;
                rowsDimensions.add(new Dimension(ppd.width, ppd.height));
            }

        }

        public void execute() {
            numThreads = Runtime.getRuntime().availableProcessors();
            threads = new Thread[numThreads];
            int part = photos.size() / numThreads;

            for (int i = 0; i < numThreads; i++) {
                if (i == numThreads - 1) {
                    threads[i] = new Thread(new Runner(i * part, photos.size()));
                    threads[i].start();
                } else {
                    threads[i] = new Thread(new Runner(i * part, part * (i + 1)));
                    threads[i].start();
                }
            }
        }

        public void cancel() {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }

        private class Runner implements Runnable {
            int from;
            int to;

            public Runner(int from, int to) {
                this.from = from;
                this.to = to;
            }

            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    for (int i = from; i < to; i++) {
                        PhotoPanel panel = new PhotoPanel(photos.get(i));
                        addToPanel(panel);
                    }
                    return;
                }

            }
        }
    }
}
