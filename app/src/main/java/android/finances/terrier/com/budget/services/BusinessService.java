package android.finances.terrier.com.budget.services;

import android.finances.terrier.com.budget.abstrait.AbstractService;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.models.data.BudgetMensuelDTO;
import android.finances.terrier.com.budget.models.data.ContexteUtilisateurDTO;
import android.finances.terrier.com.budget.models.data.transformers.DataTransformerBudget;
import android.finances.terrier.com.budget.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Services métier
 * Created by vzwingma on 02/01/2015.
 */
public class BusinessService extends AbstractService {


    // Logger
    private static final Logger LOG = new Logger(FacadeServices.class);
    private ContexteUtilisateurDTO contexte;
    private DataTransformerBudget dataTransformerBudget;

    /**
     * @param password mot de passe
     * @return password hashé en 256
     */
    private static String hashPassWord(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Authentification
     *
     * @param login      login
     * @param motDePasse mot de passe
     * @return résultat d'authentification
     */
    public boolean authenticate(String login, String motDePasse) {

        String hashMotPasse = hashPassWord(motDePasse);
        this.contexte = FacadeServices.getInstance().getInterfaceRESTService().getContexte(login, hashMotPasse);
        this.dataTransformerBudget.setCategories(this.contexte.getCategories());
        LOG.info("Tentative de connexion de " + login);

        boolean authOk = contexte != null;
        if (authOk) {
            contexte.initEncryptor(hashPassWord("#" + motDePasse + "#"));
        }
        LOG.info(" >" + authOk);
        return authOk;
    }

    /**
     * @return contexte utilisateur
     */
    public ContexteUtilisateurDTO getContexte() {
        return this.contexte;
    }

    /**
     * Chargement du budget
     *
     * @param mois   mois du compte
     * @param annee  année du compte
     * @param compte id du compte
     * @return budget budget résultat
     */
    public BudgetMensuel getBudget(String mois, String annee, String compte) {
        BudgetMensuelDTO dto = FacadeServices.getInstance().getInterfaceRESTService().getBudget(mois, annee, compte);
        return dataTransformerBudget.transformDTOtoBO(dto, this.contexte.getEncryptor());
    }


    /**
     * Création du service
     */
    @Override
    public void onCreate() {
        dataTransformerBudget = new DataTransformerBudget();
    }

    /**
     * Arrêt du service
     */
    @Override
    public boolean onDestroy() {
        return false;
    }
}
