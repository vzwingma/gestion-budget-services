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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

	public ComptesService(IComptesRepository dataComptes) {
		this.dataComptes = dataComptes;
	}

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
				.invoke(compte -> LOGGER.trace("Compte [{}] chargé pour l'utilisateur {}", compte.getLibelle(), idUtilisateur))
				.collect().asList()
				.onItem()
					.transform(comptes ->
							comptes.stream()
								.sorted(Comparator.comparingInt(CompteBancaire::getOrdre))
								.collect(Collectors.toList()));
	}
}
