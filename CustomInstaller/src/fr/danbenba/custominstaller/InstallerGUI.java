package fr.danbenba.custominstaller;

import javax.swing.*;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;
import java.util.*;
import java.util.Timer;
import java.util.zip.*;

@SuppressWarnings({ "serial", "unused" })
public class InstallerGUI extends JFrame {

    private JTextField txtEmplacement;
    private JButton btnInstaller;
    Timer timer = new Timer();
    private JButton btnClose;
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblGitHubLink;
    private JLabel lblFooter;
    private JLabel lblImage; 
    private static CountDownLatch latch = new CountDownLatch(1);

    private Properties config;

    public InstallerGUI() throws MalformedURLException {
        // Charger le fichier de configuration
        config = loadConfig();

        // Configuration du Look and Feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle(config.getProperty("app.title"));
        setSize(500, 400);
        setResizable(false); // Set dialog to non-resizable
        ImageIcon icon = new ImageIcon(getClass().getResource(config.getProperty("app.iconPath")));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);


        // Demande à l'utilisateur pour le chemin d'installation
        Object[] options = {"Installer dans le répertoire utilisateur", "Installer en tant qu'administrateur"};
        int response = JOptionPane.showOptionDialog(null,
                "Veuillez choisir l'emplacement d'installation :",
                "Emplacement d'installation",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        String emplacement = "";
        if (response == 0) { // Choix du répertoire utilisateur
            emplacement = System.getProperty("user.home") + config.getProperty("dir.user");
            System.out.println("DirDirectory : " + emplacement);
        } else if (response == 1) { // Choix du répertoire administrateur
            emplacement = config.getProperty("dir.progfile");
            if (isRunAsAdmin()) {
                System.out.println("DirDirectory : " + emplacement);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Veuillez exécuter le programme en tant qu'administrateur pour installer dans " + emplacement,
                        "Erreur: Privilèges d'administrateur requis",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            System.exit(0);
        }

        // Add Image at the Top
        ImageIcon originalIcon = new ImageIcon(new URL(config.getProperty("app.mainImage")));

        // Obtenir l'image originale
        Image originalImage = originalIcon.getImage();

        // Récupérer la largeur et la hauteur de l'image originale
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Calculer le ratio de redimensionnement pour s'assurer que l'image tient dans un cadre 64x64 sans déformation
        double aspectRatio = (double) originalWidth / originalHeight;  // <---- Calcul correct de l'aspect ratio

        // Redimensionner l'image en bannière (par exemple 400px de large et proportionnellement réduit en hauteur)
        int newWidth = 400; // Largeur pour la bannière
        int newHeight = (int) (newWidth / aspectRatio); // Ajuster la hauteur proportionnellement

        // Redimensionner l'image
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        final ImageIcon bannerIcon = new ImageIcon(scaledImage);  // Variable finale ici

        // Créer un JLabel pour afficher l'image redimensionnée et centrée
        lblImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Centrer l'image dans le JLabel
                int x = (getWidth() - bannerIcon.getIconWidth()) / 2;
                int y = (getHeight() - bannerIcon.getIconHeight()) / 2;
                g.drawImage(bannerIcon.getImage(), x, y, this);
            }
        };

        // Ajuster la taille et la position du JLabel pour qu'il occupe tout l'espace nécessaire à la bannière
        lblImage.setBounds(50, 10, 400, newHeight + 20); // Position ajustée pour la bannière
        add(lblImage);


        
        // Emplacement input (compact, placed at the bottom)
        txtEmplacement = new JTextField(emplacement);
        txtEmplacement.setBounds(10, 230, 470, 25);
        add(txtEmplacement);
        
        // Status label (log text placed above the progress bar)
        lblStatus = new JLabel("Waiting for installation...");
        lblStatus.setBounds(10, 260, 470, 25);
        lblStatus.setForeground(Color.BLACK);
        add(lblStatus);
        
        // Progress Bar with percentage (Initially invisible, placed below the status label)
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(10, 290, 470, 25); // Bottom position, compact
        progressBar.setStringPainted(true); // Display percentage
        progressBar.setVisible(false);
        add(progressBar);

