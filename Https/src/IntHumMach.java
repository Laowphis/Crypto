

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

public class IntHumMach extends JFrame{
	static String text;
	static JTextField textField = new JTextField();
	
	public IntHumMach(){
		super();
		
		build();//On initialise notre fen�tre
	}
	
	private void build(){
		setTitle("crypto https"); 
		setSize(600,100); //On donne une taille � notre fen�tre
		setLocationRelativeTo(null); //On centre la fen�tre sur l'�cran
		setResizable(true); //On permet le redimensionnement
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit � l'application de se fermer lors du clic sur la croix
		setContentPane(buildContentPane());
	}
	
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
	
	public static void affichetext(){
		text=textField.getText();
	}
	
	
	public static void main(String[] args) {
		//On cr�e une nouvelle instance de notre FenetreBoutons
		IntHumMach fenetre = new IntHumMach();
		fenetre.setVisible(true);//On la rend visible
	}
}