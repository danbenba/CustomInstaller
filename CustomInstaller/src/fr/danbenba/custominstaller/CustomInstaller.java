package fr.danbenba.custominstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.Random;

@SuppressWarnings("serial")
public class CustomInstaller extends JFrame {
    private JLabel loadingLabel;
    private JProgressBar progressBar;

    public CustomInstaller() {
        setTitle("Custom Installer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 120);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null); // Centre la fenêtre

        loadingLabel = new JLabel("Loading...");
        add(loadingLabel);

        setAlwaysOnTop(true); // Garde la fenêtre au premier plan

        // Initialisation de la barre de progression
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(279, 24));
        progressBar.setBorderPainted(true);
        progressBar.setForeground(new Color(50, 205, 50)); // Change la couleur de la barre (vert)
        add(progressBar);

        // Timer pour mettre à jour la barre de progression
        Timer timer = new Timer(100, new ActionListener() {
            private int progress = 0;
            private Random r = new Random();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (progress < 100) {
                    int increment = r.nextInt(10) + 1; // Incrément aléatoire entre 1 et 10
                    progress += increment;
                    updateProgress(progress);
                }

                if (progress >= 100) {
                    ((Timer) e.getSource()).stop();
                    dispose(); // Ferme la fenêtre une fois la progression terminée
                    try {
                        new InstallerGUI().setVisible(true);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        timer.start();

        setVisible(true);
    }

    public void updateProgress(int value) {
        progressBar.setValue(Math.min(value, 100)); // Assure que la progression ne dépasse pas 100
        if (value >= 99) {
            loadingLabel.setText("Loading...");
        } else if (value >= 40) {
            loadingLabel.setText("Unpacking...");
        } else if (value >= 30) {
            loadingLabel.setText("Wiring at 0x000054...");
        }
    }

    public static void main(String[] args) {
        // Appliquer le look and feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomInstaller();
            }
        });
    }
}
