import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import x509.an10.PKIValidator;

/**
 * Une classe impl�mentant une autorit� de certification
 * @author P. Guichet & J. Lepagnot
 */
public class CA {

        // Le DN du CA
        private static final String DN = "CN=RootCA OU=M2 O=miage C=FR";
        // L'alias permettant la r�cup�ration du certificat autosign� du CA
        private static final String ALIAS = "miageCA";
        // Le chemin du fichier contenant le keystore du CA
        private static final String CA_KS_FILE = "ksca.ks";
        // L'OID de l'algorithme SHA-1
        private static final String SHA1_OID = "1.3.14.3.2.26";
        // L'OID de l'algorithme SHA1withRSA
        private static final String SHA1_WITH_RSA_OID = "1.2.840.113549.1.1.5";
        // L'OID de l'extension Basic Constraint
        private static final String BASIC_CONSTRAINT_OID = "2.5.29.19";
        // L'OID de l'extension SubjectKeyIdentifier
        private static final String SUBJECT_KEY_IDENTIFIER_OID = "2.5.29.14";
        // L'OID de l'extension keyUsage
        private static final String KEY_USAGE_OID = "2.5.29.15";
        // L'OID de l'extension extKeyUsage
        private static final String EXT_KEY_USAGE_OID = "2.5.29.37";
        // L'OID de l'extension altName
        private static final String SUBJECT_ALT_NAME_OID = "2.5.29.17";
        // La valeur de l'extension keyUsage pour une autorit� racine
        private static final int CA_KEY_USAGE_VALUE =
            KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.cRLSign | KeyUsage.keyCertSign;
        // La valeur de l'extension keyUsage pour un certificat de serveur
        private static final int SV_KEY_USAGE_VALUE =
            KeyUsage.keyAgreement | KeyUsage.keyEncipherment | KeyUsage.digitalSignature;
        // D�limiteur d�but certificat
        private static final String CERT_BEGIN = "-----BEGIN CERTIFICATE-----\n";
        // D�limiteur fin certificat
        private static final String CERT_END = "\n-----END CERTIFICATE-----";

        // Le certificat du CA
        private Certificate caCert;
        // La cl� priv�e du CA
        private PrivateKey caPk;

        /**
         * Construction d'une instance de la classe
         * @param passwd le mot de passe prot�geant le keystore du CA
         * @throws GeneralSecurityException si la fabrication/r�cup�ration du certificat du CA �choue
         * @throws IOException si une erreur d'entr�e-sortie se produit,
         * par exemple s�rialisation du keystore corrompue
         */
        public CA(char[] passwd) throws GeneralSecurityException, IOException {
                KeyStore ksCa = KeyStore.getInstance("JCEKS");
                File caDir = new File(CA_KS_FILE);
                if (caDir.exists()) {
                        // Le keystore existe d�j� il suffit de le charger
                        ksCa.load(new BufferedInputStream(new FileInputStream(caDir)), passwd);
                        // puis de r�cup�rer le certificat du CA et la cl� priv�e associ�e
                        this.caCert = ksCa.getCertificate(ALIAS);
                        this.caPk = (PrivateKey) ksCa.getKey(ALIAS, passwd);
                } else {
                        // le keystore n'existe pas il faut construire la paire de cl�s publique, priv�e
                        // et empaqueter la cl� publique dans un certificat X509 autosign�
                        installCA(ksCa, passwd, caDir);
                }
        }

        /**
         * M�thode d'aide pour la fabrication d'une CA qui n'existe pas encore 
         * @param ks le keystore du CA
         * @param passwd le mot de passe qui prot�ge le keystore
         * @param caDir le fichier o� sera s�rialis� le keystore
         * @throws GeneralSecurityException si la fabrication/r�cup�ration du certificat du CA �choue
         * @throws IOExceptionsi une erreur d'entr�e-sortie se produit, 
         * par exemple s�rialisation du keystore corrompue
         */
        private void installCA(KeyStore ks, char[] passwd, File caDir)
                throws GeneralSecurityException, IOException {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair caKp = kpg.generateKeyPair();
                this.caPk = caKp.getPrivate();
                X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
                // le num�ro de s�rie de ce certificat
                certGen.setSerialNumber(BigInteger.ONE);
                // le nom de l'�metteur (et du sujet)
                X500Principal caDn = new X500Principal(DN);
                certGen.setIssuerDN(caDn);
                // le nom du sujet
                certGen.setSubjectDN(caDn);
                Calendar calendar = Calendar.getInstance();
                // le d�but de la p�riode de validit�
                Date notBefore = calendar.getTime();
                certGen.setNotBefore(notBefore);
                calendar.set(2010, 11, 31);
                // la fin de la p�riode de validit�
                certGen.setNotAfter(calendar.getTime());
                // la cl� publique envelopp�e dans le certificat
                certGen.setPublicKey(caKp.getPublic());
                // l'algorithme de signature
                certGen.setSignatureAlgorithm(SHA1_WITH_RSA_OID);
                // extension d�finissant l'usage de la cl�
                certGen.addExtension(
                        KEY_USAGE_OID, false, new KeyUsage(CA_KEY_USAGE_VALUE));
                // extension BasicConstraint
                certGen.addExtension(
                        BASIC_CONSTRAINT_OID, true, new BasicConstraints(Integer.MAX_VALUE));
                // extension subjectKeyIdentifier
                certGen.addExtension(
                        SUBJECT_KEY_IDENTIFIER_OID, 
                        false, 
                        new SubjectKeyIdentifierStructure(caKp.getPublic()));
                this.caCert = certGen.generate(this.caPk);
                ks.load(null, passwd);
                // Ins�rer le certificat dans le keystore
                ks.setCertificateEntry(ALIAS, caCert);
                // Ins�rer la cl� priv�e associ�e dans le keystore
                KeyStore.PrivateKeyEntry pke = 
                        new KeyStore.PrivateKeyEntry(caPk, new Certificate[]{this.caCert});
                ks.setEntry(ALIAS, pke, new KeyStore.PasswordProtection(passwd));
                // Sauvegarder le keystore nouvellement cr��
                OutputStream out = new BufferedOutputStream(new FileOutputStream(caDir));
                ks.store(out, passwd);
        }
        
