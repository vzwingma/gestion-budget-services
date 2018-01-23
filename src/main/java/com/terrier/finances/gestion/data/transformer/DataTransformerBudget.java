/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

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

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.IDataTransformer;
import com.terrier.finances.gestion.model.business.budget.BudgetMensuel;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.data.budget.BudgetMensuelDTO;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerBudget")
public class DataTransformerBudget extends IDataTransformer<BudgetMensuel, BudgetMensuelDTO> {

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
	public BudgetMensuel transformDTOtoBO(BudgetMensuelDTO dto) {
		
		BasicTextEncryptor decryptor = getEncryptor();
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
			bo.setListeDepenses(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses()));
			bo.setMargeSecurite(dto.getMargeSecurite() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeSecurite())) : 0D);
			bo.setMargeSecuriteFinMois(dto.getMargeSecuriteFinMois() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeSecuriteFinMois())) : 0D);
			bo.setMois(Month.of(dto.getMois() + 1));
			bo.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? Double.valueOf(decryptor.decrypt(dto.getResultatMoisPrecedent())) : 0D);
			
			/*
			 * Budget clos : utilisation des valeurs calculées
			 */
			if(!bo.isActif()){
				bo.setNowArgentAvance(dto.getNowArgentAvance() != null ? Double.valueOf(decryptor.decrypt(dto.getNowArgentAvance())) : 0);
				bo.setNowCompteReel(dto.getNowCompteReel() != null ? Double.valueOf(decryptor.decrypt(dto.getNowCompteReel())): 0);
				bo.setFinArgentAvance(dto.getFinArgentAvance() != null ? Double.valueOf(decryptor.decrypt(dto.getFinArgentAvance())): 0);
				bo.setFinCompteReel(dto.getFinCompteReel() != null ? Double.valueOf(decryptor.decrypt(dto.getFinCompteReel())):0);
				// Complétion des totaux
				Map<CategorieDepense, Double[]> totalCategorieBO = new HashMap<CategorieDepense, Double[]>();
				if(dto.getTotalParCategories() != null){
					for (String catKey : dto.getTotalParCategories().keySet()) {
						String[] totaux = dto.getTotalParCategories().get(catKey);
						Double[] totauxBO = new Double[totaux.length];
						for (int i = 0; i < totaux.length; i++) {
							totauxBO[i] = totaux[i] != null ? Double.valueOf(decryptor.decrypt(totaux[i])) : 0D;
						}
						try {
							totalCategorieBO.put(parametrageService.chargeCategorieParId(decryptor.decrypt(catKey)), totauxBO);
						} catch (DataNotFoundException e) {
							LOGGER.error("Impossible de trouver la catégorie {}", decryptor.decrypt(catKey));
						}
					}
				}
				bo.setTotalParCategories(totalCategorieBO);
				// Complétion des totaux ss catégorie
				Map<CategorieDepense, Double[]> totalSsCategorieBO = new HashMap<CategorieDepense, Double[]>();
				if(dto.getTotalParSSCategories() != null){
					for (String ssCatKey : dto.getTotalParSSCategories().keySet()) {
						String[] totaux = dto.getTotalParSSCategories().get(ssCatKey);
						Double[] totauxBO = new Double[totaux.length];
						for (int i = 0; i < totaux.length; i++) {
							totauxBO[i] = totaux[i] != null ? Double.valueOf(decryptor.decrypt(totaux[i])) : 0D;
						}
						try {
							totalSsCategorieBO.put(parametrageService.chargeCategorieParId(decryptor.decrypt(ssCatKey)), totauxBO);
						} catch (DataNotFoundException e) {
							LOGGER.error("Impossible de trouver la catégorie {}", decryptor.decrypt(ssCatKey));
						}
					}
				}
				bo.setTotalParSSCategories(totalSsCategorieBO);
			}
			bo.setId(dto.getId());
			
			LOGGER.debug("	[{}] \n > Transformation en BO > [{}]", dto, bo);
			return bo;
		}
		catch(Exception e){
			LOGGER.debug("	[{}] \n > Erreur lors de la transformation en BO > [{}]", dto, null);
			return null;			
		}
	}


	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformBOtoDTO(java.lang.Object)
	 */
	@Override
	public BudgetMensuelDTO transformBOtoDTO(BudgetMensuel bo) {
		
		BasicTextEncryptor encrytor = getEncryptor();
		
		BudgetMensuelDTO dto = new BudgetMensuelDTO();
		dto.setActif(bo.isActif());
		dto.setAnnee(bo.getAnnee());
		dto.setCompteBancaire(bo.getCompteBancaire());
		dto.getCompteBancaire().setListeProprietaires(null);
		dto.setDateMiseAJour(bo.getDateMiseAJour() != null ? bo.getDateMiseAJour().getTime() : null);
		dto.setListeDepenses(dataTransformerLigneDepense.transformBOtoDTO(bo.getListeDepenses()));
		dto.setMargeSecurite(bo.getMargeSecurite() != null ? encrytor.encrypt(bo.getMargeSecurite().toString()) : null);
		dto.setMargeSecuriteFinMois(bo.getMargeSecuriteFinMois() != null ?  encrytor.encrypt(bo.getMargeSecuriteFinMois().toString()) : null);

		dto.setMois(bo.getMois().getValue() - 1);
		dto.setResultatMoisPrecedent(bo.getResultatMoisPrecedent() != null ?  encrytor.encrypt(bo.getResultatMoisPrecedent().toString()) : null);
		dto.setFinArgentAvance( encrytor.encrypt(String.valueOf(bo.getFinArgentAvance())));
		dto.setFinCompteReel( encrytor.encrypt(String.valueOf(bo.getFinCompteReel())));
		dto.setNowArgentAvance( encrytor.encrypt(String.valueOf(bo.getNowArgentAvance())));
		dto.setNowCompteReel( encrytor.encrypt(String.valueOf(bo.getNowCompteReel())));

		// Complétion des totaux
		Map<String, String[]> totalCategorieDTO = new HashMap<String, String[]>();
		for (CategorieDepense catKey : bo.getTotalParCategories().keySet()) {
			Double[] totaux = bo.getTotalParCategories().get(catKey);
			String[] totauxDTO = new String[totaux.length];
			for (int i = 0; i < totaux.length; i++) {
				totauxDTO[i] = totaux[i] != null ? encrytor.encrypt(totaux[i].toString()) : null;
			}
			totalCategorieDTO.put(encrytor.encrypt(catKey.getId()), totauxDTO);
		}
		dto.setTotalParCategories(totalCategorieDTO);
		// Complétion des totaux ss catégorie
		Map<String, String[]> totalSsCategorieDTO = new HashMap<String, String[]>();
		for (CategorieDepense ssCatKey : bo.getTotalParSSCategories().keySet()) {
			Double[] totaux = bo.getTotalParSSCategories().get(ssCatKey);
			String[] totauxDTO = new String[totaux.length];
			for (int i = 0; i < totaux.length; i++) {
				totauxDTO[i] = totaux[i] != null ? encrytor.encrypt(totaux[i].toString()) : null;
			}
			totalSsCategorieDTO.put(encrytor.encrypt(ssCatKey.getId()), totauxDTO);
		}
		dto.setTotalParSSCategories(totalSsCategorieDTO);

		dto.setId(bo.getId());
		LOGGER.debug("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}
}
