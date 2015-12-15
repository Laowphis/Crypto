

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.AbstractAction;

public class Download extends AbstractAction {
	String namefile = null;
	public Download(String texte){
		super(texte);
	}
	
	public void actionPerformed(ActionEvent e) { 
		
		IntHumMach.affichetext();
		namefile=IntHumMach.text;
		System.out.println("le nom du fichier est : " + namefile);
		File fi = new File(namefile);
		File fout= new File("client/"+fi.getName());
		try {
			fout.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ishere(fi);
		copyFile(fi,fout);

	} 
	
	public String getLink(){
		return namefile;
	}
	
	
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
