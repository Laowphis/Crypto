

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class Upload extends AbstractAction {
	public Upload(String texte){
		super(texte);
	}
	
	public void actionPerformed(ActionEvent e) { 
		System.out.println("vous voulez upload un fichier");
		IntHumMach.affichetext();
		System.out.println("le nom du fichier est" + IntHumMach.text);
	} 
}
