package android.finances.terrier.com.budget.services.rest;

import android.finances.terrier.com.budget.models.DepenseCategorie;

import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.KeyDeserializer;

import java.io.IOException;

/**
 * Deserialiser d'une catégorie de dépense
 * Created by vzwingma on 31/12/2014.
 */
public class DepenseCategorieDeserializer extends KeyDeserializer {


    /**
     * Method called to deserialize a {@link java.util.Map} key from JSON property name.
     *
     * @param key  cle de la map (
     * @param ctxt contexte de serialisation
     */
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return ctxt.getParser().readValueAs(DepenseCategorie.class);
    }
}
