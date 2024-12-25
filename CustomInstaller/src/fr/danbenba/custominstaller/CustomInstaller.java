package fr.danbenba.custominstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Random;

@SuppressWarnings("serial")
public class CustomInstaller extends JFrame {
    private JLabel loadingLabel;
    private JProgressBar progressBar;
    private JButton skipButton;
    private Properties config;
    private ProgressWorker progressWorker;

    // Tableau de textes en anglais pour l'avancement
    private final String[] loadingTexts = {
            "Starting installation process...",
            "Analyzing system configurations...",
            "Validating installation files...",
            "Unpacking necessary resources...",
            "Installing required components...",
            "Applying configuration settings...",
            "Completing installation process...",
            "Cleaning up temporary files..."
    };


    public CustomInstaller() {
        // 1) Charger la config
        config = loadConfig();
        if (config.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Configuration file not found or is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle(config.getProperty("app.title", "Custom Installer") + " Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String iconPath = config.getProperty("app.iconPath");
        if (iconPath != null) {
            java.net.URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                System.err.println("Icon file not found at path: " + iconPath);
            }
        } else {
            System.err.println("app.iconPath not specified in config.properties");
        }

        // Utiliser un JPanel principal avec BoxLayout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Ajouter des marges

        // Ajouter le JLabel pour les messages d'état
        loadingLabel = new JLabel("Initializing...");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loadingLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espace vertical

        // Initialisation de la barre de progression
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(350, 25));
        progressBar.setMaximumSize(new Dimension(350, 25));
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(50, 205, 50)); // Barre verte
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(progressBar);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espace vertical

        // Ajouter le bouton "Skip"
        skipButton = new JButton("Skip");
        skipButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        skipButton.setMaximumSize(new Dimension(100, 30));
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (progressWorker != null && !progressWorker.isDone()) {
                    progressWorker.cancel(true); // Annuler le SwingWorker en cours
                }
                proceedToInstallerGUI();
            }
        });
        mainPanel.add(skipButton);

        setContentPane(mainPanel);
        setResizable(false);
        setSize(400, 160);
        setLocationRelativeTo(null); // Centre la fenêtre
        setAlwaysOnTop(true); // Garde la fenêtre au premier plan

        // Utilisation de SwingWorker pour gérer la progression
        progressWorker = new ProgressWorker();
        progressWorker.execute();

        setVisible(true);
    }

    /**
     * Chargement config
     */
    private Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file 'config.properties' not found in the classpath.");
            }
            properties.load(input);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Debug : Afficher les propriétés chargées
        properties.forEach((key, value) -> System.out.println(key + ": " + value));

        return properties;
    }

    /**
     * Met à jour le texte d'avancement selon différents seuils
     */
    private void updateLoadingText(int progress) {
        int numberOfTexts = loadingTexts.length;
        int step = 100 / numberOfTexts;
        int index = Math.min(progress / step, numberOfTexts - 1); // Correction ici
        loadingLabel.setText(loadingTexts[index]);
    }

    /**
     * Procéder à InstallerGUI après la progression ou lors du clic sur "Skip"
     */
    private void proceedToInstallerGUI() {
        dispose(); // Ferme la fenêtre actuelle
        try {
            new InstallerGUI().setVisible(true);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * SwingWorker pour gérer la progression de la barre de chargement
     */
    private class ProgressWorker extends SwingWorker<Void, Integer> {
        private final Random random = new Random();

        @Override
        protected Void doInBackground() throws Exception {
            int progress = 0;
            while (progress < 100 && !isCancelled()) {
                // Simuler une progression plus réaliste
                Thread.sleep(random.nextInt(300) + 200); // Pause entre 200 et 500 ms

                // Calcul de l'incrément
                int maxIncrement = Math.max(1, 10 - (progress / 10));
                int increment = random.nextInt(maxIncrement) + 1;
                progress += increment;
                progress = Math.min(progress, 100);

                // Publier la progression
                publish(progress);
                setProgress(progress);
            }
            return null;
        }

        @Override
        protected void process(java.util.List<Integer> chunks) {
            if (isCancelled()) {
                return;
            }
            int latestProgress = chunks.get(chunks.size() - 1);
            progressBar.setValue(latestProgress);
            updateLoadingText(latestProgress);
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                proceedToInstallerGUI();
            }
        }
    }

    public static void main(String[] args) {
        // Appliquer le look and feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new CustomInstaller());
    }
}
