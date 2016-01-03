package Serveur;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;

/**
 * Classe pr�sentant des m�thodes permettant la manipulation ais�e des keystores.
 * @author Julien Lepagnot
 */
public class KeyStoreServ {

    // Le keystore de l'instance
    private KeyStore ks;

    // Associations des OID pouvant appara�tre dans un nom distingu�
    // � compl�ter avec les OID de p�fixe 1.2.840.113549.1.9
    private static final Map<String, String> OID_MAP = new HashMap<>();
    static {
        OID_MAP.put("1.2.840.113549.1.9.1", "emailAddress");
        OID_MAP.put("1.2.840.113549.1.9.2", "unstructuredName");
        OID_MAP.put("1.2.840.113549.1.9.8", "unstructuredAddress");
        OID_MAP.put("1.2.840.113549.1.9.16", "S/MIME Object Identifier Registry");
    }

    /**
     * Construction d'une instance de la classe.
     * @param type Le type du keystore
     * @param file Le fichier contenant le keystore
     * @param passwd Le mot de passe du keystore
     */
    public KeyStoreServ(String type, File file, char[] passwd)
            throws GeneralSecurityException, IOException {
        // Construction d'une instance d'un keystore de type type
        ks = KeyStore.getInstance(type);
        // Initialisation du keystore avec le contenu du fichier file
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        ks.load(is, passwd);
    }

    /**
     * Construction d'une instance de la classe.
     * @param type Le type du keystore
     * @param file Le nom du fichier contenant le keystore
     * @param passwd Le mot de passe du keystore
     */
    public KeyStoreServ(String type, String fileName, char[] passwd)
            throws GeneralSecurityException, IOException {
        this(type, new File(fileName), passwd);
    }

    /**
     * Renvoie un String donnant une description de la cl� publique key selon
     * un format d�pendant de son algorithme (RSA ou DSA)
     * @param key Une cl� publique
     */
    public static String toString(PublicKey key) {
        StringBuilder sb = new StringBuilder();
        // Pr�sente le type de cl�
        sb.append("Cl� publique de type : ").append(key.getAlgorithm()).append('\n');
        if (key instanceof RSAPublicKey) {
            // Cas d'une cl� RSA
            RSAPublicKey rsaPk = (RSAPublicKey)key;
            sb.append("Module de chiffrement :\n");
            sb.append(rsaPk.getModulus()).append('\n');
            sb.append("Exposant public :\n");
            sb.append(rsaPk.getPublicExponent()).append('\n');
        } else if (key instanceof DSAPublicKey) {
            // Cas d'une cl� DSA
            DSAPublicKey dsaPk = (DSAPublicKey)key;
            DSAParams dsaParams = dsaPk.getParams();
            sb.append("Param�tres globaux :\n");
            sb.append("P : ").append(dsaParams.getP()).append('\n');
            sb.append("Q : ").append(dsaParams.getQ()).append('\n');
            sb.append("G : ").append(dsaParams.getG()).append('\n');
            sb.append("Cl� publique :\n");
            sb.append("Y : ").append(dsaPk.getY());
        } else {
            // Cas non pris en charge
            throw new IllegalArgumentException("Cl� de type non trait�");
        }
        // Retourne la chaine construite, d�crivant la cl�
        return sb.toString();
    }

    /**
     * Acc�s au nom X500 du sujet du certificat.
     * @param cert Un certificat X.509
     * @return Le nom au format RFC2253 + traduction des OID du sujet du certificat
     */
    private static String getSubjectName(X509Certificate cert) {
        X500Principal subject = cert.getSubjectX500Principal();
        return subject.getName(X500Principal.RFC2253, OID_MAP);
    }

    /**
     * Acc�s au nom X500 de l'autorit� ayant d�livr� le certificat.
     * @param cert Un certificat X.509
     * @return Le nom au format RFC2253 + traduction des OID de l'autorit� ayant d�livr� le certificat
     */
    private static String getIssuerName(X509Certificate cert) {
        X500Principal issuer = cert.getIssuerX500Principal();
        return issuer.getName(X500Principal.RFC2253, OID_MAP);
    }

