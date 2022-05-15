package io.github.vzwingma.finances.budget.services.comptes.business;


import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesAppProvider;
import io.github.vzwingma.finances.budget.services.comptes.business.ports.IComptesRepository;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
	IComptesRepository dataParams;

}
