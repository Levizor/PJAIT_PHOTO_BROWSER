package src;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FileDialog implements ActionListener {
    Boolean filesFound = false;
    private Future<File[]> futureFiles;
    private File[] files;

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(MainFrame.mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
            ExecutorService exServ = Executors.newSingleThreadExecutor();
            futureFiles = exServ.submit(() -> ImageFilesFinder.findIndividualImages(files));
            exServ.shutdown();
        } else {
            return;
        }

        processImageFiles();
    }

    public void processImageFiles() {
        DialogFile dialog = new DialogFile(MainFrame.mainFrame);
        if (!dialog.confirmed) {
            return;
        }

        Collection collection;
        if (dialog.exCollection) {
            collection = dialog.chosen;
        } else if (dialog.newCollection) {
            for (Collection col : Collection.allCollections) {
                if (col.getTitle().equals(dialog.newColName)) {
                    JOptionPane.showMessageDialog(MainFrame.mainFrame, "Collection with this name already exists.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            collection = new Collection(dialog.newColName);

        } else {
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "Some error occured.", "Error while opening file",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        while (true) {

            if (futureFiles.isDone()) {

                try {
                    files = futureFiles.get();
                    for (File file : files) {
                        try {
                            collection.add(new Photo(file.toPath().toString()));
                        } catch (Exception exception) {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Collection.colList.updateUI();
                MainPanel.updateCollectionView();
                return;

            }

            try {

                Thread.sleep(1000);
            } catch (Exception e) {

            }

        }

    }
}

class DialogFile extends JDialog {
    public boolean newCollection;
    public boolean exCollection;
    public boolean confirmed;
    public String newColName;
    public Collection chosen;

    public DialogFile(JFrame parent) {
        super(parent, "Choose option", true);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        JLabel label = new JLabel("Do you want to open files in a new Collection or add them to existing one?");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JButton newCol = new JButton("New Collection");
        JButton exCol = new JButton("Existing Collection");
        JButton cancel = new JButton("Cancel");

        JPanel butpanel = new JPanel();
        butpanel.add(newCol);
        butpanel.add(exCol);
        butpanel.add(cancel);
        add(butpanel, BorderLayout.SOUTH);

        cancel.addActionListener(e -> {
            dispose();
            return;
        });

        newCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newCollection = true;
                newColName = JOptionPane.showInputDialog("Enter new Collection name: ");
                confirmed = true;
                dispose();
                return;
            }
        });

        exCol.addActionListener(e -> {
            if (Collection.allCollections.size() == 0) {
                JOptionPane.showMessageDialog(parent, "You don't have any collections yet. Create one.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

            } else {
                ChooseCol dialog = new ChooseCol(this);

                if (dialog.yes) {
                    confirmed = true;
                    exCollection = true;
                    chosen = dialog.chosen;
                }
                dispose();
                return;

            }
        });

        pack();
        setVisible(true);
    }

}

class ChooseCol extends JDialog {
    JComboBox<Collection> box;
    boolean yes;
    Collection chosen;

    public ChooseCol(JDialog owner) {
        super(owner, "Choose existing collection", true);

        setLocationRelativeTo(owner);

        setLayout(new BorderLayout());

        box = new JComboBox<>();
        for (Collection col : Collection.allCollections) {
            box.addItem(col);
        }

        JLabel label = new JLabel("Choose one of the collections");
        JPanel boxPanel = new JPanel();
        boxPanel.add(box);
        add(label, BorderLayout.NORTH);
        add(boxPanel, BorderLayout.CENTER);

        JButton ok = new JButton("Ok");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            yes = true;
            chosen = (Collection) box.getSelectedItem();
            dispose();
        });

        cancel.addActionListener(e -> {
            yes = false;
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
}
