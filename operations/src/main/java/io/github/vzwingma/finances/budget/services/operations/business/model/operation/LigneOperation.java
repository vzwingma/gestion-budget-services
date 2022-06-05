package io.github.vzwingma.finances.budget.services.operations.business.model.operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.vzwingma.finances.budget.services.communs.data.abstrait.AbstractAPIObjectModel;
import io.github.vzwingma.finances.budget.services.communs.data.model.CategorieOperations;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
@Getter @Setter @NoArgsConstructor
public class LigneOperation extends AbstractAPIObjectModel implements Comparable<LigneOperation> {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -2932267709864103657L;
	// Id
	@Schema(description = "Identifiant de l'opération")
	private String id;

	// Libellé
	@Schema(description = "Libellé")
	private String libelle;

	// Catégorie
	@Schema(description = "Catégorie")
	private Categorie categorie;
	@Schema(description = "Sous catégorie")
	private Categorie ssCategorie;
	
	// Type de dépense
	@Schema(description = "Type de dépense")
	private TypeOperationEnum typeOperation;
	// Etat de la ligne
	@Schema(description = "Etat de l'opération")
	private EtatOperationEnum etat;
	// Valeur
	@Schema(description = "Valeur")
	private Double valeur;

	// Périodique
	@Schema(description = "Opération périodique ?")
	private boolean periodique; 
	// tag comme dernière opération
	@Schema(description = "Dernier opération ?")
	private boolean tagDerniereOperation;

	@Schema(description = "Autres infos")
	private LigneOperation.AddInfos autresInfos;

    @Getter @Setter @NoArgsConstructor
	@Schema(description = "Catégorie")
	public static class Categorie implements Serializable{

		@Serial
		private static final long serialVersionUID = -3703948740885489277L;

		@Schema(description = "Id de la catégorie")
		private String id;
		// Libelle
		@Schema(description = "Libellé")
		private String libelle;
		@Override
		public String toString() {
			return libelle;
		}
	}
	
	
	@Getter @Setter @NoArgsConstructor
	@Schema(description = "Données additionnelles")
	public static class AddInfos implements Serializable{
		@Serial
		private static final long serialVersionUID = -3109473021774203805L;
		// Date Creation
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@Schema(description = "Date de création")
		private LocalDateTime dateCreate;		
		// Date validation de l'operation
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@Schema(description = "Date de validation")
		private LocalDateTime dateOperation;
		// Date mise à jour
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@Schema(description = "Date de mise à jour")
		private LocalDateTime dateMaj;
		// Auteur MAJ
		@Schema(description = "Auteur")
		private String auteur;
	}

	
	/**
	 * Constructeur
	 * @param ssCategorie Catégorie
	 * @param libelle libellé
	 * @param typeDepense type d'opération
	 * @param absValeur valeur montant en valeur absolue
	 * @param etat état
	 * @param periodique périodicité de l'opération
	 */
	public LigneOperation(CategorieOperations ssCategorie, String libelle, TypeOperationEnum typeDepense, Double absValeur, EtatOperationEnum etat, boolean periodique){
		Categorie c = null;
		Categorie ssc = null;
		if(ssCategorie != null && ssCategorie.getCategorieParente() != null) {
			c = new Categorie();
			c.setId(ssCategorie.getCategorieParente().getId());
			c.setLibelle(ssCategorie.getCategorieParente().getLibelle());
			setCategorie(c);
		}
		if(ssCategorie != null) {
			ssc = new Categorie();
			ssc.setId(ssCategorie.getId());
			ssc.setLibelle(ssCategorie.getLibelle());
			setSsCategorie(ssc);
		}
		buidLigneOperation(c, ssc, libelle, typeDepense, absValeur, etat, periodique);
	}
	
	

	/**
	 * Constructeur
	 * @param categorie Catégorie
	 * @param ssCategorie Sous Catégorie
	 * @param libelle libellé
	 * @param typeDepense type d'opération
	 * @param absValeur valeur montant en valeur absolue
	 * @param etat état
	 * @param periodique périodicité de l'opération
	 */
	public LigneOperation(Categorie categorie, Categorie ssCategorie, String libelle, TypeOperationEnum typeDepense, Double absValeur, EtatOperationEnum etat, boolean periodique){
		buidLigneOperation(categorie, ssCategorie, libelle, typeDepense, absValeur, etat, periodique);
	}
	
	/**
	 * Constructeur
	 * @param categorie Catégorie
	 * @param ssCategorie Sous Catégorie
	 * @param libelle libellé
	 * @param typeDepense type d'opération
	 * @param absValeur valeur montant en valeur absolue
	 * @param etat état
	 * @param periodique périodicité de l'opération
	 */
	private void buidLigneOperation(Categorie categorie, Categorie ssCategorie, String libelle, TypeOperationEnum typeDepense, Double absValeur, EtatOperationEnum etat, boolean periodique){
		this.id = UUID.randomUUID().toString();
		this.libelle = libelle;
		this.typeOperation = typeDepense;
		this.periodique = periodique;
		setValeurFromSaisie(absValeur);
		this.etat = etat;
		this.tagDerniereOperation = false;

		setCategorie(categorie);
		setSsCategorie(ssCategorie);

		AddInfos addInfos = new AddInfos();
		addInfos.setDateMaj(LocalDateTime.now());
		addInfos.setDateOperation(LocalDateTime.now());
		addInfos.setDateCreate(LocalDateTime.now());
        // Autres infos
		this.autresInfos = addInfos;
    }
	
	/**
	 * @param valeurD : Valeur depuis la saisie (en décimal)
	 */
	@JsonIgnore
	public void setValeurFromSaisie(Double valeurD){
		if(valeurD != null){
			this.valeur = Math.abs(valeurD) * (TypeOperationEnum.DEPENSE.equals(this.getTypeOperation()) ? -1 : 1);
		}
	}
	
	@JsonIgnore
	public Double getValeurToSaisie() {
		return Math.abs(this.valeur);
	}
	
	@JsonIgnore
	public String getAuteur() {
		return getAutresInfos() != null ? getAutresInfos().getAuteur(): "";
	}
	
	/**
	 * @return dateMaj
	 */
	@JsonIgnore
	public LocalDateTime getDateMaj() {
		return getAutresInfos() != null ? getAutresInfos().getDateMaj() : null;
	}	/**
	 * @return dateOpération
	 */
	@JsonIgnore
	public LocalDateTime getDateOperation() {
		return getAutresInfos() != null ? getAutresInfos().getDateOperation() : null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return String.format("LigneOperations [id=%s, categorie=%s, sous-categorie=%s, libelle=%s, typeDepense=%s, etat=%s, valeur=%s, periodique=%s, derniereOperation=%s]"
				, id, categorie, ssCategorie, libelle, typeOperation, etat, valeur, periodique, tagDerniereOperation);
	}

	@Override
	public int compareTo(LigneOperation o) {
		if(o != null){
			LocalDateTime dateC = this.getAutresInfos() != null && this.getAutresInfos().getDateCreate() != null ? 
										this.getAutresInfos().getDateCreate() : LocalDateTime.MIN;
			LocalDateTime dateCo = o.getAutresInfos() != null && o.getAutresInfos().getDateCreate() != null ? 
										o.getAutresInfos().getDateCreate() : LocalDateTime.MIN;
			return dateC.compareTo(dateCo);
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LigneOperation that = (LigneOperation) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
