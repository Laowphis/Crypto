import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Une classe permettant la validation PKIX (minimale) de certificats
 * @author Patrick Guichet & Julien Lepagnot
 */
public class PKIValidator {
    // le validateur PKIX
    private CertPathValidator validator;
    // les param�tres de validation
    private PKIXParameters pkixParams;

    /**
     * Construction d'une instance de la classe � partir d'un fichier
     * de certificats d'autorit�s de confiance et d'un fichier de liste de r�vocation
     * @param caFiles les fichiers des certificats des autorit�s de confiance
     * @param crlFiles les fichiers de CRL (peut �tre vide)
     * @throws GeneralSecurityException si la reconstruction d'un certificat
     * ou d'une CRL �choue
     * @throws IOException si la lecture d'un fichier �choue
     */
    public PKIValidator(File[] caFiles, File... crlFiles) throws GeneralSecurityException, IOException {
        // Instantiation du validateur
        this.validator = CertPathValidator.getInstance("PKIX");
        // Instantiation des param�tres de validation
        this.pkixParams = new PKIXParameters(getCAFromFiles(caFiles));
        // Une liste de r�vocation doit elle �tre utilis�e
        if (crlFiles.length == 0) {
            pkixParams.setRevocationEnabled(false);
        } else {
            // si oui cr�er le magasin o� elle sera stock�e
            // il est directement initialis� avec la CRL transmise
            Collection<CRL> crls = new HashSet<>();
            for (File file : crlFiles) {
                crls.addAll(getCRLFromFile(file));
            }
            CollectionCertStoreParameters ccsParams =
                    new CollectionCertStoreParameters(crls);
            CertStore certStore = CertStore.getInstance("collection", ccsParams);
            pkixParams.addCertStore(certStore);
            pkixParams.setRevocationEnabled(true);
        }
    }

    /**
     * Construction d'une instance de la classe � partir d'un fichier
     * de certificats d'autorit�s de confiance et d'un fichier de liste de r�vocation
     * @param caFileName le nom du fichier des autorit�s de confiance
     * @param crlFileName les noms des fichiers de CRL (�ventuellement aucun)
     * @throws GeneralSecurityException si la reconstruction d'un certificat
     * ou d'une CRL �choue
     * @throws IOException si la lecture d'un fichier �choue
     */
    public PKIValidator(String[] caFileNames, String... crlFileNames)
            throws GeneralSecurityException, IOException {
        this(getFiles(caFileNames), getFiles(crlFileNames));
    }

    /**
     * M�thode d'aide pour la construction d'un tableau de descripteurs de fichiers
     * depuis une liste de leurs noms
     * @param crlFileNames la liste d'arguments variables des noms de fichiers
     * @return
     */
    private static File[] getFiles(String... crlFileNames) {
        File[] files = new File[crlFileNames.length];
        int i = 0;
        for (String fileName : crlFileNames) {
            System.out.println("--> " + fileName);
            files[i] = new File(crlFileNames[i]);
            ++i;
        }
        return files;
    }

    /**
     * M�thode d'aide pour la fabrication d'un ensemble de
     * certificats d'autorit�s de confiance
     * @param caFiles les fichiers o� sont stock�s les certificats
     * @return une collection de certificats de CA
     * @throws IOException si la lecture du fichier �choue
     * @throws CertificateException si la reconstruction d'un certificat �choue
     */
    private static Set<TrustAnchor> getCAFromFiles(File... caFiles)
            throws IOException, CertificateException {
        Set<TrustAnchor> cas = new HashSet<>();
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        for (File caFile : caFiles) {
            InputStream is = new BufferedInputStream(new FileInputStream(caFile));
            for (Certificate cert : cf.generateCertificates(is)) {
                cas.add(new TrustAnchor((X509Certificate) cert, null));
            }
        }
        return cas;
    }

    /**
     * M�thode d'aide pour la fabrication d'une collection de CRL
     * @param crlFile le fichier o� sont stock�s les CRL
     * @return une collection de CRL
     * @throws IOException si la lecture du fichier �choue
     * @throws CertificateException si la reconstruction d'une CRL �choue
     * @throws CRLException
     */
    private static Collection<? extends CRL> getCRLFromFile(File crlFile)
            throws IOException, CertificateException, CRLException {
        InputStream in = new BufferedInputStream(new FileInputStream(crlFile));
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        return cf.generateCRLs(in);
    }

    /**
     * Validation d'un chemin de certification relativement aux CA du validateur
     * @param cpFile le fichier o� est stock�e l'encodage du chemin de certification
     * @param  encoding l'encodage utilis� : PKCS7 ou PkiPath
     * @return la cl� publique du certificat valid�
     * @throws GeneralSecurityException si la validation �choue
     * @throws IOException si la lecture du fichier �choue
     */
    public PublicKey validate(File cpFile, String encoding) throws GeneralSecurityException, IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        CertPath path = cf.generateCertPath(new BufferedInputStream(new FileInputStream(cpFile)), encoding);
        PKIXCertPathValidatorResult result =
                (PKIXCertPathValidatorResult) validator.validate(path, pkixParams);
        return result.getPublicKey();
    }

    /**
     * Validation d'un chemin de certification relativement aux CA du validateur
     * @param cpFile le nom du fichier o� est stock�e l'encodage
     * au format p7b du chemin de certification
     * @return la cl� publique du certificat valid�
     * @throws GeneralSecurityException si la validation �choue
     * @throws IOException si la lecture du fichier �choue
     */
    public PublicKey validate(String cpFileName, String encoding) throws GeneralSecurityException, IOException {
        return validate(new File(cpFileName), encoding);
    }

    /**
     * Test de la classe
     * @param args
     */
    public static void main(String[] args) {
        try {
            PKIValidator pkiV = new PKIValidator(new String[]{"msrc.cer", "msca.cer"}, "ms1.crl", "ms.crl");
            PublicKey pk = pkiV.validate("mscpa.p7b","PKCS7");
            System.out.println(pk);
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(PKIValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
