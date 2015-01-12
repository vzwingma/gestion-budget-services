package android.finances.terrier.com.budget.abstrait;

/**
 * Created by vzwingma on 27/12/2014.
 * Classe abstraite d'un service
 *
 * @author vzwingma
 */
public abstract class AbstractService {

    /**
     * @return facade des services

    public FacadeServices getFacadeServices(){
    return FacadeServices.getInstance();
    }
     */

    /**
     * Création du service
     */
    public abstract void onCreate();

    /**
     * Arrêt du service
     */
    public abstract boolean onDestroy();
}
