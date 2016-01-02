import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * Cr�ation de la classe IntHumMach qui h�rite de la classe JFrame du package javax.swing qui va permettre de faire
 * int�ragir l'humain avec la machine gr�ce � une interface.
 * @author Jonathan, Fr�d�ric, Anthony et M�lanie
 */

public class IntHumMach extends JFrame{
	static String text;
	static JTextField textField = new JTextField();
	
	/**
	 * Interface Homme-Machine
	 */
	
	public IntHumMach(){
		super();
		build(); //On initialise notre fen�tre
	}
	
	/**
	 * M�thode priv�e permettant de cr�er une fen�tre qui servira, par la suite, d'interface entre l'homme et la 
	 * machine. Elle poss�de des m�thodes permettant d'affecter un titre ou une taille � la fen�tre, de centrer 
	 * cette derni�re sur l'�cran, de la redimensionner, de la fermer ou encore de modifier son contenu.
	 */
	
	private void build(){
		setTitle("Cryptographie : https"); 
		setSize(600,100); //On donne une taille � notre fen�tre
		setLocationRelativeTo(null); //On centre la fen�tre sur l'�cran
		setResizable(true); //On permet le redimensionnement
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit � l'application de se fermer lors du clic sur la croix
		setContentPane(buildContentPane());
	}
	
	/**
	 * M�thode priv�e permettant de cr�er ou modifier le contenu de la fen�tre c'est-�-dire un panneau, un bouton de 
	 * t�l�chargement, un bouton de mise � jour ou encore une possibilit� de redimensionnement.
	 * @return panel qui est le contenu de la fen�tre pr�c�demment cr��e
	 */
	
	private JPanel buildContentPane(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton download = new JButton(new Download("Download"));
		panel.add(download);
		
		JButton upload = new JButton(new Upload("Upload"));
		panel.add(upload);
		
		textField.setPreferredSize( new Dimension(100,30));
		panel.add(textField);
		return panel;
	}
	
	/**
	 * M�thode statique permettant d'afficher le chemin du fichier � traiter.
	 */
	
	public static void affichetext(){
		text=textField.getText();
	}
	
	/**
	 * Main permettant de cr�er une nouvelle instance de fen�tre et de la rendre visible
	 * @param args
	 */
	
	public static void main(String[] args) {
		//On cr�e une nouvelle instance de notre FenetreBoutons
		IntHumMach fenetre = new IntHumMach();
		fenetre.setVisible(true); //On la rend visible
	}
}