        /**
         * G�n�ration d'un certificat pour l'identification d'un serveur
         * @param dn le nom distingu� du serveur
         * @param altName le nom alternatif du serveur
         * @param pk la cl� publique devant �tre enrob�e dans le certificat
         * @return un certificat (norme X509 v3) empaquettan la cl� publique pk
         * @throws GeneralSecurityException si la fabrication du certificat �choue
         * @throws IOException si la fabrication du num�ro de s�rie �choue 
         */
        X509Certificate generateServerCertificate(String dn, String altName, PublicKey pk)
                throws GeneralSecurityException, IOException {
                X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
                // le num�ro de s�rie de ce certificat
                certGen.setSerialNumber(SerialIdGenerator.generate());
                // le nom de l'�metteur
                X500Principal caDnI = new X500Principal(DN);
                certGen.setIssuerDN(caDnI);
                // le nom du sujet
                X500Principal caDnS = new X500Principal(dn);
                certGen.setSubjectDN(caDnS);
                Calendar calendar = Calendar.getInstance();
                // le d�but de la p�riode de validit�
                Date notBefore = calendar.getTime();
                certGen.setNotBefore(notBefore);
                calendar.add(Calendar.YEAR, 2);
                // la fin de la p�riode de validit�
                certGen.setNotAfter(calendar.getTime());
                // la cl� publique envelopp�e dans le certificat
                certGen.setPublicKey(pk);
                // l'algorithme de signature
                certGen.setSignatureAlgorithm(SHA1_WITH_RSA_OID);
                // extension d�finissant l'usage de la cl�
                certGen.addExtension(
                        KEY_USAGE_OID, false, new KeyUsage(SV_KEY_USAGE_VALUE));
                // extension d�finissant le nom alternatif du serveur
                certGen.addExtension(
                        SUBJECT_ALT_NAME_OID,
                        false,
                        new GeneralNames(new GeneralName(GeneralName.dNSName, altName)));
                // extension subjectKeyIdentifier
                certGen.addExtension(
                        SUBJECT_KEY_IDENTIFIER_OID, 
                        false, 
                        new SubjectKeyIdentifierStructure(pk));
                return certGen.generate(this.caPk);
        }
                
        /**
         * Exportation du certificat du CA en DER encod� Base64
         * @param file le fichier o� exporter le certificat
         * @param le certificat � exporter
         * @throws GeneralSecurityException si l'encodage DER �choue
         * @throws IOException si l'exportation �choue
         */
    public static void exportCertificate(File file, Certificate cert)
        throws GeneralSecurityException, IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(CERT_BEGIN.getBytes("UTF-8"));
            out.write(Base64.encodeBase64Chunked(cert.getEncoded()));
            out.write(CERT_END.getBytes("UTF-8"));
        }
    }
                
        /**
         * Exportation du certificat du CA en DER encod� base64
         * @param fileName le nom du fichier o� exporter le certificat
         * @param le certificat � exporter
         * @throws GeneralSecurityException si l'encodage DER �choue
         * @throws IOException si l'exportation �choue
         */
        public static void exportCertificate(String fileName, Certificate cert) 
                throws GeneralSecurityException, IOException {
                exportCertificate(new File(fileName), cert);
        }

    /**
     * D�monstration de la classe.
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Pour pouvoir utiliser l'API BouncyCastle au travers du m�canisme standard du JCE
            Security.addProvider(new BouncyCastleProvider());
            // Instanciation d'une CA depuis un fichier keystore, s'il existe
            CA ca = new CA("x4TRDf4JHY578pth".toCharArray());
            // G�n�ration d'une paire de cl�s pour un certificat serveur
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair caKp = kpg.generateKeyPair();
            // G�n�ration du certificat serveur
            PublicKey pk = caKp.getPublic();
            X509Certificate srvCert = ca.generateServerCertificate(
                    "CN=secure.entreprise.fr, OU=FST, O=UHA, L=Mulhouse, ST=68093, C=FR",
                    "secure.entreprise.com",
                    pk);
            // Exportation du certification du serveur
            CA.exportCertificate("srv.cer", srvCert);
            // Exportation du certification du CA
            CA.exportCertificate("ca.cer", ca.caCert);
            // Cr�ation d'un chemin de certification pour srvCert
            CertificateFactory factory = CertificateFactory.getInstance("X509");
            List list = new ArrayList();
            list.add(srvCert);
            CertPath cp = factory.generateCertPath(list);
            byte[] encoded = cp.getEncoded("PKCS7");
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream("srv.p7b"))) {
                out.write(encoded);
            }
            // V�rification de ce chemin de certification en utilisant caCert
            PKIValidator pkiV = new PKIValidator(new String[]{"ca.cer"});
            pk = pkiV.validate("srv.p7b", "PKCS7");
            // Affichage de la cl� publique du serveur
            System.out.println(pk);
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(CA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
