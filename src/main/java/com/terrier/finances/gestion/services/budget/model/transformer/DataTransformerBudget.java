/**
 * 
 */
package com.terrier.finances.gestion.services.budget.model.transformer;

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
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;

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
			bo.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? Double.valueOf(dto.getResultatMoisPrecedent()) : 0D, dto.getMargeMoisPrecedent() != null ? Double.valueOf(dto.getMargeMoisPrecedent()) : 0D);

			/*
			 * Budget clos : utilisation des valeurs calculées
			 */
			if(!bo.isActif()){
				bo.setSoldeNow(dto.getNowArgentAvance() != null ? Double.valueOf(dto.getNowArgentAvance()) : 0);
				bo.setSoldeFin(dto.getFinArgentAvance() != null ? Double.valueOf(dto.getFinArgentAvance()): 0);
				// Complétion des totaux
				Map<String, Double[]> totalCategorieBO = new HashMap<>();

				if(dto.getTotalParCategories() != null){
					dto.getTotalParCategories().entrySet()
					.parallelStream()
					.forEach(entry -> {
						if(entry.getKey() != null){
							Double[] totauxBO = new Double[entry.getValue().length];
							for (int i = 0; i < entry.getValue().length; i++) {
								totauxBO[i] = entry.getValue()[i] != null ? Double.valueOf(entry.getValue()[i]) : 0D;
							}
							totalCategorieBO.put(entry.getKey(), totauxBO);
						}
					});
				}
				bo.setTotalParCategories(totalCategorieBO);


				// Complétion des totaux ss catégorie
				Map<String, Double[]> totalSsCategorieBO = new HashMap<>();
				if(dto.getTotalParSSCategories() != null){
					dto.getTotalParSSCategories().entrySet()
					.parallelStream()
					.forEach(entry -> {
						if(entry.getKey() != null){
							Double[] totauxBO = new Double[entry.getValue().length];
							for (int i = 0; i < entry.getValue().length; i++) {
								totauxBO[i] = entry.getValue()[i] != null ? Double.valueOf(entry.getValue()[i]) : 0D;
							}
							totalSsCategorieBO.put(entry.getKey(), totauxBO);
						}
					});
				}
				bo.setTotalParSSCategories(totalSsCategorieBO);
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
		dto.setMargeMoisPrecedent(bo.getMoisPrecedentMarge() != null ? bo.getMoisPrecedentMarge().toString() : null);

		
		dto.setFinArgentAvance( String.valueOf(bo.getSoldeFin()));
		dto.setNowArgentAvance( String.valueOf(bo.getSoldeNow()));

		// Complétion des totaux
		Map<String, String[]> totalCategorieDTO = new HashMap<>();
		if(bo.getTotalParCategories() != null){
			bo.getTotalParCategories().entrySet()
			.parallelStream()
			.forEach(entry -> {
				String[] totauxDTO = new String[entry.getValue().length];
				for (int i = 0; i < entry.getValue().length; i++) {
					totauxDTO[i] = entry.getValue()[i] != null ? entry.getValue()[i].toString() : null;
				}
				totalCategorieDTO.put(entry.getKey(), totauxDTO);
			});
		}
		dto.setTotalParCategories(totalCategorieDTO);

		// Complétion des totaux ss catégorie
		Map<String, String[]> totalSsCategorieDTO = new HashMap<>();
		if(bo.getTotalParSSCategories() != null){
			bo.getTotalParSSCategories().entrySet()
			.parallelStream()
			.forEach(entry -> {
				String[] totauxDTO = new String[entry.getValue().length];
				for (int i = 0; i < entry.getValue().length; i++) {
					totauxDTO[i] = entry.getValue()[i] != null ? entry.getValue()[i].toString() : null;
				}
				totalSsCategorieDTO.put(entry.getKey(), totauxDTO);
			});
		}
		dto.setTotalParSSCategories(totalSsCategorieDTO);

		dto.setId();
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
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
