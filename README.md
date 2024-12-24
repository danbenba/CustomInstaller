# Custom Installer – Version Preview

Ce projet est une application Java (basée sur **Swing**) permettant d’effectuer l’installation d’un logiciel de manière automatisée et avancée. Il inclut :

- Un écran de chargement initial (géré par `CustomInstaller.java`).
- Une interface principale d’installation (gérée par `InstallerGUI.java`).
- Un mécanisme de téléchargement, de décompression, de vérifications système (version Java, espace disque, présence d’Internet, etc.).
- La possibilité de gérer un fichier de configuration `config.properties` pour modifier le comportement de l’installeur sans recompilation.
- La création optionnelle de raccourcis sur Windows, ainsi qu’un désinstalleur.
- La prise en charge (optionnelle) du téléchargement résumable.

## Table des matières

1. [Aperçu du projet](#aperçu-du-projet)  
2. [Prérequis](#prérequis)  
3. [Installation et Compilation](#installation-et-compilation)  
4. [Configuration](#configuration)  
5. [Utilisation](#utilisation)  
6. [Structure du projet](#structure-du-projet)  
7. [Guide Git (basique)](#guide-git-basique)  
8. [Contribuer](#contribuer)  
9. [Licence](#licence)  

---

## Aperçu du projet

### Flux d’exécution

1. **CustomInstaller.java** : Affiche un mini-écran de chargement (barre de progression avec messages dynamiques).  
   - Lorsque le chargement atteint 100 %, la fenêtre se ferme et lance la classe `InstallerGUI.java`.

2. **InstallerGUI.java** : Interface principale d’installation proposant :  
   - Le choix du chemin d’installation (utilisateur ou administrateur).  
   - L’affichage d’une bannière personnalisée (téléchargée depuis une URL définie dans `config.properties`).  
   - Les contrôles d’installation (chemin, démarrage, barre de progression, CGU, etc.).  
   - Le téléchargement et la décompression du contenu.  
   - Des options de configuration avancées (activables/désactivables dans `config.properties`).  
   - La possibilité de créer un désinstalleur et des raccourcis Windows.  
   - La vérification de la connectivité internet et de la version de Java requise.

### Fonctionnalités principales

- **Gestion de configuration via `config.properties`** :  
  - Personnalisation du titre, icône, URL de téléchargement, version minimale de Java, etc.
- **Logs** (optionnels) :  
  - Génération d’un fichier `log` (ou non) selon vos besoins.
- **Téléchargement résumable** (optionnel) :  
  - Reprise du téléchargement là où il s’était arrêté.
- **Vérifications système** :  
  - Espace disque minimum requis.  
  - Présence d’une connexion internet.  
  - Lancement en administrateur (pour Windows).
- **Création d’un désinstalleur** (optionnel).
- **Création de raccourcis Windows** (optionnel).
- **Proposition de lancement de l’application** après l’installation.

---

## Prérequis

- **Java 8** ou version supérieure (un **JDK** est recommandé pour compiler, mais un **JRE** suffit pour exécuter l’exécutable une fois compilé).
- **Git** (fortement recommandé) si vous souhaitez cloner le dépôt et bénéficier du versionnement.
- **Un IDE Java** (Eclipse, IntelliJ, NetBeans, etc.) **ou** l’outil en ligne de commande `javac` pour compiler les sources.

---

## Installation et Compilation

### 1. Cloner le dépôt

```bash
# Via HTTPS
git clone https://github.com/<votre-nom-utilisateur>/CustomInstaller.git

# OU via SSH
git clone git@github.com:<votre-nom-utilisateur>/CustomInstaller.git
```

Remplacez `<votre-nom-utilisateur>` par votre pseudo ou le nom du dépôt GitHub cible.

### 2. Importer dans votre IDE (facultatif)

1. Ouvrez votre IDE.
2. Sélectionnez **Import Project**.
3. Choisissez le dossier `CustomInstaller` que vous venez de cloner.
4. Importez-le comme un projet Java (ou Maven / Gradle si vous l’avez configuré).

### 3. Compiler en ligne de commande (optionnel)

1. Depuis la racine du projet, ouvrez un terminal.
2. Compilez le code dans un répertoire de sortie `out` :

   ```bash
   javac -d out src/fr/danbenba/custominstaller/*.java
   ```
   (Vous pouvez également compiler `InstallerGUI.java` si nécessaire.)

3. Exécutez la classe principale :

   ```bash
   java -cp out fr.danbenba.custominstaller.CustomInstaller
   ```
   (ou `java -cp out fr.danbenba.custominstaller.InstallerGUI` si vous voulez lancer directement l’interface complète.)

---

## Configuration

Le fichier de configuration `config.properties` (placé dans le **même package** que les classes Java) gère toutes les options de l’installeur. Voici un exemple de contenu :

```properties
# ----------------------------------------------------------------------
# Fichier de configuration pour Custom Installer (v2.0pre2)
# ----------------------------------------------------------------------

##################################################
# Configuration générale de l'installateur
##################################################

app.title=Custom Installer (Preview Version)
app.iconPath=/icon.png
app.mainImage=https://raw.githubusercontent.com/danbenba/CustomInstaller/project/webFiles/CustomInstallerBanner.png

##################################################
# URLs pour les ressources de l'installateur
##################################################

version.url=https://raw.githubusercontent.com/danbenba/CustomInstaller/project/webFiles/appversion
terms.url=https://raw.githubusercontent.com/danbenba/CustomInstaller/project/webFiles/terms
download.url=https://github.com/danbenba/CustomInstaller/raw/project/webFiles/CustomInstaller.zip
link.name=Version 2.0 Preview
link.url=https://github.com/danbenba/CustomInstaller

##################################################
# Chemins d'installation
##################################################

dir.user=\\CustomInstaller
dir.progfile=C:\\CustomInstaller

##################################################
# Raccourci
##################################################

shortcut.enabled=true
shortcut.name=Custom Installer
shortcut.target=CustomInstaller.txt

# ----------------------------------------------------------------------
# Gestion de l'installation
# ----------------------------------------------------------------------

java.minVersion=23
installer.minSpace=200000000

logging.path=${user.home}/custom-installer_log.txt
logging.enabled=true

alreadyInstalledCheck.file=CustomInstaller.txt
alreadyInstalledCheck.enabled=true

advanced.enabled=false
uninstaller.enabled=true
launch.prompt=true
internetCheck.enabled=true
download.resumable=true
```

> **Remarque** : Vous pouvez adapter ces valeurs selon vos besoins (URL de téléchargement, version Java minimale, etc.).

---

## Utilisation

1. **Lancement du mini-installateur (loading)** :  
   - Classe : `CustomInstaller.java`  
   - Une fenêtre de 320×120 px s’ouvre avec une **barre de progression pseudo-aléatoire**.  
   - Une fois la barre à 100 %, la fenêtre se ferme automatiquement et lance `InstallerGUI.java`.

2. **Interface d’installation** :  
   - Classe : `InstallerGUI.java`  
   - Vous choisissez le type d’installation (utilisateur ou administrateur).  
   - Une bannière (image) se télécharge et s’affiche.  
   - Vous validez ou non les CGU (téléchargées depuis l’URL définie) avant de lancer le téléchargement.  
   - Le téléchargement des fichiers s’effectue (en résumable ou non).  
   - Les fichiers sont décompressés (`unzip`) puis copiés vers le dossier d’installation.  
   - (Optionnel) Un désinstalleur est généré.  
   - (Optionnel) Des raccourcis (Bureau, Menu Démarrer) sont créés.  
   - (Optionnel) L’installeur propose de lancer immédiatement l’application fraîchement installée.

3. **Si vous avez activé l’option de logs** (`logging.enabled=true` dans `config.properties`), un fichier de log sera généré à l’emplacement choisi (`logging.path`).

4. **Pour quitter** :  
   - Soit à l’aide du bouton *Close* (dans l’interface).  
   - Soit via la croix en haut à droite de la fenêtre.  
   - Ou en tapant `Ctrl + C` dans le terminal (si vous l’avez lancé depuis un terminal).

---

## Structure du projet

```
CustomInstaller/
├── src/
│   └── fr/
│       └── danbenba/
│           └── custominstaller/
│               ├── CustomInstaller.java
│               ├── InstallerGUI.java
│               └── config.properties
├── README.md
└── .gitignore (optionnel)
```

- **CustomInstaller.java**  
  - Lance un écran de chargement (progress bar) qui, en fin de progression, démarre `InstallerGUI.java`.
- **InstallerGUI.java**  
  - Gère l’intégralité du processus d’installation décrit plus haut.
- **config.properties**  
  - Fichier de configuration (titre de l’app, URL de téléchargement, logs, etc.).

---

## Guide Git (basique)

Si vous ne maîtrisez pas Git, voici un mini-récapitulatif :

1. **Initialiser un dépôt** (si vous n’en avez pas encore un) :
   ```bash
   git init
   ```
2. **Ajouter des fichiers** :
   ```bash
   git add <fichier_ou_dossier>
   # ou pour tout ajouter
   git add .
   ```
3. **Valider (commit) les changements** :
   ```bash
   git commit -m "Mon premier commit"
   ```
4. **Associer le dépôt local à GitHub** :
   ```bash
   git remote add origin https://github.com/<votre-nom-utilisateur>/CustomInstaller.git
   ```
5. **Pousser (push) les commits vers GitHub** :
   ```bash
   git push -u origin master
   ```
6. **Récupérer (pull) les dernières modifications** :
   ```bash
   git pull
   ```

Pour plus de détails, voir la [documentation Git officielle](https://git-scm.com/doc).

---

## Contribuer

1. **Forkez** le projet sur GitHub.  
2. **Clonez** votre fork en local :  
   ```bash
   git clone https://github.com/<votre-nom-utilisateur>/CustomInstaller.git
   ```
3. Créez une **branche** pour votre nouvelle fonctionnalité ou correctif :  
   ```bash
   git checkout -b feature/ma-nouvelle-fonction
   ```
4. Modifiez le code et **committez** :
   ```bash
   git commit -m "Ajout de la nouvelle fonctionnalité"
   ```
5. **Poussez** la branche sur GitHub :
   ```bash
   git push origin feature/ma-nouvelle-fonction
   ```
6. Créez une **Pull Request** sur le dépôt principal, en expliquant vos modifications.

---

## Licence

Ce projet est distribué sous licence **MIT**. Vous êtes libre de l’utiliser, le modifier et le redistribuer. Veuillez consulter le fichier `LICENSE` (s’il est fourni) ou créer un nouveau fichier `LICENSE` si vous souhaitez définir précisément les conditions d’utilisation.  

**Merci d’utiliser Custom Installer !**  
*N’hésitez pas à ouvrir une *issue* ou une *pull request* si vous rencontrez un souci ou si vous souhaitez proposer des améliorations.*
