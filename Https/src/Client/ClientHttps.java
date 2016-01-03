package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;



/**
 * Un client lecteur du contenu d'une URL selon le protocole HTTPS
 * muni d'un vérificateur d'identité de l'hote
 * @author P. Guichet
 */
public class ClientHttps {

	
	//static String httpsUrl = "https://www.google.fr";
	static String httpsUrl = "https://localhost:1337/file";
	static URL url;
	static HttpsURLConnection connection=null;
	
    /**
     * Lancement du client
     * @param args args les paramètres passés en ligne de commande
     * @throws java.lang.Exception Si la construction de l'URL ou si la connexion échoue
     */
    public static void main(String[] args) throws Exception {
        ///////////////////////////////////////////////////////////////////////
        // Propriété nécessaires à l'établissement d'une connection SSL
        // comme aucun truststore n'est spécifié du coté du client
        // c'est le keystore JRE_HOME/lib/security/cacerts, livré avec 
        // l'environnement d'exécution Java, qui sera utilisé
        // Propriété définissant le niveau de trace du débogage de la session
        
    	 {
            //System.setProperty("javax.net.ssl.keyStore", "kssrv.ks");
            //System.setProperty("javax.net.ssl.keyStorePassword", "x4TRDf4JHY578pth");
            //System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
    		//System.setProperty("javax.net.debug", "all");
        }
    	
    	System.setProperty("javax.net.debug", "ssl");
        // Construction de l'URL de la ressouce à récupérer
        // par exemple "https://www.verisign.com"
        //URL url = new URL(args[0]);
        //URL url = new URL("https://localhost:1337/file");
        url = new URL(httpsUrl);
        // Ouverture de la connexion sur cette URL
        // Le type déclaré renvoyé par l'invocation de openConnection() est 
        // URLConnection mais son type réel sera HttpsURLConnection
        connection = (HttpsURLConnection) url.openConnection();
        
        //InstallCert ic= new InstallCert();
        //String[] arg;
        //arg[0]="toto";
       
        //InstallCert.main(null);
        System.out.println("######{{{{{{{{{{{}}}}}}}}}########");
        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        System.out.println("[[[(((([])))))]]]");
        System.out.println(System.getProperty("javax.net.ssl.keyStore"));
        
        // La connection est munie d'un vérificateur de nom d'hôte
        connection.setHostnameVerifier(new HostnameVerifier() {
            /**
             * Callback invoqué automatiquement si les informations identifiant
             * le serveur, contenues sur le certificat ne correspondent pas
             * au nom d'hôte extrait de l'URL
             */
            public boolean verify(String string, SSLSession sSLSession) {
                try {
                    // pour interroger interactivement l'utilisateur
                    // du client depuis l'entrée standard.
                    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("***!! certificat suspect !!***");
                    // récupération du sujet (description au format X500) lié au serveur
                    X500Principal principal = (X500Principal) connection.getPeerPrincipal();
                    System.out.println("nom serveur: " + principal.getName());
                    System.out.println("ACCEPTER? [o/n]");
                    String rep = console.readLine();
                    if (rep.equalsIgnoreCase("o"))
                    {
                        // provoquera l'acceptation du serveur
                        return true;
                    } else
                    {
                        // provoquera le rejet du serveur
                        return false;
                    }
                } catch (IOException ioe) {
                    // Problème lors de saisie de la réponse du user
                    // dans le doute le certificat sera rejeté
                    return false;
                }
            }
        });
        
        //////////
        
        /////////
        
        // Création d'un flot entrant associé au serveur
        /*
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        // lecture du contenu de la ressource
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        // fermeture de la connexion
        }
        
        in.close();
        */
        sendGet();
    }
    
 // HTTP GET request
 	private static void sendGet() throws Exception {

 		System.out.println("##################################################");
 		System.out.println("envoie requete");
 		System.out.println();
 		System.out.println("################################################");
 		
 		//String newurl = "https://www.google.fr/webhp?hl=fr#hl=fr&q=mkyong";
 		String newurl = "https://localhost:1337/file?download=dwarf.jpg";//+namefile;
 		
 		java.net.URL url = new URL(null, newurl,new sun.net.www.protocol.https.Handler());
 		
 		//url = new URL(newurl);
 		connection = (HttpsURLConnection) url.openConnection();

 		// optional default is GET
 		connection.setRequestMethod("GET");

 		//add request header
 		connection.setRequestProperty("download", "dwarf.jpg");

 		int responseCode = connection.getResponseCode();
 		System.out.println("\nSending 'GET' request to URL : " + url);
 		System.out.println("Response Code : " + responseCode);

 		/*
 		BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
 		String inputLine;
 		StringBuffer response = new StringBuffer();

 		while ((inputLine = in.readLine()) != null) {
 			response.append(inputLine);
 		}
 		in.close();
		*/

 		//print result
 		//System.out.println(response.toString());

 	}
 	
 	
}
