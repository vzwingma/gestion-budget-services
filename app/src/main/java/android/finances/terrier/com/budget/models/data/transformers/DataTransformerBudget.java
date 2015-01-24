/**
 *
 */
package android.finances.terrier.com.budget.models.data.transformers;

import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.models.data.BudgetMensuelDTO;
import android.finances.terrier.com.budget.models.data.CategorieDepenseDTO;
import android.finances.terrier.com.budget.utils.Logger;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataTransformer
 *
 * @author vzwingma
 */
public class DataTransformerBudget implements IDataTransformer<BudgetMensuel, BudgetMensuelDTO> {

    // Logger
    private static final Logger LOGGER = new Logger(DataTransformerBudget.class);
    /**
     * Transformeur de dépenses
     */
    private DataTransformerLigneDepense dataTransformerLigneDepense = new DataTransformerLigneDepense();
    private List<CategorieDepenseDTO> listeCategories;

    /**
     * Constructeur pour Spring
     */
    public void setCategories(List<CategorieDepenseDTO> listeCategories) {
        this.listeCategories = listeCategories;
    }

    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.AbstractTransformer#transformDTOtoBO(java.lang.Object)
     */
    @Override
    public BudgetMensuel transformDTOtoBO(BudgetMensuelDTO dto, BasicTextEncryptor decryptor) {
        BudgetMensuel bo = null;
        try {
            if (dto != null) {
                bo = new BudgetMensuel();
                bo.setActif(dto.isActif());
                bo.setAnnee(dto.getAnnee());
                bo.setCompteBancaire(dto.getCompteBancaire());
                if (dto.getDateMiseAJour() != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dto.getDateMiseAJour());
                    bo.setDateMiseAJour(c);
                }
                // bo.setListeDepenses(dataTransformerLigneDepense.transformDTOtoBO(dto.getListeDepenses(), decryptor));
                bo.setMargeSecurite(Double.valueOf(decryptor.decrypt(dto.getMargeSecurite())));
                bo.setMargeSecuriteFinMois(dto.getMargeSecuriteFinMois() != null ? Double.valueOf(decryptor.decrypt(dto.getMargeSecuriteFinMois())) : 0D);
                bo.setMois(dto.getMois());
                bo.setResultatMoisPrecedent(dto.getResultatMoisPrecedent() != null ? Double.valueOf(decryptor.decrypt(dto.getResultatMoisPrecedent())) : 0D);

                bo.setNowArgentAvance(Double.valueOf(decryptor.decrypt(dto.getNowArgentAvance())));
                bo.setNowCompteReel(Double.valueOf(decryptor.decrypt(dto.getNowCompteReel())));
                bo.setFinArgentAvance(Double.valueOf(decryptor.decrypt(dto.getFinArgentAvance())));
                bo.setFinCompteReel(Double.valueOf(decryptor.decrypt(dto.getFinCompteReel())));

                // Complétion des totaux
                Map<CategorieDepenseDTO, Double[]> totalCategorieBO = new HashMap<>();
                for (String catKey : dto.getTotalParCategories().keySet()) {
                    String[] totaux = dto.getTotalParCategories().get(catKey);
                    Double[] totauxBO = new Double[totaux.length];
                    for (int i = 0; i < totaux.length; i++) {
                        totauxBO[i] = totaux[i] != null ? Double.valueOf(decryptor.decrypt(totaux[i])) : 0D;
                    }
                    totalCategorieBO.put(getCategorie(decryptor.decrypt(catKey)), totauxBO);
                }
                bo.setTotalParCategories(totalCategorieBO);
                // Complétion des totaux ss catégorie
                Map<CategorieDepenseDTO, Double[]> totalSsCategorieBO = new HashMap<>();
                for (String ssCatKey : dto.getTotalParSSCategories().keySet()) {
                    String[] totaux = dto.getTotalParSSCategories().get(ssCatKey);
                    Double[] totauxBO = new Double[totaux.length];
                    for (int i = 0; i < totaux.length; i++) {
                        totauxBO[i] = totaux[i] != null ? Double.valueOf(decryptor.decrypt(totaux[i])) : 0D;
                    }
                    LOGGER.trace("SSCatégorie : " + ssCatKey + "->" + decryptor.decrypt(ssCatKey) + "->" + getCategorie(decryptor.decrypt(ssCatKey)));
                    totalSsCategorieBO.put(getCategorie(decryptor.decrypt(ssCatKey)), totauxBO);
                }
                bo.setTotalParSSCategories(totalSsCategorieBO);

                bo.setId(dto.getId());
            }
        } catch (Exception e) {
            // Rien
        }
        return bo;
    }

    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.AbstractTransformer#transformBOtoDTO(java.lang.Object)
     */
    @Override
    public BudgetMensuelDTO transformBOtoDTO(BudgetMensuel bo, BasicTextEncryptor encrytor) {
        /**
         BudgetMensuelDTO dto = new BudgetMensuelDTO();
         dto.setActif(bo.isActif());
         dto.setAnnee(bo.getAnnee());
         dto.setCompteBancaire(bo.getCompteBancaire());
         dto.setDateMiseAJour(bo.getDateMiseAJour() != null ? bo.getDateMiseAJour().getTime() : null);
         // dto.setListeDepenses(dataTransformerLigneDepense.transformBOtoDTO(bo.getListeDepenses(), encrytor));
         dto.setMargeSecurite(bo.getMargeSecurite() != null ? encrytor.encrypt(bo.getMargeSecurite().toString()) : null);
         dto.setMargeSecuriteFinMois(bo.getMargeSecuriteFinMois() != null ?  encrytor.encrypt(bo.getMargeSecuriteFinMois().toString()) : null);
         dto.setMois(bo.getMois());
         dto.setResultatMoisPrecedent(bo.getResultatMoisPrecedent() != null ?  encrytor.encrypt(bo.getResultatMoisPrecedent().toString()) : null);
         dto.setFinArgentAvance( encrytor.encrypt(String.valueOf(bo.getFinArgentAvance())));
         dto.setFinCompteReel( encrytor.encrypt(String.valueOf(bo.getFinCompteReel())));
         dto.setNowArgentAvance( encrytor.encrypt(String.valueOf(bo.getNowArgentAvance())));
         dto.setNowCompteReel( encrytor.encrypt(String.valueOf(bo.getNowCompteReel())));
         dto.setId(bo.getId());
         return dto;
         **/
        return null;
    }

    /**
     * chargement de la catégorie
     *
     * @param idCategorie
     * @return catégorie
     */
    private CategorieDepenseDTO getCategorie(String idCategorie) {
        for (CategorieDepenseDTO categorie : this.listeCategories) {
            if (categorie.getId().equals(idCategorie)) {
                return categorie;
            }
            for (CategorieDepenseDTO ssCategorie : categorie.getListeSSCategories()) {
                if (ssCategorie.getId().equals(idCategorie)) {
                    return ssCategorie;
                }
            }
        }
        return null;
    }
}
