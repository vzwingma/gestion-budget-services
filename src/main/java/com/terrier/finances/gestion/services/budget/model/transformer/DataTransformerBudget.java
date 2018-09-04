/**
 * 
 */
package com.terrier.finances.gestion.services.budget.model.transformer;

import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.communs.budget.model.BudgetMensuel;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieDepense;
import com.terrier.finances.gestion.services.budget.model.BudgetMensuelDTO;
import com.terrier.finances.gestion.services.parametrages.data.ParametragesDatabaseService;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerBudget")
public class DataTransformerBudget implements IDataTransformer<BudgetMensuel, BudgetMensuelDTO> {

	@Autowired @Qualifier("dataTransformerLigneDepense")
	private DataTransformerLigneDepense dataTransformerLigneDepense = new DataTransformerLigneDepense();
	@Autowired
	private ParametragesDatabaseService parametrageService;
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
	public BudgetMensuel transformDTOtoBO(BudgetMensuelDTO dto, BasicTextEncryptor decryptor) {
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
			bo.setListeOperations(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses(), decryptor));
			bo.setMois(Month.of(dto.getMois() + 1));
			bo.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? Double.valueOf(decryptor.decrypt(dto.getResultatMoisPrecedent())) : 0D, dto.getMargeMoisPrecedent() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeMoisPrecedent())) : 0D);

			/*
			 * Budget clos : utilisation des valeurs calculées
			 */
			if(!bo.isActif()){
				bo.setSoldeNow(dto.getNowArgentAvance() != null ? Double.valueOf(decryptor.decrypt(dto.getNowArgentAvance())) : 0);
				bo.setSoldeFin(dto.getFinArgentAvance() != null ? Double.valueOf(decryptor.decrypt(dto.getFinArgentAvance())): 0);
				// Complétion des totaux
				Map<CategorieDepense, Double[]> totalCategorieBO = new HashMap<>();

				if(dto.getTotalParCategories() != null){
					dto.getTotalParCategories().entrySet()
					.parallelStream()
					.forEach(entry -> {
						CategorieDepense c = getCategorieByEncryptedId(entry.getKey(), decryptor);
						if(c != null){
							Double[] totauxBO = new Double[entry.getValue().length];
							for (int i = 0; i < entry.getValue().length; i++) {
								totauxBO[i] = entry.getValue()[i] != null ? Double.valueOf(decryptor.decrypt(entry.getValue()[i])) : 0D;
							}
							totalCategorieBO.put(c, totauxBO);
						}
					});
				}
				bo.setTotalParCategories(totalCategorieBO);


				// Complétion des totaux ss catégorie
				Map<CategorieDepense, Double[]> totalSsCategorieBO = new HashMap<>();
				if(dto.getTotalParSSCategories() != null){
					dto.getTotalParSSCategories().entrySet()
					.parallelStream()
					.forEach(entry -> {
						CategorieDepense ssC = getCategorieByEncryptedId(entry.getKey(), decryptor);
						if(ssC != null){
							Double[] totauxBO = new Double[entry.getValue().length];
							for (int i = 0; i < entry.getValue().length; i++) {
								totauxBO[i] = entry.getValue()[i] != null ? Double.valueOf(decryptor.decrypt(entry.getValue()[i])) : 0D;
							}
							totalSsCategorieBO.put(ssC, totauxBO);
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


	private CategorieDepense getCategorieByEncryptedId(String encryptedId, BasicTextEncryptor decryptor){
		return parametrageService.chargeCategorieParId(decryptor.decrypt(encryptedId));
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformBOtoDTO(java.lang.Object)
	 */
	@Override
	public BudgetMensuelDTO transformBOtoDTO(BudgetMensuel bo, BasicTextEncryptor encryptor) {
		BudgetMensuelDTO dto = new BudgetMensuelDTO();
		dto.setActif(bo.isActif());
		dto.setAnnee(bo.getAnnee());
		dto.setCompteBancaire(bo.getCompteBancaire());
		dto.getCompteBancaire().setListeProprietaires(null);
		dto.setDateMiseAJour(bo.getDateMiseAJour() != null ? bo.getDateMiseAJour().getTime() : null);
		dto.setListeDepenses(dataTransformerLigneDepense.transformBOtoDTO(bo.getListeOperations(), encryptor));
		
		dto.setMois(bo.getMois().getValue() - 1);
		dto.setResultatMoisPrecedent(bo.getMoisPrecedentResultat() != null ?  encryptor.encrypt(bo.getMoisPrecedentResultat().toString()) : null);
		dto.setMargeMoisPrecedent(bo.getMoisPrecedentMarge() != null ? encryptor.encrypt(bo.getMoisPrecedentMarge().toString()) : null);

		
		dto.setFinArgentAvance( encryptor.encrypt(String.valueOf(bo.getSoldeFin())));
		dto.setNowArgentAvance( encryptor.encrypt(String.valueOf(bo.getSoldeNow())));

		// Complétion des totaux
		Map<String, String[]> totalCategorieDTO = new HashMap<>();
		if(bo.getTotalParCategories() != null){
			bo.getTotalParCategories().entrySet()
			.parallelStream()
			.forEach(entry -> {
				String[] totauxDTO = new String[entry.getValue().length];
				for (int i = 0; i < entry.getValue().length; i++) {
					totauxDTO[i] = entry.getValue()[i] != null ? encryptor.encrypt(entry.getValue()[i].toString()) : null;
				}
				totalCategorieDTO.put(encryptor.encrypt(entry.getKey().getId()), totauxDTO);
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
					totauxDTO[i] = entry.getValue()[i] != null ? encryptor.encrypt(entry.getValue()[i].toString()) : null;
				}
				totalSsCategorieDTO.put(encryptor.encrypt(entry.getKey().getId()), totauxDTO);
			});
		}
		dto.setTotalParSSCategories(totalSsCategorieDTO);

		dto.setId(bo.getId());
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}


	/**
	 * @return the dataTransformerLigneDepense
	 */
	public final DataTransformerLigneDepense getDataTransformerLigneDepense() {
		return dataTransformerLigneDepense;
	}


	/**
	 * @param dataTransformerLigneDepense the dataTransformerLigneDepense to set
	 */
	public final void setDataTransformerLigneDepense(DataTransformerLigneDepense dataTransformerLigneDepense) {
		this.dataTransformerLigneDepense = dataTransformerLigneDepense;
	}


	/**
	 * @param parametrageService the parametrageService to set
	 */
	public final void setParametrageService(ParametragesDatabaseService parametrageService) {
		this.parametrageService = parametrageService;
	}


}
