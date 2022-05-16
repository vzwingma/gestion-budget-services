package io.github.vzwingma.finances.budget.services.operations.utils;

import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import io.github.vzwingma.finances.budget.services.communs.utils.data.BudgetDateTimeUtils;
import io.github.vzwingma.finances.budget.services.communs.utils.exceptions.BudgetNotFoundException;
import io.github.vzwingma.finances.budget.services.operations.business.model.budget.BudgetMensuel;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.EtatOperationEnum;
import io.github.vzwingma.finances.budget.services.operations.business.model.operation.LigneOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utilitaire de data
 * @author vzwingma
 *
 */
public class BudgetDataUtils {

	protected static final Logger LOGGER = LoggerFactory.getLogger( BudgetDataUtils.class );


	private BudgetDataUtils(){
		// constructeur privé
	}

	
	/**
	 * @param idCompte id compte bancaire
	 * @param mois mois
	 * @param annee année
	 * @return id de budget
	 */
	public static String getBudgetId(String idCompte, Month mois, int annee){
		return String.format("%s_%s_%s", idCompte, annee, String.format("%02d", mois.getValue()));
	}
	/**
	 * @param budgetId id budget
	 * @return la valeur de l'année à partir de l'id
	 * @throws BudgetNotFoundException budget introuvable car erreur d'id
	 */
	public static Month getMoisFromBudgetId(String budgetId) throws BudgetNotFoundException {
		if(budgetId != null){
			try {
				return Month.of(Integer.parseInt(budgetId.substring(budgetId.lastIndexOf('_') + 1)));
			}
			catch (Exception e) {
				// Erreur dans l'id
				throw new BudgetNotFoundException("Erreur dans l'id du budget " + budgetId + ". Données incohérentes");
			}
		}
		return null;
	}
	/**
	 * @param budgetId id budget
	 * @return la valeur de l'année à partir de l'id
	 * @throws BudgetNotFoundException budget introuvable car erreur d'id
	 */
	public static String getCompteFromBudgetId(String budgetId) throws BudgetNotFoundException {
		if(budgetId != null){
			try {
				return budgetId.substring(0, budgetId.indexOf('_'));
			}
			catch (Exception e) {
				// Erreur dans l'id
				throw new BudgetNotFoundException("Erreur dans l'id du budget " + budgetId + ". Données incohérentes");
			}
		}
		return null;
	}

	/**
	 * Extrait l'année de l'id budget
	 * @param budgetId id budget
	 * @return la valeur de l'année à partir de l'id
	 * @throws BudgetNotFoundException budget introuvable car erreur d'id
	 */
	public static Integer getAnneeFromBudgetId(String budgetId) throws BudgetNotFoundException{
		if(budgetId != null){
			try {
				return Integer.parseInt(budgetId.substring(budgetId.indexOf('_') + 1, budgetId.lastIndexOf('_')));
			}
			catch (Exception e) {
				// Erreur dans l'id
				throw new BudgetNotFoundException("Erreur dans l'id du budget " + budgetId + ". Données incohérentes");
			}
		}
		return null;
	}
	
	

	/**
	 * Raz calculs
	 * @param budget : budget à modifier
	 */
	public static void razCalculs(BudgetMensuel budget){
		budget.getTotauxParCategories().clear();
		budget.getTotauxParSSCategories().clear();
		budget.getSoldes().setSoldeAtMaintenant(budget.getSoldes().getSoldeAtFinMoisPrecedent());
		budget.getSoldes().setSoldeAtFinMoisCourant(budget.getSoldes().getSoldeAtFinMoisPrecedent());
	}
	

	/**
	 * Ajout du solde à fin du mois courant
	 * @param budget  budget à modifier 
	 * @param soldeAAjouter  valeur à ajouter
	 */
	public static void ajouteASoldeNow(BudgetMensuel budget, double soldeAAjouter) {
		budget.getSoldes().setSoldeAtMaintenant(budget.getSoldes().getSoldeAtMaintenant() + soldeAAjouter);
	}

	/**
	 * Ajout du solde à fin du mois courant
	 * @param budget  budget à modifier 
	 * @param soldeAAjouter  valeur à ajouter
	 */
	public static void ajouteASoldeFin(BudgetMensuel budget, double soldeAAjouter) {
		budget.getSoldes().setSoldeAtFinMoisCourant(budget.getSoldes().getSoldeAtFinMoisCourant() + soldeAAjouter);
	}


