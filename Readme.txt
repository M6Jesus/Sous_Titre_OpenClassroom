Projet subtitlor Java EE - OpenClassRooms

Fonctionnalités :

- Utilisation du pattern DAO (avec deux implémentations fonctionnelles : MySql et la gestion de fichier .srt directement)
- Enregistrement des fichiers art en base de données ainsi que leur traduction (le fichier joint subtitlor.sql contient la base initiale qui doit être utilisée par l'utilisateur "opcr" ayant pour mot de passe "opcr")
- Export de fichiers .srt
- Upload de nouveau fichier à traduire
- Sélection d'un fichier à traduire ou chargement du dernier fichier édité

- Message de warning lors d'un changement de page sans sauvegarder les modifications
- Interface responsive design avec l'aide de Bootstrap
- Interface permettant de sélection d'un nombre de séquences à traduire par page pour éviter les pages trop longues
- Début de gestion des langages (avec l'enum SupportedLanguage, pour le moment, seule la traduction .trad.srt est différenciée du fichier original)
- Javadoc partiel, essentiellement sur les méthodes des servlets


Composition du projet : 

- vues : 
  - select.jsp : page d'accueil avec une liste déroulant pour le choix du fichier et un formalaire d'upload de fichier.
  - edit_subtitle.jsp : page d'édition des sous-titres contenant un formulaire pour l'envoi du texte. Le changement de page et du nombre de séquence par page est géré en javascript.

- servlets :
  - Select : accueil, sert à la sélection du fichier à traduire ou à son upload.
  - EditSubtitle : page de traduction, les fichiers de sous-titres sont décomposés par "séquences" et chaque page de traduction affiche un nombre de séquences par page.
  - Download : ce servlet est appelé lors du clic sur un lien de téléchargement.

- beans :
  - SrtFile : descrit un fichier de sous-titre et contient son nom, son langage ainsi que la liste des sous-titres regroupés en séquences.
  - Sequence : description d'une séquence de sous-titre, contient l'index du sous-titre, les temps d'apparition et la liste des lignes de sous-titres.

- interfaces DAO : 
  - SequenceDAO : lié à une référence de fichier srt, il permet de gérer les opérations nécessaire sur les séquences : lister les séquences, mettre à jour une séquence, retrouver une séquence à l'aide de son index, restituer le bean du SrtFile lié, donner le nombre de séquence total...
  - SrtFileDAO : permet de gérer les opérations nécessaires sur les fichiers srt : donner la liste des fichiers référencés, ajouter un fichier, mettre à jour un fichier, supprimer un fichier, créer une copie vide d'un fichier (la traduction initiale).

- deux implémentations fonctionnelles des interfaces DAO :
  - dao.mysql : implémentation en base de donnée MySQL
  - dao.file : implémentation à l'aide de fichiers. Cette implémentation n'est que partiellement utilisée, pour générer les fichiers à télécharger.

- utilities :
  - ContextHandler : permet de retrouver facilement le context de l'application.
  - FileManager : méthodes statiques pour faciliter l'implémentation de dao.file et la gestion des fichiers.
  - Key : déclaration centralisé de toutes les clés des paramètres et attributs.
  - SupportedLanguage : enum de déclaration des langages supportés.
 