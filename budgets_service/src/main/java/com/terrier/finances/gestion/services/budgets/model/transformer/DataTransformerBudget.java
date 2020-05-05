/**
 * 
 */
package com.terrier.finances.gestion.services.budgets.model.transformer;

import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.services.budgets.model.BudgetMensuelDTO;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerBudget")
public class DataTransformerBudget implements IDataTransformer<BudgetMensuel, BudgetMensuelDTO> {

	@Autowired @Qualifier("dataTransformerLigneDepense")
	private DataTransformerLigneOperation dataTransformerLigneDepense = new DataTransformerLigneOperation();

	/**
	 * Constructeur pour Spring
	 */
	public DataTransformerBudget(){
		// Constructeur pour Spring
	}

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerBudget.class);

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformDTOtoBO(java.lang.Object)
	 */
	@Override
	public BudgetMensuel transformDTOtoBO(BudgetMensuelDTO dto) {
		try{
			BudgetMensuel bo = new BudgetMensuel();
			bo.setActif(dto.isActif());
			bo.setAnnee(dto.getAnnee());
			bo.setCompteBancaire(dto.getCompteBancaire());
			if(dto.getDateMiseAJour() != null){
				Calendar c = Calendar.getInstance();
				c.setTime(dto.getDateMiseAJour());
				bo.setDateMiseAJour(c);
			}
			bo.setListeOperations(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses()));
			bo.setMois(Month.of(dto.getMois() + 1));
			bo.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? Double.valueOf(dto.getResultatMoisPrecedent()) : 0D);

			/*
			 * Budget clos : utilisation des valeurs calculées
			 */
			if(!bo.isActif()){
				transformDTOClostoBO(dto, bo);
			}
			bo.setId(dto.getId());

			LOGGER.trace("	[{}] \n > Transformation en BO > [{}]", dto, bo);
			return bo;
		}
		catch(Exception e){
			LOGGER.debug("	[{}] \n > Erreur lors de la transformation en BO > [{}]", dto, null, e);
			return null;			
		}
	}

	
	/**
	 * Transforme DTO d'un budget clos
	 * @param dto dto à transformer
	 * @param bo bo à afficher
	 */
	private void transformDTOClostoBO(BudgetMensuelDTO dto, BudgetMensuel bo) {
		bo.setSoldeNow(dto.getNowArgentAvance() != null ? Double.valueOf(dto.getNowArgentAvance()) : 0);
		bo.setSoldeFin(dto.getFinArgentAvance() != null ? Double.valueOf(dto.getFinArgentAvance()): 0);
		// Complétion des totaux
		bo.setTotalParCategories(calculTotalBoParCategories(dto.getTotalParCategories()));
		// Complétion des totaux ss catégorie
		bo.setTotalParSSCategories(calculTotalBoParCategories(dto.getTotalParSSCategories()));
	}
	
	/**
	 * @param totalParCategories
	 * @return les totaux par catégories BO à partir du DTO
	 */
	private Map<String, Double[]> calculTotalBoParCategories(Map<String, String[]> totalParCategories) {
		Map<String, Double[]> totalParCategorieBO = new HashMap<>();
		if(totalParCategories != null){
			totalParCategories.entrySet()
			.parallelStream()
			.forEach(entry -> {
				if(entry.getKey() != null){
					Double[] totauxBO = new Double[entry.getValue().length];
					for (int i = 0; i < entry.getValue().length; i++) {
						totauxBO[i] = entry.getValue()[i] != null ? Double.valueOf(entry.getValue()[i]) : 0D;
					}
					totalParCategorieBO.put(entry.getKey(), totauxBO);
				}
			});
		}
		return totalParCategorieBO;
	}
	
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformBOtoDTO(java.lang.Object)
	 */
	@Override
	public BudgetMensuelDTO transformBOtoDTO(BudgetMensuel bo) {
		BudgetMensuelDTO dto = new BudgetMensuelDTO();
		dto.setActif(bo.isActif());
		dto.setAnnee(bo.getAnnee());
		dto.setCompteBancaire(bo.getCompteBancaire());
		dto.getCompteBancaire().setListeProprietaires(null);
		dto.setDateMiseAJour(bo.getDateMiseAJour() != null ? bo.getDateMiseAJour().getTime() : null);
		dto.setListeDepenses(dataTransformerLigneDepense.transformBOtoDTO(bo.getListeOperations()));
		
		dto.setMois(bo.getMois().getValue() - 1);
		dto.setResultatMoisPrecedent(bo.getMoisPrecedentResultat() != null ? bo.getMoisPrecedentResultat().toString() : null);
		dto.setFinArgentAvance( String.valueOf(bo.getSoldeFin()));
		dto.setNowArgentAvance( String.valueOf(bo.getSoldeNow()));

		// Complétion des totaux
		dto.setTotalParCategories(calculTotalDTOParCategories(bo.getTotalParCategories()));
		// Complétion des totaux ss catégorie
		
		dto.setTotalParSSCategories(calculTotalDTOParCategories(bo.getTotalParSSCategories()));

		dto.setId();
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}
	
	/**
	 * @param totalParCategories
	 * @return les totaux par catégories BO à partir du DTO
	 */
	private Map<String, String[]> calculTotalDTOParCategories(Map<String, Double[]> totalParCategories) {
		Map<String, String[]> totalSsCategorieDTO = new HashMap<>();
		if(totalParCategories != null){
			totalParCategories.entrySet()
			.parallelStream()
			.forEach(entry -> {
				String[] totauxDTO = new String[entry.getValue().length];
				for (int i = 0; i < entry.getValue().length; i++) {
					totauxDTO[i] = entry.getValue()[i] != null ? entry.getValue()[i].toString() : null;
				}
				totalSsCategorieDTO.put(entry.getKey(), totauxDTO);
			});
		}
		return totalSsCategorieDTO;
	}

	/**
	 * @return the dataTransformerLigneDepense
	 */
	public final DataTransformerLigneOperation getDataTransformerLigneDepense() {
		return dataTransformerLigneDepense;
	}


	/**
	 * @param dataTransformerLigneDepense the dataTransformerLigneDepense to set
	 */
	public final void setDataTransformerLigneDepense(DataTransformerLigneOperation dataTransformerLigneDepense) {
		this.dataTransformerLigneDepense = dataTransformerLigneDepense;
	}
}
