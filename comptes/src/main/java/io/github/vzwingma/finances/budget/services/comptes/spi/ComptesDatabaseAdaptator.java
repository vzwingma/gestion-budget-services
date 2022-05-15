package io.github.vzwingma.finances.budget.services.comptes.spi;

import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Service de données en MongoDB fournissant les comptes.
 * Adapteur du port {@link IComptesRepository}
 * @author vzwingma
 *
 */
@ApplicationScoped
public class ComptesDatabaseAdaptator implements IComptesRepository { // extends AbstractDatabaseServiceProvider<CategorieOperation>


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesDatabaseAdaptator.class);

	/**
	 * Chargement des comptes
	 * @param idUtilisateur utilisateur
	 * @return liste des comptes associés
	 */
	public Multi<CompteBancaire> chargeComptes(String idUtilisateur) {
		try{
			LOGGER.info("[idUser={}] Chargement des comptes de l'utilisateur", idUtilisateur);
			return find("proprietaire.login", idUtilisateur)
					.stream()
					.invoke(compte -> LOGGER.debug("Chargement du compte [{}] en BDD terminé", compte.getLibelle()));
		}
		catch(Exception e){
			LOGGER.error("[idUser={}] Erreur lors de la connexion à la BDD", idUtilisateur, e);
			return Multi.createFrom().failure(new DataNotFoundException("Erreur lors de la recherche des comptes "));
		}
	}


	/**
	 * Chargement d'un compte par un id
	 * @param idCompte id du compte
	 * @param idUtilisateur utilisateur associé
	 * @return compte
	 */
	public Uni<CompteBancaire> chargeCompteParId(String idCompte, String idUtilisateur) {
		try{
			LOGGER.info("[idCompte={}] Chargement du compte", idCompte);
			return find("_id", idCompte)
					.singleResult()
					.onItem()
						.ifNull().failWith(new DataNotFoundException("Compte non trouvé"));
		}
		catch(Exception e){
			LOGGER.error("[idUser=?] Erreur lors de la connexion à la BDD", e);
			return Uni.createFrom().failure(new DataNotFoundException("Erreur lors de la recherche du compte " + idCompte));
		}
	}


	/**
	 * Chargement d'un compte par un id
	 * @param idCompte id du compte
	 * @return compte
	 */
	public Uni<Boolean> isCompteActif(String idCompte) {
		try{
			return find("id = ?1 and actif = ?2", idCompte, true)
					.singleResultOptional()
					.onItem()
						.ifNull()
							.failWith(new DataNotFoundException("Compte non trouvé"))
						.map(compte -> compte.orElse(CompteBancaire.getCompteInactif()).getActif())
					.invoke(compteActif -> LOGGER.info("[idCompte={}] Compte actif ? {}", idCompte, compteActif));
		}
		catch(Exception e) {
			LOGGER.error("[idUser=?] Erreur lors de la connexion à la BDD", e);
			return Uni.createFrom().failure(new DataNotFoundException("Erreur lors de la recherche du compte " + idCompte));
		}
	}
}