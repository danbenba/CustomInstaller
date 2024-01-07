# CustomInstaller

Ce document fournit des instructions sur l'installation et l'utilisation de CustomInstaller, un programme d'installation personnalisé pour les applications Java.

## Prérequis

- Java Development Kit [(JDK) 18](https://download.oracle.com/java/18/archive/jdk-18.0.2.1_windows-x64_bin.exe ) ou supérieur.
- Un environnement de développement intégré (IDE) comme IntelliJ IDEA, Eclipse, ou NetBeans.

## Installation des Sources

1. **Cloner le dépôt**:
   
   Ouvrez un terminal et exécutez la commande suivante pour cloner le dépôt :

   ```bash
   git clone https://github.com/danbenba/CustomInstaller
   ```

2. **Ouvrir le projet dans l'IDE**:
   
   - Ouvrez votre IDE.
   - Sélectionnez `File > Open...` et naviguez jusqu'au dossier du projet cloné.
   - Ouvrez le projet. L'IDE devrait automatiquement détecter les fichiers de configuration et les charger.

3. **Configurer l'environnement de développement**:
   
   Assurez-vous que le JDK est correctement configuré dans votre IDE pour utiliser Java 8 ou une version ultérieure.

## Compilation du Projet

Dans votre IDE, exécutez la commande de compilation pour générer le fichier `.jar`:

- **IntelliJ IDEA**: `Build > Build Artifacts... > Build`.
- **Eclipse**: `Project > Build Project`.
- **NetBeans**: `Run > Clean and Build Project`.

Le fichier `.jar` se trouve généralement dans le dossier `out/artifacts` ou `target` du projet.

## Exécution du Fichier .jar

Pour exécuter le fichier `.jar`, ouvrez un terminal et exécutez la commande suivante :

```bash
java -jar chemin/vers/CustomInstaller.jar
```

Remplacez `chemin/vers/CustomInstaller.jar` par le chemin réel vers le fichier `.jar` généré.

## Utilisation

Après avoir lancé l'application, suivez les instructions à l'écran pour installer votre logiciel. Le programme vous demandera de choisir un emplacement d'installation et affichera la progression de l'installation.
