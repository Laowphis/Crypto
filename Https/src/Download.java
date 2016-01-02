import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.AbstractAction;

/**
 * Création de la classe Download qui hérite de la classe AbstractAction du package javax.swing pour réaliser 
 * le téléchargement d'un fichier texte puis réaliser des opérations dessus.
 * @author Jonathan, Frédéric, Anthony et Mélanie
 *
 */

public class Download extends AbstractAction {
	String namefile = null;
	
	/**
	 * Téléchargement du fichier texte.
	 * @param texte est le texte à télécharger.
	 */
	public Download(String texte){
		super(texte);
	}
	
	/**
	 * Méthode permettant de savoir si l'évènement demandé a bien été effectué.
	 * @param e est un évènement qui indique qu'une action de composant définie est produite.
	 */
	
	public void actionPerformed(ActionEvent e) { 
		
		IntHumMach.affichetext();
		namefile=IntHumMach.text;
		System.out.println("le nom du fichier est : " + namefile);
		File fi = new File(namefile);
		File fout= new File("client/"+fi.getName());
		try {
			fout.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ishere(fi);
		copyFile(fi,fout);
	} 
	
	/**
	 * Méthode permettant d'obtenir le lien du fichier téléchargé.
	 * @return namefile qui est le chemin du fichier téléchargé
	 */
	
	public String getLink(){
		return namefile;
	}
	
	/**
	 * Méthode permettant de savoir si le fichier existe ou non.
	 * @param file est le fichier dont on veut connaître l'existence ou non
	 * @return true si le fichier existe
	 * @return false si le fichier n'existe pas
	 */
	
	public boolean ishere(File file){
		File f =new File("server/" + file.getName());
		if(f.exists()){
			System.out.println("exist");
			return true;
		}else {
			System.out.println("doesn't exist");
			return false;
		}
	}

	/**
	 * Méthode permettant de copier un fichier source donc du server vers un fichier destination donc vers un client
	 * @param source1 est le fichier provenant du server et à copier 
	 * @param dest1 est le fichier copié dans le client
	 */
	
	public  void copyFile(File source1, File dest1){
		if(ishere(source1)){
			File source =new File("server/" + source1.getName());
			File dest =new File("client/" + dest1.getName());
		try{
			// Declaration et ouverture des flux
			java.io.FileInputStream sourceFile = new java.io.FileInputStream(source);
			try{
				java.io.FileOutputStream destinationFile = null;
			 		try{
						destinationFile = new FileOutputStream(dest);
						// Lecture par segment de 0.5Mo 
						byte buffer[] = new byte[512 * 1024];
						int nbLecture;
						while ((nbLecture = sourceFile.read(buffer)) != -1){
							destinationFile.write(buffer, 0, nbLecture);
						}
					} finally {
						destinationFile.close();
					}
			} finally {
				sourceFile.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		}
	}
}
