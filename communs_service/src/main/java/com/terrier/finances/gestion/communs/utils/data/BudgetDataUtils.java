package com.terrier.finances.gestion.communs.utils.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.communs.budget.model.v12.BudgetMensuel;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.v12.LigneOperation.Categorie;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import com.terrier.finances.gestion.communs.utils.exceptions.BudgetNotFoundException;

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
		return new StringBuilder().append(idCompte).append("_").append(annee).append("_").append(String.format("%02d", mois.getValue())).toString();
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
			Categorie cat = ligneOperationClonee.new Categorie();
			cat.setId(ligneOperation.getCategorie().getId());
			cat.setLibelle(ligneOperation.getCategorie().getLibelle());
			ligneOperationClonee.setCategorie(cat);
		}
		if(ligneOperation.getSsCategorie() != null) {
			Categorie ssCatClonee = ligneOperationClonee.new Categorie();
			ssCatClonee.setId(ligneOperation.getSsCategorie().getId());
			ssCatClonee.setLibelle(ligneOperation.getSsCategorie().getLibelle());
			ligneOperationClonee.setSsCategorie(ssCatClonee);
		}
		ligneOperationClonee.setAutresInfos(ligneOperationClonee.new AddInfos());
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
				}
				else{
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
	 * @param id id de la catégorie
	 * @param listeCategories liste des catégories
	 * @return catégorie correspondante
	 */
	public static CategorieOperation getCategorieById(String id, List<CategorieOperation> listeCategories){
		CategorieOperation categorie = null;
		if(id != null && listeCategories != null && !listeCategories.isEmpty()){
			// Recherche parmi les catégories
			Optional<CategorieOperation> cat = listeCategories.parallelStream()
					.filter(c -> id.equals(c.getId()))
					.findFirst();
			if(cat.isPresent()){
				categorie = cat.get();
			}
			// Sinon les sous catégories
			else{
				Optional<CategorieOperation> ssCats = listeCategories.parallelStream()
						.flatMap(c -> c.getListeSSCategories().stream())
						.filter(ss -> id.equals(ss.getId()))
						.findFirst();
				if(ssCats.isPresent()){
					categorie = ssCats.get();
				}
			}
		}
		if(categorie == null) {
			assert listeCategories != null;
			LOGGER.warn("Impossible de trouver une catégorie correspondant à l'id [{}] parmi les {} catégories", id, listeCategories.size());
		}
		return categorie;
	}
}
