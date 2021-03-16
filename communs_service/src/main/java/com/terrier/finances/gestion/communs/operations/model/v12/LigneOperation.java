package com.terrier.finances.gestion.communs.operations.model.v12;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private String id;

	// Libellé
	private String libelle;

	// Catégorie
	private Categorie categorie;
	private Categorie ssCategorie;
	
	// Type de dépense
	private TypeOperationEnum typeOperation;
	// Etat de la ligne
	private EtatOperationEnum etat;
	// Valeur
	private Double valeur;

	// Périodique
	private boolean periodique; 
	// tag comme dernière opération
	private boolean tagDerniereOperation;

	private LigneOperation.AddInfos autresInfos;

    @Getter @Setter @NoArgsConstructor
	public class Categorie implements Serializable{

		private static final long serialVersionUID = -3703948740885489277L;
		@Id
		private String id;
		// Libelle
		private String libelle;
		@Override
		public String toString() {
			return libelle;
		}
	}
	
	
	@Getter @Setter @NoArgsConstructor
	public class AddInfos implements Serializable{
		private static final long serialVersionUID = -3109473021774203805L;
		// Date Creation
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		private LocalDateTime dateCreate;		
		// Date validation de l'operation
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		private LocalDateTime dateOperation;
		// Date mise à jour
		@JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		private LocalDateTime dateMaj;
		// Auteur MAJ
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