	/**
	 * Mise à jour des valeurs depuis le mois précédent
	 * @param budget budget à modifier
	 * @param resultatMoisPrecedent the resultatMoisPrecedent to set
	 */
	public static void setResultatMoisPrecedent(BudgetMensuel budget, Double resultatMoisPrecedent) {
		budget.getSoldes().setSoldeAtFinMoisCourant(resultatMoisPrecedent);
		budget.getSoldes().setSoldeAtFinMoisPrecedent(resultatMoisPrecedent);
		budget.getSoldes().setSoldeAtMaintenant(resultatMoisPrecedent);
	}
	
	
	/**
	 * Clone d'une ligne opération
	 * @return Ligne dépense clonée
	 * @param ligneOperation : ligneOpérations à cloner
	 */
	public static LigneOperation cloneDepenseToMoisSuivant(LigneOperation ligneOperation) {
		LigneOperation ligneOperationClonee = new LigneOperation();
		ligneOperationClonee.setId(UUID.randomUUID().toString());
		ligneOperationClonee.setLibelle(ligneOperation.getLibelle());
		if(ligneOperation.getCategorie() != null) {
			LigneOperation.Categorie cat = new LigneOperation.Categorie();
			cat.setId(ligneOperation.getCategorie().getId());
			cat.setLibelle(ligneOperation.getCategorie().getLibelle());
			ligneOperationClonee.setCategorie(cat);
		}
		if(ligneOperation.getSsCategorie() != null) {
			LigneOperation.Categorie ssCatClonee = new LigneOperation.Categorie();
			ssCatClonee.setId(ligneOperation.getSsCategorie().getId());
			ssCatClonee.setLibelle(ligneOperation.getSsCategorie().getLibelle());
			ligneOperationClonee.setSsCategorie(ssCatClonee);
		}
		ligneOperationClonee.setAutresInfos(new LigneOperation.AddInfos());
		ligneOperationClonee.getAutresInfos().setDateMaj(LocalDateTime.now());
		ligneOperationClonee.getAutresInfos().setDateOperation(null);
		ligneOperationClonee.setEtat(EtatOperationEnum.PREVUE);
		ligneOperationClonee.setPeriodique(ligneOperation.isPeriodique());
		ligneOperationClonee.setTypeOperation(ligneOperation.getTypeOperation());
		ligneOperationClonee.setValeurFromSaisie(Math.abs(ligneOperation.getValeur()));
		ligneOperationClonee.setTagDerniereOperation(false);
		return ligneOperationClonee;
	}
	
	
	
	/**
	 * @param listeOperations liste des opérations
	 * @return date max d'une liste de dépenses
	 */
	public static LocalDate getMaxDateListeOperations(List<LigneOperation> listeOperations){

		LocalDate localDateDerniereOperation = BudgetDateTimeUtils.localDateNow();

		if(listeOperations != null && !listeOperations.isEmpty()){
			// Comparaison de date
			
			Comparator <LigneOperation> comparator = Comparator.comparing(LigneOperation::getDateOperation, (date1, date2) -> {
				if(date1 == null){
					return 1;
				}
				else if(date2 == null){
					return -1;
				} else if (date1.equals(date2)){
					return 0;
				} else{
					return date1.isBefore(date2) ? -1 : 1;
				}
			});
			Optional<LigneOperation> maxDate = listeOperations.stream().max(comparator);
			if(maxDate.isPresent() && maxDate.get().getDateOperation() != null){
				localDateDerniereOperation = maxDate.get().getDateOperation().toLocalDate();
			}
		}
		return localDateDerniereOperation;
	}


	/**
	 * @param valeurS valeur en String
	 * @return la valeur d'un String en double
	 */
	public static Double getValueFromString(String valeurS){

		if(valeurS != null){
			valeurS = valeurS.replace(",", ".");
			try{
				return Double.valueOf(valeurS);
			}
			catch(Exception e){
				// Erreur de parsing
			}
		}
		return null;
	}



	/**
	 * @param valeurS valeur en String
	 * @return la valeur du String sans le tag [xxx]
	 */
	public static String deleteTagFromString(String valeurS){

		if(valeurS != null){
			return valeurS.replaceAll("\\[.*]", "").trim();
		}
		return null;
	}


	/**
	 * @param id id de la catégorie
	 * @param listeCategories liste des catégories
	 * @return catégorie correspondante
	 */
	public static CategorieOperations getCategorieById(String id, List<CategorieOperations> listeCategories){
		CategorieOperations categorie = null;
		if(id != null && listeCategories != null && !listeCategories.isEmpty()){
			// Recherche parmi les catégories
			Optional<CategorieOperations> cat = listeCategories.parallelStream()
					.filter(c -> id.equals(c.getId()))
					.findFirst();
			if(cat.isPresent()){
				categorie = cat.get();
			}
			// Sinon les sous catégories
			else{
				Optional<CategorieOperations> ssCats = listeCategories.parallelStream()
						.flatMap(c -> c.getListeSSCategories().stream())
						.filter(ss -> id.equals(ss.getId()))
						.findFirst();
				if(ssCats.isPresent()){
					categorie = ssCats.get();
				}
			}
		}
		if(categorie == null) {
			LOGGER.warn("Impossible de trouver une catégorie correspondant à l'id [{}] parmi les catégories", id);
		}
		return categorie;
	}
}
