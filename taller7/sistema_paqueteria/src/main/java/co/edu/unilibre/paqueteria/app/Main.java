package co.edu.unilibre.paqueteria.app;

import co.edu.unilibre.paqueteria.ui.MainFrame;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            
        }
        MainFrame.mostrar();
    }
}
