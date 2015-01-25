package android.finances.terrier.com.budget.abstrait;

import android.finances.terrier.com.budget.services.rest.RESTDataModule;
import android.finances.terrier.com.budget.utils.Logger;
import android.finances.terrier.com.budget.utils.NetworkUtils;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;


/**
 * Webservice REST
 *
 * @author vzwingma
 */
public abstract class AbstractRESTService extends AbstractService {


    // Logger
    private static final Logger LOG = new Logger(AbstractRESTService.class);


    // create HttpClient
    private HttpClient httpclient;
    private ObjectMapper objectMapper = null;

    /**
     * Création du service
     */
    @Override
    public void onCreate() {
        httpclient = new DefaultHttpClient();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new RESTDataModule());

    }


    /**
     * Arrêt du service
     */
    @Override
    public boolean onDestroy() {
        return true;
    }


    /**
     * @return racine de l'URL
     */
    private String getRootURL() {
        return NetworkUtils.IP_SERVEUR + "/rest";
    }


    /**
     * Appel GET
     *
     * @param url URL appelée par le service GET
     * @return Résultat T
     */
    protected <T> T GET(String url, Class<T> classeAttendue) {
        T result = null;
        LOG.info("Appel GET de " + getRootURL() + url);
        try {
            // make GET request to the given URL
            HttpGet get = new HttpGet(getRootURL() + url);
            // Ajout de basic authentication

            String authentication = NetworkUtils.REST_BASIC_AUTH_LOGIN + ":" + NetworkUtils.REST_BASIC_AUTH_PWD;
            String base64 = Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP);
            get.addHeader("Authorization", "Basic " + base64);
            HttpResponse httpResponse = httpclient.execute(get);
            LOG.info("  [" + httpResponse.getStatusLine() + "]");
            // receive response as inputStream
            InputStream inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToObject(inputStream, classeAttendue);
            }
        } catch (Exception e) {
            LOG.error("Erreur lors de l'appel GET", e);
        }
        return result;
    }


    /**
     * Convertion du résultat
     *
     * @param inputStream stream issu du service
     * @return résultat
     * @throws IOException erreur lors de la lecture
     */
    private <T> T convertInputStreamToObject(InputStream inputStream, Class<T> classeAttendue) throws IOException {
        return objectMapper.readValue(inputStream, classeAttendue);
    }
}
