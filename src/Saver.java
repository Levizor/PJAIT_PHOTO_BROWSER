package src;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JOptionPane;

public class Saver {
    public static void save(List<Collection> cols, Path path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(cols);
        } catch (IOException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "Could not save info", "Shit happens",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Collection> load(Path path) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path));
        return (List<Collection>) ois.readObject();
    }
}
