/*

 */
package com.terrier.finances.gestion.services.comptes.api;

import com.terrier.finances.gestion.communs.comptes.model.v12.CompteBancaire;
import com.terrier.finances.gestion.communs.utils.data.BudgetApiUrlEnum;
import com.terrier.finances.gestion.communs.utils.exceptions.DataNotFoundException;
import com.terrier.finances.gestion.services.communs.api.AbstractAPIController;
import com.terrier.finances.gestion.services.comptes.business.ports.IComptesRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API Comptes
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value=BudgetApiUrlEnum.COMPTES_BASE)
public class ComptesAPIController extends AbstractAPIController {


	@Autowired
	private IComptesRequest comptesService;


	/**
	 * Retour la liste des comptes
	 * @return liste des comptes de l'utilisateur
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@Operation(method="GET", description="Comptes d'un utilisateur", tags={"Comptes"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CompteBancaire.class))}),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Session introuvable")
	})
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_LIST)
	public @ResponseBody ResponseEntity<List<CompteBancaire>> getComptesUtilisateur() throws DataNotFoundException{
		logger.info("getComptes");
		return getEntities(comptesService.getComptesUtilisateur(getIdProprietaire()));
	}

	/**
	 * Retourne le compte
	 * @param idCompte id du compte
	 * @return compte associé
	 * @throws DataNotFoundException erreur données non trouvées
	 */
	@Operation(method="GET",description="Compte d'un utilisateur", tags={"Comptes"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération réussie",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CompteBancaire.class)) }),
			@ApiResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
			@ApiResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
			@ApiResponse(responseCode = "404", description = "Données introuvables")
	})
	@GetMapping(value=BudgetApiUrlEnum.COMPTES_ID)
	public @ResponseBody ResponseEntity<CompteBancaire> getCompteUtilisateur(@PathVariable("idCompte") String idCompte) throws DataNotFoundException{
		logger.info("[idCompte={}] getCompte", idCompte);
		return getEntity(comptesService.getCompteById(idCompte, getIdProprietaire()));
	}
}
