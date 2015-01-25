package android.finances.terrier.com.budget.services;

import android.finances.terrier.com.budget.abstrait.AbstractService;
import android.finances.terrier.com.budget.utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Facade des services
 * Created by vzwingma on 27/12/2014.
 */
public class FacadeServices {


    // Logger
    private static final Logger LOG = new Logger(FacadeServices.class);
    /**
     * Singleton de la facade
     */
    private static FacadeServices facade;
    /**
     * Map des services démarrés
     */
    private final Map<Class<? extends AbstractService>, AbstractService> mapServicesStarted = new HashMap<>();

    /**
     * Instance de facadeService (en protected car accessible des controleurs uniquement)
     *
     * @return l'instance de la facade de services
     */
    public static FacadeServices getInstance() {
        if (facade == null) {
            LOG.error("Le service Android FacadeService n'existe pas. Démarrage");
            FacadeServices.facade = new FacadeServices();
        }
        return FacadeServices.facade;
    }

    /**
     * Start des services
     */
    public static void initServices() {
        getInstance().startService(InterfaceRESTService.class);
        getInstance().startService(BusinessService.class);
    }

    /**
     * ACTIONS SUR LES SERVICES
     */

    /**
     * Arrêt de tous les services
     */
    public static void stopAndroidServices() {
        for (AbstractService serviceClass : getInstance().mapServicesStarted.values()) {
            FacadeServices.getInstance().stopService(serviceClass.getClass());
        }
    }

    /**
     * Service REST de Budget
     *
     * @return interface REST
     */
    protected InterfaceRESTService getInterfaceRESTService() {
        return getServiceStarted(InterfaceRESTService.class);
    }

    public BusinessService getBusinessService() {
        return getServiceStarted(BusinessService.class);
    }

    /**
     * Démarrage d'un service
     *
     * @param serviceClass classe du service
     */
    private boolean startService(Class<? extends AbstractService> serviceClass) {
        if (mapServicesStarted.get(serviceClass) == null) {
            LOG.info("Démarrage du service de type " + serviceClass.getName());
            try {
                AbstractService service = (AbstractService) serviceClass.getConstructors()[0].newInstance();
                this.mapServicesStarted.put(service.getClass(), service);
                // Démarrage du service
                service.onCreate();
                return true;
            } catch (IllegalArgumentException e) {
                LOG.error(" Impossible de créer le service " + serviceClass.getName() + ". Arguments incorrects", e);
            } catch (InstantiationException e) {
                LOG.error(" Impossible de créer le service " + serviceClass.getName() + ". Erreur lors de l'instantiation", e);
            } catch (IllegalAccessException e) {
                LOG.error(" Impossible de créer le service " + serviceClass.getName() + ". Accès interdit", e);
            } catch (InvocationTargetException e) {
                LOG.error(" Impossible de créer le service " + serviceClass.getName() + ". Invocation exception", e);
            }
        } else {
            LOG.warn("Un service de type " + serviceClass.getName() + " existe déjà. Impossible de créer un nouveau service de ce type");
        }
        return false;
    }


    /**
     * Arrêt d'un service
     *
     * @param serviceClass classe du service
     */
    private boolean stopService(Class<? extends AbstractService> serviceClass) {
        LOG.info("Arrêt du service de type " + serviceClass.getName());
        AbstractService service = mapServicesStarted.get(serviceClass);
        if (service != null) {
            boolean arret = service.onDestroy();
            if (arret) {
                LOG.info("	[OK]");
                return true;
            } else {
                LOG.warn("	[NOK] Impossible d'arrêter le service de type " + serviceClass.getName());
            }
        } else {
            LOG.warn("	Impossible de trouver un service démarré associé au type de service " + serviceClass.getName());
        }
        return false;
    }


    /**
     * Recherche de l'instance de service démarrée
     *
     * @param classService classe du service
     * @return instance du service
     */
    @SuppressWarnings("unchecked")
    private <ServiceStarted extends AbstractService> ServiceStarted getServiceStarted(Class<ServiceStarted> classService) {
        AbstractService service = mapServicesStarted.get(classService);

        // Recherche de service
        if (service != null) {
            return (ServiceStarted) service;
        } else {
            LOG.error("Impossible de trouver un service démarré du type " + classService.getName());
            return null;
        }
    }
}
