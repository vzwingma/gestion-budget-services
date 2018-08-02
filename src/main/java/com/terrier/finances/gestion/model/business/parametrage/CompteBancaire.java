/**
 * 
 */
package com.terrier.finances.gestion.model.business.parametrage;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Compte bancaire
 * @author vzwingma
 *
 */
@Document(collection = "comptesbancaires")
public class CompteBancaire implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -846392155444814540L;

	@Id
	private String id;
		
	// Libellé du compte
	private String libelle;
	// Liste des propriétaires du compte
	@JsonIgnore
	private List<Utilisateur> listeProprietaires;
	// Icone
	private String itemIcon;
	// N° d'ordre
	private int ordre;
	// closed
	private Boolean actif;
	

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	/**
	 * @return the listeProprietaires
	 */
	public List<Utilisateur> getListeProprietaires() {
		return listeProprietaires;
	}

	/**
	 * @param listeProprietaires the listeProprietaires to set
	 */
	public void setListeProprietaires(List<Utilisateur> listeProprietaires) {
		this.listeProprietaires = listeProprietaires;
	}

	/**
	 * @return the itemIcon
	 */
	public String getItemIcon() {
		return itemIcon;
	}

	/**
	 * @param itemIcon the itemIcon to set
	 */
	public void setItemIcon(String itemIcon) {
		this.itemIcon = itemIcon;
	}
	
	

	

	/**
	 * @return the actif
	 */
	public Boolean isActif() {
		// Vrai par défaut
		return actif != null ? actif : Boolean.TRUE;
	}

	/**
	 * @param actif the actif to set
	 */
	public void setActif(Boolean actif) {
		this.actif = actif;
	}

	/**
	 * @return the ordre
	 */
	public int getOrdre() {
		return ordre;
	}

	/**
	 * @param ordre the ordre to set
	 */
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompteBancaire [id=").append(id).append(", libelle=").append(libelle).append("]");
		return builder.toString();
	}
	
	

}