    /**
     * Renvoie un String donnant une description d'un certificat X.509.
     * @param cert Un certificat X.509
     */
    public static String toString(X509Certificate cert) {
        StringBuilder sb = new StringBuilder();
        // Pr�sente le sujet du certificat
        sb.append("D�tenteur : ").append(getSubjectName(cert)).append('\n');
        // Pr�sente l'�metteur du certificat
        sb.append("Autorit� de certification : ").append(getIssuerName(cert)).append('\n');
        // Pr�sente la date de d�but de validit�
        sb.append("Valable du ").append(cert.getNotBefore()).append('\n');
        // Pr�sente la date de fin de validit�
        sb.append("Valable jusqu'au ").append(cert.getNotAfter()).append('\n');
        // D�crit la cl� publique contenue dans le certificat
        sb.append(toString(cert.getPublicKey()));
        // Retourne la chaine construite, d�crivant le certificat
        return sb.toString();
    }

    /**
     * Renvoie un String d�crivant la liste des descriptions des certificats
     * contenus dans le keystore de l'instance.
     */
    public String listCertificates()
            throws GeneralSecurityException {
        StringBuilder sb = new StringBuilder();
        // R�cup�re tous les alias identifiant les entr�es du keystore
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            // Teste si l'entr�e nomm�e par l'alias courant est un certificat
            if (ks.isCertificateEntry(alias)) {
                // Si c'est le cas la r�cup�rer
                Certificate cert = ks.getCertificate(alias);
                // Pr�sente l'alias du certificat
                sb.append("Alias : ").append(alias).append('\n');
                if (cert instanceof X509Certificate) {
                    // Cas d'un certificat X.509
                    sb.append(toString((X509Certificate)cert));
                }
                else {
                    // Cas non pris en charge
                    sb.append("Certificat de type non trait�\n");
                }
                sb.append('\n');
            }
        }
        // Retourne la chaine construite, d�crivant les certificats du keystore
        return sb.toString();
    }

    /**
     * Importe dans le keystore le certificat cert sous le nom alias.
     * @param cert Le certificat � ins�rer.
     * @param alias L'alias � associer avec le certificat ins�r�.
     */
    public void importCertificate(Certificate cert, String alias)
            throws GeneralSecurityException {
        // Ins�re le certificat dans le keystore
        ks.setCertificateEntry(alias, cert);
    }

    /**
     * Importe dans le keystore les certificats contenu dans le fichier de chemin
     * file, le i-i�me certificat �tant identifi� par aliases[i-1].
     * @param file Le fichier contenant les certificats � ins�rer.
     * @param aliases Les alias � associer avec les certificats ins�r�s.
     */
    public void importCertificates(File file, String[] aliases)
            throws GeneralSecurityException, IOException {
        // Le flot transmis � la m�thode generateCertificate doit supporter
        // les op�rations mark et reset ce qui est le cas de BufferedInputStream
        // mais pas celui de FileInputStream
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        // L'usine est sp�cialis�e dans le traitement des certificats X509.
        CertificateFactory factory = CertificateFactory.getInstance("X509");
        // Comme les lectures ne sont pas faites explicitement, c'est la m�thode
        // InputStream.available qui permet de savoir si la fin de fichier est atteinte
        for (int i = 0; in.available() > 0; i++) {
            importCertificate(factory.generateCertificate(in), aliases[i]);
        }
    }

    /**
     * Sauvegarde l'�tat courant du keystore manipul� dans le fichier file en le
     * prot�geant avec le mot de passe passwd.
     * @param file Le fichier dans lequel sauvegarder le keystore de l'instance.
     * @param passwd Le mot de passe prot�geant le fichier cr��.
     */
    public void save(File file, char[] passwd)
            throws GeneralSecurityException, IOException {
        // S�rialise le contenu du keystore dans le flot attach� au fichier file
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            ks.store(os, passwd);
        }
    }

    /**
     * D�monstration de la classe.
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Nouvelle instance de la classe initialis�e avec le fichier store.ks
            KeyStoreServ kst = new KeyStoreServ("JCEKS", "store.ks", "azerty".toCharArray());

            // Liste les certificats et cl�s priv�es du keystore
            System.out.println(kst.listCertificates());

            // Ins�re le certificat msca.cer en lui associant l'alias key7
            kst.importCertificates(new File("msca.cer"), new String[]{"key7"});

            // Sauvegarde le keystore dans le fichier kstore.ks avec un nouveau mot de passe
            kst.save(new File("kstore.ks"), "x75DT7Rdx98tdZK".toCharArray());

            // Nouvelle instance de la classe initialis�e avec le fichier kstore.ks
            kst = new KeyStoreServ("JCEKS", "kstore.ks", "x75DT7Rdx98tdZK".toCharArray());

            // Liste les certificats et cl�s priv�es du nouveau keystore
            System.out.println(kst.listCertificates());
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(KeyStoreServ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
