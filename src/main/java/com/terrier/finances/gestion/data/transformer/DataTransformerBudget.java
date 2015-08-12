/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

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
public class DataTransformerBudget implements IDataTransformer<BudgetMensuel, BudgetMensuelDTO> {

	@Autowired @Qualifier("dataTransformerLigneDepense")
	private DataTransformerLigneDepense dataTransformerLigneDepense = new DataTransformerLigneDepense();
	@Autowired
	private ParametragesDatabaseService parametrageService;
	/**
	 * Constructeur pour Spring
	 */
	public DataTransformerBudget(){ }

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerBudget.class);

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformDTOtoBO(java.lang.Object)
	 */
	@Override
	public BudgetMensuel transformDTOtoBO(BudgetMensuelDTO dto, BasicTextEncryptor decryptor) {
		BudgetMensuel bo = new BudgetMensuel();
		bo.setActif(dto.isActif());
		bo.setAnnee(dto.getAnnee());
		bo.setCompteBancaire(dto.getCompteBancaire());
		if(dto.getDateMiseAJour() != null){
			Calendar c = Calendar.getInstance();
			c.setTime(dto.getDateMiseAJour());
			bo.setDateMiseAJour(c);
		}
		bo.setListeDepenses(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses(), decryptor));
		bo.setMargeSecurite(dto.getMargeSecurite() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeSecurite())) : 0D);
		bo.setMargeSecuriteFinMois(dto.getMargeSecuriteFinMois() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeSecuriteFinMois())) : 0D);
		bo.setMois(dto.getMois());
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
		return bo;
	}



	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformDTOtoBO(java.lang.Object)
	 */
	public BudgetMensuelDTO decryptDTO(BudgetMensuelDTO dto, BasicTextEncryptor decryptor) {
		//		bo.setListeDepenses(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses(), decryptor));
		dto.setMargeSecurite(dto.getMargeSecurite() != null ? decryptor.decrypt(dto.getMargeSecurite()) : "0");
		dto.setMargeSecuriteFinMois(dto.getMargeSecuriteFinMois() != null ? decryptor.decrypt(dto.getMargeSecuriteFinMois()) : "0");
		dto.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? decryptor.decrypt(dto.getResultatMoisPrecedent()) : "0");

		/*
		 * Budget clos : utilisation des valeurs calculées
		 */
		dto.setNowArgentAvance(dto.getNowArgentAvance() != null ? decryptor.decrypt(dto.getNowArgentAvance()) : "0");
		dto.setNowCompteReel(dto.getNowCompteReel() != null ? decryptor.decrypt(dto.getNowCompteReel()) : "0");
		dto.setFinArgentAvance(dto.getFinArgentAvance() != null ? decryptor.decrypt(dto.getFinArgentAvance()) : "0");
		dto.setFinCompteReel(dto.getFinCompteReel() != null ? decryptor.decrypt(dto.getFinCompteReel()) :"0");

		// Complétion des totaux
		Map<String, String[]> totalCategorieDTO = new HashMap<String, String[]>();
		if(dto.getTotalParCategories() != null){
			for (String catKey : dto.getTotalParCategories().keySet()) {
				String[] totaux = dto.getTotalParCategories().get(catKey);
				String[] totauxBO = new String[totaux.length];
				for (int i = 0; i < totaux.length; i++) {
					totauxBO[i] = totaux[i] != null ? decryptor.decrypt(totaux[i]) : "0";
				}
				totalCategorieDTO.put(decryptor.decrypt(catKey), totauxBO);
			}
		}
		dto.setTotalParCategories(totalCategorieDTO);
		// Complétion des totaux ss catégorie
		Map<String, String[]> totalSsCategorieDTO = new HashMap<String, String[]>();
		if(dto.getTotalParSSCategories() != null){
			for (String ssCatKey : dto.getTotalParSSCategories().keySet()) {
				String[] totaux = dto.getTotalParSSCategories().get(ssCatKey);
				String[] totauxBO = new String[totaux.length];
				for (int i = 0; i < totaux.length; i++) {
					totauxBO[i] = totaux[i] != null ? decryptor.decrypt(totaux[i]) : "0";
				}
				totalSsCategorieDTO.put(decryptor.decrypt(ssCatKey), totauxBO);
			}
		}
		dto.setTotalParSSCategories(totalSsCategorieDTO);
		return dto;
	}

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.AbstractTransformer#transformBOtoDTO(java.lang.Object)
	 */
	@Override
	public BudgetMensuelDTO transformBOtoDTO(BudgetMensuel bo, BasicTextEncryptor encrytor) {
		BudgetMensuelDTO dto = new BudgetMensuelDTO();
		dto.setActif(bo.isActif());
		dto.setAnnee(bo.getAnnee());
		dto.setCompteBancaire(bo.getCompteBancaire());
		dto.setDateMiseAJour(bo.getDateMiseAJour() != null ? bo.getDateMiseAJour().getTime() : null);
		dto.setListeDepenses(dataTransformerLigneDepense.transformBOtoDTO(bo.getListeDepenses(), encrytor));
		dto.setMargeSecurite(bo.getMargeSecurite() != null ? encrytor.encrypt(bo.getMargeSecurite().toString()) : null);
		dto.setMargeSecuriteFinMois(bo.getMargeSecuriteFinMois() != null ?  encrytor.encrypt(bo.getMargeSecuriteFinMois().toString()) : null);
		dto.setMois(bo.getMois());
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
		return dto;
	}
}
