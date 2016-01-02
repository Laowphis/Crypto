import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * Création de la classe IntHumMach qui hérite de la classe JFrame du package javax.swing qui va permettre de faire
 * intéragir l'humain avec la machine grâce à une interface.
 * @author Jonathan, Frédéric, Anthony et Mélanie
 */

public class IntHumMach extends JFrame{
	static String text;
	static JTextField textField = new JTextField();
	
	/**
	 * Interface Homme-Machine
	 */
	
	public IntHumMach(){
		super();
		build(); //On initialise notre fenêtre
	}
	
	/**
	 * Méthode privée permettant de créer une fenêtre qui servira, par la suite, d'interface entre l'homme et la 
	 * machine. Elle possède des méthodes permettant d'affecter un titre ou une taille à la fenêtre, de centrer 
	 * cette dernière sur l'écran, de la redimensionner, de la fermer ou encore de modifier son contenu.
	 */
	
	private void build(){
		setTitle("Cryptographie : https"); 
		setSize(600,100); //On donne une taille à notre fenêtre
		setLocationRelativeTo(null); //On centre la fenêtre sur l'écran
		setResizable(true); //On permet le redimensionnement
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit à l'application de se fermer lors du clic sur la croix
		setContentPane(buildContentPane());
	}
	
	/**
	 * Méthode privée permettant de créer ou modifier le contenu de la fenêtre c'est-à-dire un panneau, un bouton de 
	 * téléchargement, un bouton de mise à jour ou encore une possibilité de redimensionnement.
	 * @return panel qui est le contenu de la fenêtre précédemment créée
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
	 * Méthode statique permettant d'afficher le chemin du fichier à traiter.
	 */
	
	public static void affichetext(){
		text=textField.getText();
	}
	
	/**
	 * Main permettant de créer une nouvelle instance de fenêtre et de la rendre visible
	 * @param args
	 */
	
	public static void main(String[] args) {
		//On crée une nouvelle instance de notre FenetreBoutons
		IntHumMach fenetre = new IntHumMach();
		fenetre.setVisible(true); //On la rend visible
	}
}