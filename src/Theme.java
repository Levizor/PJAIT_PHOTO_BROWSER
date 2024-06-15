package src;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

public class Theme {

    public static void turnOn() {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
