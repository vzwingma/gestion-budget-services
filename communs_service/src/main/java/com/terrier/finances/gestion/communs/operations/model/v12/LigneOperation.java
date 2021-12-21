package com.terrier.finances.gestion.communs.operations.model.v12;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 
 * Ligne de dépense dans un budget mensuel
 * @author vzwingma
 *
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false)
public class LigneOperation extends AbstractAPIObjectModel implements Comparable<LigneOperation> {

	/**
	 * 
	 */
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
	public class Categorie implements Serializable{

		private static final long serialVersionUID = -3703948740885489277L;
		@Id
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
	public class AddInfos implements Serializable{
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
	public LigneOperation(CategorieOperation ssCategorie, String libelle, TypeOperationEnum typeDepense, Double absValeur, EtatOperationEnum etat, boolean periodique){
		Categorie c = null;
		Categorie ssc = null;
		if(ssCategorie != null && ssCategorie.getCategorieParente() != null) {
			c = this.new Categorie();
			c.setId(ssCategorie.getCategorieParente().getId());
			c.setLibelle(ssCategorie.getCategorieParente().getLibelle());
			setCategorie(c);
		}
		if(ssCategorie != null) {
			ssc = this.new Categorie();
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

		AddInfos addInfos = this.new AddInfos();
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
		builder.append("LigneDepense [id=").append(id)
				.append(", categorie=").append(categorie)
				.append(", SSCategorie=").append(ssCategorie)
				.append(", libelle=").append(libelle).append(", typeDepense=").append(typeOperation)
				.append(", etat=").append(etat).append(", valeur=")
				.append(valeur).append(", periodique=")
				.append(periodique).append(", derniereOperation=")
				.append(tagDerniereOperation).append("]");
		return builder.toString();
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
}
