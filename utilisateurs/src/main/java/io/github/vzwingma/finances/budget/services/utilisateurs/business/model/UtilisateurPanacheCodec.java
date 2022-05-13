package io.github.vzwingma.finances.budget.services.utilisateurs.business.model;


import com.mongodb.MongoClientSettings;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Panache Codec pour la classe Utilisateur
 */
@ApplicationScoped
public class UtilisateurPanacheCodec implements CollectibleCodec<Utilisateur> {

    private final Codec<Document> documentCodec;

    public UtilisateurPanacheCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    private static final Logger LOG = LoggerFactory.getLogger(UtilisateurPanacheCodec.class);
    @Override
    public Utilisateur generateIdIfAbsentFromDocument(Utilisateur utilisateur) {
        utilisateur.setId(ObjectId.get());
        return utilisateur;
    }

    @Override
    public boolean documentHasId(Utilisateur utilisateur) {
        return utilisateur != null && utilisateur.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(Utilisateur utilisateur) {
        BsonValue bsonValue = null;
        if (documentHasId(utilisateur)) {
            bsonValue = new BsonString(utilisateur.getId().toString());
        }
        else{
            generateIdIfAbsentFromDocument(utilisateur);
            return getDocumentId(utilisateur);
        }
        return bsonValue;
    }

    /**
     * Décodage de la classe {@link Utilisateur}
     * @param bsonReader reader du BSON issu de la BDD
     * @param decoderContext contexte
     * @return utilisateur lu
     */
    @Override
    public Utilisateur decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Utilisateur utilisateur = new Utilisateur();
        Document document = documentCodec.decode(bsonReader, decoderContext);
        utilisateur.setId(document.getObjectId("_id"));
        utilisateur.setLibelle(document.getString("libelle"));
        utilisateur.setLogin(document.getString("login"));
        utilisateur.setDernierAcces(BudgetDateTimeUtils.getLocalDateTimeFromMillisecond(document.getDate("dernierAcces").getTime()));
     //   document.getList("prefsUtilisateur", Document.class).forEach(pref -> {} );

        return utilisateur;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Utilisateur utilisateur, EncoderContext encoderContext) {
        LOG.warn("Encoding is not implemented");
    }

    @Override
    public Class<Utilisateur> getEncoderClass() {
        return Utilisateur.class;
    }
}
