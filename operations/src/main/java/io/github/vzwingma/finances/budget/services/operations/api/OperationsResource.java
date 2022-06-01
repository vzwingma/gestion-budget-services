package io.github.vzwingma.finances.budget.services.operations.api;

import io.github.vzwingma.finances.budget.services.communs.api.AbstractAPIResource;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.BadParametersException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.DataNotFoundException;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.UserNotAuthorizedException;
import io.github.vzwingma.finances.budget.services.operations.api.enums.OperationsApiUrlEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.IntervallesCompteAPIObject;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LibellesOperationsAPIObject;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IBudgetAppProvider;
import io.github.vzwingma.finances.budget.services.operations.business.ports.IOperationsAppProvider;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Month;
import java.util.Set;

/**
 * Controleur REST -
 * Adapteur du port {@link IOperationsAppProvider}
 * @author vzwingma
 *
 */
@Path(OperationsApiUrlEnum.BUDGET_BASE)
public class OperationsResource extends AbstractAPIResource {

    private static final Logger LOG = LoggerFactory.getLogger(OperationsResource.class);


    @Inject
    IBudgetAppProvider budgetService;

    @Inject
    IOperationsAppProvider operationsService;

    private String getIdProprietaire() {
        return "vzwingmann";
    }
    /**
     * Retour le budget d'un utilisateur
     * @param idCompte id du compte
     * @param mois mois du budget
     * @param annee année du budget
     * @return budget
     */
    @Operation(description = "Recherche d'un budget mensuel pour un compte d'un utilisateur")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_QUERY)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> getBudget(
            @RestQuery("idCompte") String idCompte,
            @RestQuery("mois") Integer mois,
            @RestQuery("annee") Integer annee) {
        LOG.trace("[idCompte={}] getBudget {}/{}", idCompte, mois, annee);

        if(mois != null && annee != null){
            try{
                return budgetService.getBudgetMensuel(idCompte, Month.of(mois), annee, getIdProprietaire());
            }
            catch(NumberFormatException e){
                return Uni.createFrom().failure(new BadParametersException("Mois et année doivent être des entiers"));
            }
        }
        return Uni.createFrom().failure(new BadParametersException("Mois et année doivent être renseignés"));
    }



    /**
     * Mise à jour du budget
     * @param idBudget id du budget
     * @return budget mis à jour
     */
    @Operation(description="Chargement d'un budget")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Budget chargé",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> getBudget(@RestPath("idBudget") String idBudget) {

        LOG.trace("[idBudget={}] chargeBudget", idBudget);
        if(idBudget != null){
            return budgetService.getBudgetMensuel(idBudget);
        }
        else{
            return Uni.createFrom().failure(new BadParametersException("L'id du budget doit être renseigné"));
        }
    }


    /**
     * Mise à jour du budget
     * @param idBudget id du budget
     * @return budget mis à jour
     */
    @Operation(description="Réinitialisation d'un budget")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Budget réinitialisé",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "405", description = "Compte clos. Impossible de réinitialiser le budget")
    })
    @DELETE
    @Path(value=OperationsApiUrlEnum.BUDGET_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> reinitializeBudget(@RestPath("idBudget") String idBudget){
        LOG.trace("[idBudget={}] reinitialisation", idBudget);
        String proprietaire = getIdProprietaire();
        if(idBudget != null && proprietaire != null){
            return budgetService.reinitialiserBudgetMensuel(idBudget, proprietaire);
        }
        else{
            return Uni.createFrom().failure(new BadParametersException("L'id du budget doit être renseigné"));
        }
    }
    /**
     * Retourne le statut du budget
     * @param idBudget id du compte
     * @return statut du budget
     */
    @Operation(description="Retourne l'état d'un budget mensuel : {etat}; {etat} : indique si le budget est ouvert ou cloturé.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Budget actif"),
            @APIResponse(responseCode = "423", description = "Budget inactif"),
            @APIResponse(responseCode = "400", description = "Paramètres incorrects"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_ETAT)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> isBudgetActif(
            @RestPath("idBudget") String idBudget,
            @RestQuery(value = "actif") Boolean actif) {

        LOG.debug("[idBudget={}] actif ? : {}", idBudget, actif);

        if(Boolean.TRUE.equals(actif)){
            return budgetService.isBudgetMensuelActif(idBudget);
        }
        return Uni.createFrom().failure(new BadParametersException("Les paramètres {idBudget}=" +idBudget + " et {actif}=" + actif + " ne sont pas valides"));
    }


    /**
     * Retourne le statut du budget
     * @param idBudget id du compte
     * @return statut du budget
     */
    @Operation(description="Retourne l'état de mise à jour d'un budget mensuel : {uptodate} : {uptodate} indique si le budget a été mis à jour en BDD par rapport à la date passée en paramètre")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Budget à jour"),
            @APIResponse(responseCode = "400", description = "Paramètres incorrects"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "426", description = "Le Budget a été mis à jour par rapport à la date renvoyée")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_UP_TO_DATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> isBudgetUptoDate(
            @RestPath("idBudget") String idBudget, @RestQuery(value="uptodateto") Long uptodateto) {

        LOG.trace("[idBudget={}] uptodateto ? {}", idBudget, uptodateto );

        if(uptodateto != null){
            return budgetService.isBudgetIHMUpToDate(idBudget, uptodateto)
                    .onItem()
                    .invoke(isUpToDate -> LOG.info("[idBudget={}] isIHM Up To Date {} ? : {}", idBudget, BudgetDateTimeUtils.getLibelleDateFromMillis(uptodateto), isUpToDate))
                    .onItem().ifNull().failWith(new DataNotFoundException("Le budget mensuel n'a pas été trouvé"));
        }
        return Uni.createFrom().failure(new BadParametersException("Les paramètres {idBudget}=" +idBudget + " et {uptodateto}=" + uptodateto + " ne sont pas valides"));
    }

    /**
     * Met à jour le statut du budget
     * @param idBudget id du compte
     * @return statut du budget
     */
    @Operation(description="Mise à jour de l'{état} d'un budget mensuel (ouvert/cloturé) ; {etat} : indique si le budget est ouvert ou cloturé.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "500", description = "Opération en échec")
    })
    @POST
    @Path(value=OperationsApiUrlEnum.BUDGET_ETAT)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> setBudgetActif(
            @RestPath("idBudget") String idBudget,
            @RestQuery(value="actif") Boolean actif) {

        LOG.info("[idBudget={}] set Actif : {}", idBudget, actif );
        return budgetService.setBudgetActif(idBudget, actif, getIdProprietaire());
    }


    /* ********************************************************
     *                      OPERATIONS
     *********************************************************/


    /**
     * Met à jour le flag de l'opération comme dernière opération réalisée
     * @param idBudget id du compte
     * @return résultat de l'action
     */
    @Operation(description="Met à jour le flag de l'opération comme dernière opération réalisée")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Budget mis à jour"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @POST
    @Path(value=OperationsApiUrlEnum.BUDGET_OPERATION_DERNIERE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> setAsDerniereOperation(
            @RestPath("idBudget") String idBudget,
            @RestPath("idOperation") String idOperation)  {

        LOG.info("[idBudget={}][idOperation={}] setAsDerniereOperation", idBudget, idOperation);
        return operationsService.setLigneAsDerniereOperation(idBudget, idOperation, getIdProprietaire());
    }


    /**
     * Mise à jour d'une opération
     * @param idBudget id du budget
     * @param operation opération à mettre à jour
     * @return budget mis à jour
     */
    @Operation(description="Mise à jour d'une opération")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération mise à jour",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "400", description = "Paramètres incorrects"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "423", description = "Compte clos")
    })
    @POST
    @Path(value=OperationsApiUrlEnum.BUDGET_OPERATION)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> createOrUpdateOperation(
            @RestPath("idBudget") String idBudget,
            @RestPath("idOperation") String idOperation,
            LigneOperation operation) {

        LOG.info("[idBudget={}][idOperation={}] createOrUpdateOperation", idBudget, idOperation);
        if(operation != null && idBudget != null && getIdProprietaire() != null){
            operation.setId(idOperation);
      //      operationsService.completeCategoriesOnOperation(operation, this.paramClientApi.getCategories());

            return operationsService.updateOperationInBudget(idBudget, operation, getIdProprietaire());

/*            catch (CompteClosedException e) {
                return ResponseEntity.unprocessableEntity().build();
            }*/
        }
        else {
            return Uni.createFrom().failure(new BadParametersException("Les paramètres idBudget et idOperation sont obligatoires"));
        }
    }



    /**
     * Création d'une opération inter comptes
     * @param idBudget id du budget
     * @param idOperation opération à mettre à jour
     * @param idCompte id du compte à mettre à jour
     * @return budget mis à jour
     */
    @Operation(description="Mise à jour d'une opération Intercomptes")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération mise à jour",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "400", description = "Paramètres incorrects"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "423", description = "Compte clos")
    })
    @POST
    @Path(value=OperationsApiUrlEnum.BUDGET_OPERATION_INTERCOMPTE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> createOperationIntercomptes(
            @RestPath("idBudget") String idBudget,
            @RestPath("idOperation") String idOperation,
            @RestPath("idCompte") String idCompte,
            LigneOperation operation) {

        LOG.info("[idBudget={}][idOperation={}] createOperation InterCompte [{}]", idBudget, idOperation, idCompte);
        if(operation != null && idBudget != null){

            operation.setId(idOperation);
       //     operationsService.completeCategoriesOnOperation(operation, this.paramClientApi.getCategories());
            return operationsService.createOperationIntercompte(idBudget, operation, idCompte, getIdProprietaire());
        }
        else{
            return Uni.createFrom().failure(new BadParametersException("Les paramètres idBudget, idOperation et idCompte sont obligatoires"));
        }
    }

    /**
     * Suppression d'une opération
     * @param idBudget id du budget
     * @param idOperation opération à mettre à jour
     * @return budget mis à jour
     */
    @Operation(description="Suppression d'une opération")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération supprimée",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BudgetMensuel.class))}),
            @APIResponse(responseCode = "204", description = "Opération supprimée"),
            @APIResponse(responseCode = "400", description = "Paramètres incorrects"),
            @APIResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @APIResponse(responseCode = "403", description = "Opération non autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables"),
            @APIResponse(responseCode = "405", description = "Compte clos")
    })
    @DELETE
    @Path(value=OperationsApiUrlEnum.BUDGET_OPERATION)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BudgetMensuel> deleteOperation(
            @RestPath("idBudget") String idBudget,
            @RestPath("idOperation") String idOperation) {

        if(idOperation != null && idBudget != null && getIdProprietaire() != null){
            LOG.info("[idBudget={}][idOperation={}] deleteOperation", idBudget, idOperation);
            return operationsService.deleteOperation(idBudget, idOperation, getIdProprietaire());
        }
        else{
            return Uni.createFrom().failure(new BadParametersException("Les paramètres idBudget, et idOperation sont obligatoires"));
        }
    }


    /**
     * Retourne l'invervalle des budgets pour le compte
     * @param idCompte id du compte
     * @return l'intervalle des budgets
     */
    @Operation(description="Intervalles des budgets pour un compte")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = IntervallesCompteAPIObject.class))}),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_COMPTE_INTERVALLES)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<IntervallesCompteAPIObject> getIntervallesBudgetsCompte(@RestPath("idCompte") String idCompte) {
        LOG.info("[idCompte={}] getIntervallesBudgetsCompte", idCompte);
        if(getIdProprietaire() == null) {
            return Uni.createFrom().failure(new UserNotAuthorizedException("L'utilisateur n'est pas authentifié"));
        }
        if(idCompte == null){
                return Uni.createFrom().failure(new BadParametersException("Le paramètre idCompte est obligatoire"));
        }
            return this.budgetService.getIntervallesBudgets(idCompte)
                    .onItem()
                        .transform(intervalles -> {
                            if(intervalles != null && intervalles.length >= 2){
                                IntervallesCompteAPIObject intervallesAPI = new IntervallesCompteAPIObject();
                                intervallesAPI.setDatePremierBudget(BudgetDateTimeUtils.getNbDayFromLocalDate(intervalles[0]));
                                intervallesAPI.setDateDernierBudget(BudgetDateTimeUtils.getNbDayFromLocalDate(intervalles[1]));
                                return intervallesAPI;
                            }
                            else{
                                return null;
                            }
                        })
                    .onItem()
                        .ifNull().failWith(new DataNotFoundException("Impossible de trouver l'intervalle de budget pour le compte " + idCompte));
    }



    /**
     * Liste des libellés des opérations d'un compte (tout mois confondu)
     * @param idCompte idCompte
     * @param annee année
     */
    @Operation(description="Libelles des opérations des budgets de l'année pour un compte")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Opération réussie",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LibellesOperationsAPIObject.class))}),
            @APIResponse(responseCode = "204", description = "Aucune donnée"),
            @APIResponse(responseCode = "401", description = "L'utilisateur doit être authentifié"),
            @APIResponse(responseCode = "403", description = "L'opération n'est pas autorisée"),
            @APIResponse(responseCode = "404", description = "Données introuvables")
    })
    @GET
    @Path(value=OperationsApiUrlEnum.BUDGET_COMPTE_OPERATIONS_LIBELLES)
    @Produces(MediaType.APPLICATION_JSON)
    public  Uni<LibellesOperationsAPIObject> getLibellesOperations(@RestPath("idCompte") String idCompte, @RestQuery("annee") Integer annee) {
        LOG.info("[idCompte={}] get Libellés Opérations de l'année {}", idCompte, annee);
        if(getIdProprietaire() != null) {
            return this.operationsService.getLibellesOperations(idCompte, annee)
                    .collect().asList()
                    .map(libelles -> {
                        if(libelles != null && !libelles.isEmpty()){
                            LibellesOperationsAPIObject libellesO = new LibellesOperationsAPIObject();
                            libellesO.setIdCompte(idCompte);
                            libellesO.setLibellesOperations(Set.copyOf(libelles));
                            return libellesO;
                        }
                        else{
                            return null;
                        }
                    })
                    .onItem()
                        .ifNull().failWith(new DataNotFoundException("Impossible de trouver les libellés des opérations pour le compte " + idCompte));
        }
        else {
            return Uni.createFrom().failure(new UserNotAuthorizedException("L'utilisateur n'est pas authentifié"));
        }
    }

}