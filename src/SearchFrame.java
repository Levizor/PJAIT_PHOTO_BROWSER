package src;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class SearchFrame extends JFrame {
    JPanel panel;
    public static JTextField titleField;
    public static JTextField wordInDescField;
    public static JTextField tagsField;
    public static JFrame searchFrame;
    public static JTextField dateField;
    public static boolean andTag = false;

    public static String prompt = "Comma, Separated, Tags";

    public SearchFrame() {
        super("Search");
        searchFrame = this;
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1));
        add(panel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Title");
        titleField = new JTextField("");
        panel.add(titleLabel);
        panel.add(titleField);

        JLabel wordInDesc = new JLabel("Words in description");
        wordInDescField = new JTextField("");
        panel.add(wordInDesc);
        panel.add(wordInDescField);

        JLabel date = new JLabel("Date (DD-MM-YYYY)");
        dateField = new JTextField();
        panel.add(date);
        panel.add(dateField);

        JLabel tagsLabel = new JLabel("Tags");
        ButtonGroup btnGroup = new ButtonGroup();
        JRadioButton or = new JRadioButton("OR");
        JRadioButton and = new JRadioButton("AND");
        or.setSelected(true);
        or.addActionListener(e -> {
            and.setSelected(false);
            andTag = false;
        });
        and.addActionListener(e -> {
            or.setSelected(false);
            andTag = true;
        });
        btnGroup.add(or);
        btnGroup.add(and);

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tagsPanel.add(tagsLabel);
        tagsPanel.add(or);
        tagsPanel.add(and);
        panel.add(tagsPanel);
        tagsField = new JTextField(prompt);
        tagsField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                if (tagsField.getText().isEmpty()) {
                    tagsField.setText(prompt);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (tagsField.getText().equals(prompt)) {
                    tagsField.setText("");
                }
            }
        });
        panel.add(tagsField);
        JButton search = new JButton("Search");
        search.addActionListener(new Search());
        panel.add(search);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setPreferredSize(new Dimension(600, 400));
        pack();
        setVisible(true);

    }
}

class Search implements ActionListener {
    boolean and;
    Pattern titlePattern;
    Pattern desWordPattern;
    Date date;
    String[] tags;

    public Search() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!SearchFrame.titleField.getText().isEmpty()) {
            titlePattern = Pattern.compile(SearchFrame.titleField.getText());
        } else {
            titlePattern = null;
        }
        if (!SearchFrame.wordInDescField.getText().isEmpty()) {
            desWordPattern = Pattern.compile(SearchFrame.wordInDescField.getText());

        } else {
            desWordPattern = null;
        }
        if (!SearchFrame.tagsField.getText().equals(SearchFrame.prompt)) {
            tags = SearchFrame.tagsField.getText().split("\\s*,\\s*");
        } else {
            tags = null;
        }
        and = SearchFrame.andTag;

        if (!SearchFrame.dateField.getText().isEmpty()) {
            try {
                date = Photo.sdf.parse(SearchFrame.dateField.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(SearchFrame.searchFrame,
                        "Error while parsing the date", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            date = null;
        }
        Collection result = search();
        System.out.println("THIS IS THE COLLECTION:" + result.getPhotos());
        MainPanel.showCollection(result);
        SearchFrame.searchFrame.dispose();
    }

    private Collection search() {
        Collection result = new Collection();

        System.out.println(Collection.allCollections);
        Collection.allPhotosCollection.stream().peek(System.out::println)
                .filter(new SearchPredicate()).forEach(photo -> result.add(photo));

        return result;
    }

    class SearchPredicate implements Predicate<Photo> {

        @Override
        public boolean test(Photo photo) {
            System.out.println(photo.getTags().toString());

            if (titlePattern != null) {
                if (!photo.getTitle().isEmpty()) {
                    Matcher titleMatcher = titlePattern.matcher(photo.getTitle());
                    if (!titleMatcher.find()) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            if (desWordPattern != null) {
                if (!photo.getDescription().isEmpty()) {
                    Matcher descMatcher = desWordPattern.matcher(photo.getDescription());
                    if (!descMatcher.find()) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            if (date != null) {
                if (!photo.getDate().equals(date)) {
                    return false;
                }
            }

            if (tags != null) {
                if (!and) {
                    boolean has = false;
                    for (String tag : tags) {
                        if (photo.hasTag(tag)) {
                            has = true;
                            break;
                        }
                    }
                    if (!has)
                        return false;
                } else {
                    for (String tag : tags) {
                        if (!photo.hasTag(tag))
                            return false;
                    }
                }

            }
            return true;
        }

    }

}
