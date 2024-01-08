package fr.danbenba.CustomInstaller;
import javax.swing.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.Timer;
import java.util.zip.*;

@SuppressWarnings("serial")
public class CustomInstallerGUI extends JFrame {

    private JTextField txtEmplacement;
    private JButton btnInstaller;
    Timer timer = new Timer();
    private JButton btnClose;
    private JProgressBar progressBar;
    private JLabel lblStatus;
	private JLabel lblGitHubLink;
    private JLabel lblFooter; // Déclaration de lblFooter

    public CustomInstallerGUI() {
    	// Configuration du Look and Feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("CustomInstaller");
        setSize(400, 200);
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png")); // Replace 'your_icon.png' with your icon's filename
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);


        // Demande à l'utilisateur pour le chemin d'installation
        int response = JOptionPane.showConfirmDialog(null, "Voulez-vous installer dans le répertoire utilisateur ?", "Emplacement d'installation", JOptionPane.YES_NO_OPTION);
        String emplacement;
        if (response == JOptionPane.YES_OPTION) {
            // Chemin du répertoire utilisateur
            emplacement = System.getProperty("user.home") + "\\CustomInstaller";
        } else {
            // Chemin par défaut pour l'installation locale
            emplacement = "C:\\Program Files\\CustomInstaller";
            if (response == JOptionPane.NO_OPTION && !isRunAsAdmin()) {
                JOptionPane.showMessageDialog(this, "Veuillez éxécuter le programme en tant qu'administrateur.", "Privileges d'administrateur requis", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        
        // Emplacement input
        txtEmplacement = new JTextField(emplacement);
        txtEmplacement.setBounds(10, 10, 370, 25);
        add(txtEmplacement);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(10, 40, 370, 25);
        progressBar.setVisible(false);
        add(progressBar);

        // Bouton Installer
        btnInstaller = new JButton("Installer");
        btnInstaller.setBounds(10, 70, 100, 25);
        btnInstaller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                installer();
            }
        });
        add(btnInstaller);

        // Bouton Close
        btnClose = new JButton("Close");
        btnClose.setBounds(120, 70, 100, 25);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(btnClose);

        lblGitHubLink = new JLabel("<html><a href=''>GitHub Official</a></html>");
        lblGitHubLink.setBounds(280, 100, 370, 25);
        lblGitHubLink.setForeground(Color.BLUE);
        lblGitHubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGitHubLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/danbenba"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    // Gestion de l'exception IOException
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                    // Gestion de l'exception URISyntaxException
                }
            }
        });
        
        add(lblGitHubLink);

        
        // Label Footer
        lblFooter = new JLabel("---- Created by CustomInstaller ----");
        lblFooter.setBounds(10, 130, 370, 25);
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblFooter);
        
        // Status label
        lblStatus = new JLabel("");
        lblStatus.setBounds(10, 100, 370, 25);
        add(lblStatus);
        
    }
    
    private boolean isRunAsAdmin() {
        File testPrivilege = new File("C:\\testPrivilege.txt");
        try {
            if (testPrivilege.createNewFile()) {
                testPrivilege.delete();
                return true;
            }
        } catch (IOException e) {
            // L'exception indique un manque de privilèges
        }
        return false;
    }

    private void installer() {
    	progressBar.setVisible(true);
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
            	publish("Creating files...");
            	progressBar.setValue(10);
            	Thread.sleep(0500);
                Path tempDir = Files.createTempDirectory("CustomInstaller");
                progressBar.setValue(30);
                File zipFile = new File(getClass().getResource("/CustomInstaller.zip").toURI());
                publish("Unziping...");
                Thread.sleep(0500);
            	progressBar.setValue(60);
                unzip(zipFile, tempDir);

                Path targetDir = Paths.get(txtEmplacement.getText());
                Files.walk(tempDir)
                    .forEach(source -> {
                    	progressBar.setValue(70);
                        publish("Extracting " + source.getFileName());
                        try {
							Thread.sleep(0030);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        copy(source, targetDir.resolve(tempDir.relativize(source)));
                        progressBar.setValue(90);
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    });

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String status : chunks) {
                    lblStatus.setText(status);
                }
            }

            @Override
            protected void done() {
                progressBar.setValue(100);
                lblStatus.setText("Installation finished");
                btnInstaller.setEnabled(false);
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
                new CustomInstallerGUI().setVisible(true);
            }
        });
    }
}
