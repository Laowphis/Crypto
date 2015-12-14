

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class Download extends AbstractAction {
	public Download(String texte){
		super(texte);
	}
	
	public void actionPerformed(ActionEvent e) { 
		System.out.println("vous voulez dl un fichier");
		IntHumMach.affichetext();
		System.out.println("le nom du fichier est : " + IntHumMach.text);
	} 
}
