import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;

/**
 * Cr�ation de la classe Upload qui h�rite de la classe AbstractAction du package javax.swing et qui va permettre d'effectuer
 * des mises � jour sur un fichier.
 * @author Jonathan, Fr�d�ric, Anthony et M�lanie
 */

public class Upload extends AbstractAction {
	String namefile = null ;
	
	/**
	 * Mise � jour
	 * @param texte qui est le fichier texte � mettre � jour
	 */
	
	public Upload(String texte){
		super(texte);
	}
	
	/**
	 * M�thode permettant de savoir si l'�v�nement demand� a bien �t� effectu�.
	 */
	
	public void actionPerformed(ActionEvent e) { 
		IntHumMach.affichetext();
		namefile=IntHumMach.text;
		System.out.println("le nom du fichier est : " + namefile);
		File fi = new File(namefile);
		File fout= new File("server/"+fi.getName());
		try {
			fout.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ishere(fi);
		copyFile(fi,fout);
	} 
	
	/**
	 * M�thode permettant d'obtenir le lien du fichier mis � jour.
	 * @return namefile qui est le chemin du fichier mis � jour
	 */
	
	public String getLink(){
		return namefile;
	}
	
	/**
	 * M�thode permettant de savoir si le fichier mis � jour existe ou non.
	 * @param file est le fichier dont on veut conna�tre l'existence ou non
	 * @return true si le fichier existe
	 * @return false si le fichier n'existe pas
	 */
	
	public boolean ishere(File file){
		File f =new File("client/" + file.getName());
		if(f.exists()){
			System.out.println("exist");
			return true;
		}else {
			System.out.println("doesn't exist");
			return false;
		}
	}
	
	/**
	 * M�thode permettant de copier un fichier source mis � jour du server vers un fichier destination c'est-�-dire vers 
	 * un client.
	 * @param source1 est le fichier provenant du server et � copier 
	 * @param dest1 est le fichier copi� dans le client
	 */
	
	public  void copyFile(File source1, File dest1){
		if(ishere(source1)){
			File dest =new File("server/" + dest1.getName());
			File source =new File("client/" + source1.getName());
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
