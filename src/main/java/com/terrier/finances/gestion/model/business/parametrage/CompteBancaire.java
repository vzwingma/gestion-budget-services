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
	
	private boolean defaut = false;

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
	 * @return the defaut
	 */
	public boolean isDefaut() {
		return defaut;
	}

	/**
	 * @param defaut the defaut to set
	 */
	public void setDefaut(boolean defaut) {
		this.defaut = defaut;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CompteBancaire [id=" + id + ", libelle=" + libelle
				+ ", listeProprietaires=" + listeProprietaires + ", itemIcon="
				+ itemIcon + "]";
	}
	
	

}
