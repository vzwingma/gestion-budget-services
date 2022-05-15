package io.github.vzwingma.finances.budget.services.comptes.business;


import io.github.vzwingma.finances.budget.services.communs.data.model.CompteBancaire;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Service fournissant les comptes
 * @author vzwingma
 *
 */
@ApplicationScoped
@NoArgsConstructor
public class ComptesService implements IComptesAppProvider {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComptesService.class);
	/**
	 * Service Provider Interface des données
	 */
	@Inject
	IComptesRepository dataComptes;

	@Override
	public Uni<Boolean> isCompteActif(String idCompte) {
		return dataComptes.isCompteActif(idCompte);
	}

	@Override
	public Uni<CompteBancaire> getCompteById(String idCompte, String idUtilisateur) {
		return dataComptes.chargeCompteParId(idCompte, idUtilisateur);
	}

	@Override
	public Uni<List<CompteBancaire>> getComptesUtilisateur(String idUtilisateur) {
		return dataComptes.chargeComptes(idUtilisateur)
				.invoke(compte -> LOGGER.info("Compte [{}] chargé pour l'utilisateur {}", compte, idUtilisateur))
				.collect().asList();
	}
}