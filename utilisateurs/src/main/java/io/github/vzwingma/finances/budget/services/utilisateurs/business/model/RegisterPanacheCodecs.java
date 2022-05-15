package io.github.vzwingma.finances.budget.services.utilisateurs.business.model;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Enregistrement des codecs pour les échanges avec MongoDB
 */
public class RegisterPanacheCodecs implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz.equals(Utilisateur.class)) {
            return (Codec<T>) new UtilisateurPanacheCodec();
        }
        return null;
    }

}