        // Bouton Installer (now grays out when clicked)
        btnInstaller = new JButton("Installer");
        btnInstaller.setBounds(10, 320, 100, 25);
        btnInstaller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	showMarkdownAndStartInstallation();
                btnInstaller.setEnabled(false); // Gray out the installer button
                progressBar.setVisible(true); // Show the progress bar
                lblStatus.setText("Installing..."); // Show installation text
                installer();
            }
        });
        add(btnInstaller);

        // Bouton Close (next to the Installer button)
        btnClose = new JButton("Close");
        btnClose.setBounds(120, 320, 100, 25);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(btnClose);

        // GitHub Link (placed to the right of the Close button)
        lblGitHubLink = new JLabel(config.getProperty("link.name"));
        lblGitHubLink.setBounds(375, 321, 230, 25);
        lblGitHubLink.setForeground(Color.BLUE);
        lblGitHubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGitHubLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(config.getProperty("link.url")));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add(lblGitHubLink);

        // Label Footer (aligned 3px above the buttons)
        lblFooter = new JLabel("---- Data not found ----");
        lblFooter.setBounds(10, 228- 30, 470, 25); // Positioned 3px above buttons
        lblFooter.setForeground(Color.BLACK);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblFooter);

        updateVersionLabel();
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file 'config.properties' not found in the classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private void updateVersionLabel() {
        try {
            URL url = new URL(config.getProperty("version.url"));
            String versionText = downloadText(url);
            lblFooter.setText("---- " + versionText + " ----");
        } catch (IOException e) {
            lblFooter.setText("Server Closed");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Internal Error: Server Closed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String downloadText(URL url) throws IOException {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private void showMarkdownAndStartInstallation() {
        JDialog dialog = new JDialog(this, "Terms and Conditions", true);
        dialog.setSize(800, 630);
        dialog.setResizable(false); // Set dialog to non-resizable
        dialog.setLayout(null);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/plain"); // Set to plain text
        textPane.setEditable(false);

        try {
            URL url = new URL(config.getProperty("terms.url"));
            String termsContent = downloadText(url); // Download the terms and conditions content
            textPane.setText(termsContent);
        } catch (IOException e) {
            textPane.setText("Failed to load content.");
        }

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBounds(10, 10, 760, 500);
        dialog.add(scrollPane);

        JCheckBox chkAccept = new JCheckBox("I Accept the Terms and Conditions");
        chkAccept.setBounds(10, 520, 250, 25);
        dialog.add(chkAccept);

        JButton btnDownload = new JButton("Install");
        JButton btnExit = new JButton("Exit");

        int buttonWidth = 120;
        int buttonHeight = 25;
        int buttonYPosition = 550;
        int dialogWidth = dialog.getWidth();
        int totalButtonWidth = buttonWidth * 2 + 20;
        int startXPosition = (dialogWidth - totalButtonWidth) / 2;

        btnDownload.setBounds(startXPosition, buttonYPosition, buttonWidth, buttonHeight);
        btnDownload.setEnabled(false);
        btnDownload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                installer();
            }
        });
        dialog.add(btnDownload);

        btnExit.setBounds(startXPosition + buttonWidth + 20, buttonYPosition, buttonWidth, buttonHeight);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                System.exit(0);
            }
        });
        dialog.add(btnExit);

        chkAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnDownload.setEnabled(chkAccept.isSelected());
            }
        });

        dialog.setVisible(true);
    }

    private boolean isRunAsAdmin() {
        File testPrivilege = new File("C:\\testPrivilege.txt");
        try {
            if (testPrivilege.createNewFile()) {
                testPrivilege.delete();
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    private void installer() {
        progressBar.setVisible(true);
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                lblStatus.setText("Downloading files...");
                progressBar.setValue(10);
                Thread.sleep(500);

                URL url = new URL(config.getProperty("download.url"));
                Path tempDir = Files.createTempDirectory("CustomInstaller");
                File zipFile = downloadFile(url, tempDir);

                progressBar.setValue(30);
                lblStatus.setText("Unpacking " + zipFile.getName() + "...");
                Thread.sleep(500);
                progressBar.setValue(60);
                unzip(zipFile, tempDir);

                zipFile.delete();

                Path targetDir = Paths.get(txtEmplacement.getText());
                Files.walk(tempDir)
                    .forEach(source -> {
                        progressBar.setValue(70);
                        lblStatus.setText("Copying file " + source.getFileName());
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        copy(source, targetDir.resolve(tempDir.relativize(source)));
                        progressBar.setValue(90);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                return null;
            }

            private File downloadFile(URL url, Path tempDir) throws IOException {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                File file = tempDir.resolve("downloaded.zip").toFile();
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                return file;
            }

            @Override
            protected void done() {
                progressBar.setValue(100);
                lblStatus.setText("Installation finished");
            }
        };

        worker.execute();
    }

    private void unzip(File zipFile, Path outputPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newPath = zipSlipProtect(zipEntry, outputPath);
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }

    private Path zipSlipProtect(ZipEntry zipEntry, Path outputPath) throws IOException {
        Path targetDirResolved = outputPath.resolve(zipEntry.getName());
        Path normalizedPath = targetDirResolved.normalize();
        if (!normalizedPath.startsWith(outputPath)) {
            throw new IOException("Bad zip entry");
        }
        return normalizedPath;
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            lblStatus.setText("Error copying files: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new InstallerGUI().setVisible(true);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
