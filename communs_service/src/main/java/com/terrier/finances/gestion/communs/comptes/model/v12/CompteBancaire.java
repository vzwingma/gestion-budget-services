package com.terrier.finances.gestion.communs.comptes.model.v12;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Compte bancaire
 * @author vzwingma
 *
 */
@Document(collection = "comptesbancaires")
@Getter @Setter @NoArgsConstructor
public class CompteBancaire  extends AbstractAPIObjectModel {

	// SId
	private static final long serialVersionUID = -846392155444814540L;

	@Id
	private String id;
		
	// Libellé du compte
	private String libelle;
	// Propriétaire du compte
	@JsonIgnore
	private Proprietaire proprietaire;
	// Icone
	private String itemIcon;
	// N° d'ordre
	private int ordre;
	// closed
	private Boolean actif;
	
	/**
	 * @return the actif
	 */
	public Boolean isActif() {
		// Vrai par défaut
		return actif != null ? actif : Boolean.TRUE;
	}

	/**
	 * Embeded Document Utilisateur (résumé d'un utilisateur)
	 * @author vzwingma
	 *
	 */
	@Getter @Setter @NoArgsConstructor
	public class Proprietaire implements Serializable{

		private static final long serialVersionUID = 4167886825681128618L;
		@Id
		private String id;
		// Login
		private String login;
		// Libellé
		private String libelle;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CompteBancaire)) {
			return false;
		}
		CompteBancaire other = (CompteBancaire) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}
	
	
}
