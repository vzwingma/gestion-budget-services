package android.finances.terrier.com.budget.services;

import android.finances.terrier.com.budget.abstrait.AbstractService;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.models.data.BudgetMensuelDTO;
import android.finances.terrier.com.budget.models.data.ContexteUtilisateurDTO;
import android.finances.terrier.com.budget.models.data.transformers.DataTransformerBudget;
import android.finances.terrier.com.budget.utils.AuthenticationPreferencesEnums;
import android.finances.terrier.com.budget.utils.Logger;

import org.jasypt.util.text.BasicTextEncryptor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Services métier
 * Created by vzwingma on 02/01/2015.
 */
public class BusinessService extends AbstractService {


    // Logger
    private static final Logger LOG = new Logger(BusinessService.class);
    private ContexteUtilisateurDTO contexte;
    private DataTransformerBudget dataTransformerBudget;

    private String loginServeur = null;
    private String mdpServeur = null;
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
     * Création d'une identité Android
     * @param login login
     * @param motDePasse mot de passe
     * @param pattern pattern
     */
    public boolean createAndroidId(String login, String motDePasse, char[] pattern) {
        String stringPattern = "";
        for (char p : pattern) {
            stringPattern += p;
        }
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        LOG.info("Création d'une identité Android & Serveur");
        LOG.info("  Pattern : [" + stringPattern + "]     -> [" + hashPassWord(stringPattern) + "]");
        encryptor.setPassword(hashPassWord(stringPattern));
        LOG.info("  Login   : [" + login + "]     -> [" + encryptor.encrypt(login) + "]");
        LOG.info("  Mdp     : [XXXXXXX]     -> [" + encryptor.encrypt(motDePasse) + "]");
        LOG.info("Enregistrement");
        boolean saved =
                FacadeServices.getInstance().getPersistanceService().savePreference(null, AuthenticationPreferencesEnums.ANDROID_ID_PATTERN, hashPassWord(stringPattern))
                        && FacadeServices.getInstance().getPersistanceService().savePreference(null, AuthenticationPreferencesEnums.ANDROID_ID_LOGIN, encryptor.encrypt(login))
                        && FacadeServices.getInstance().getPersistanceService().savePreference(null, AuthenticationPreferencesEnums.ANDROID_ID_PWD, encryptor.encrypt(motDePasse));
        if (saved) {
            setServeurCredential(login, motDePasse);
        } else {
            LOG.error("Erreur lors de l'enregistrement de l'identité Android");
        }
        return saved;
    }


    /**
     * Authentification mobile
     *
     * @param stringPattern pattern à valider
     * @return résultat de l'authentification
     */
    public boolean authenticateToMobile(String stringPattern) {
        LOG.info("Tentative de connexion de " + stringPattern);

        String savedPattern = FacadeServices.getInstance().getPersistanceService().getPreference(null, AuthenticationPreferencesEnums.ANDROID_ID_PATTERN);
        String savedCodeLogin = FacadeServices.getInstance().getPersistanceService().getPreference(null, AuthenticationPreferencesEnums.ANDROID_ID_LOGIN);
        String savedCodePwd = FacadeServices.getInstance().getPersistanceService().getPreference(null, AuthenticationPreferencesEnums.ANDROID_ID_PWD);

        if (stringPattern.equals(savedPattern)) {
            LOG.info(" Identité valide : Déchiffrement de l'identité serveur");
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(hashPassWord(stringPattern));
            LOG.info("  Login   : [" + savedCodeLogin + "]     -> [" + encryptor.decrypt(savedCodeLogin) + "]");
            LOG.info("  Mdp     : [" + savedCodePwd + "]     -> [" + encryptor.decrypt(savedCodePwd) + "]");
            setServeurCredential(encryptor.decrypt(savedCodeLogin), encryptor.decrypt(savedCodePwd));
            return true;
        } else {
            LOG.error("Erreur : Le pattern est incorrect");
        }
        return false;
    }


    public void setServeurCredential(String loginServeur, String mdpServeur) {
        this.loginServeur = loginServeur;
        this.mdpServeur = mdpServeur;
    }

    /**
     * Authentification serveur
     *
     * @return résultat d'authentification
     */
    public boolean authenticateToServeur() {
        boolean authOk = false;
        if (this.loginServeur != null && this.mdpServeur != null) {
            String hashMotPasse = hashPassWord(this.mdpServeur);
            this.contexte = FacadeServices.getInstance().getInterfaceRESTService().getContexte(this.loginServeur, hashMotPasse);
            LOG.info("Tentative de connexion au serveur de " + this.loginServeur);
            authOk = contexte != null && contexte.getUtilisateur() != null;
            if (authOk) {
                this.dataTransformerBudget.setCategories(this.contexte.getCategories());
                contexte.initEncryptor(hashPassWord("#" + this.mdpServeur + "#"));
            }
            LOG.info(" >" + authOk);
        }
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
        return true;
    }
}
