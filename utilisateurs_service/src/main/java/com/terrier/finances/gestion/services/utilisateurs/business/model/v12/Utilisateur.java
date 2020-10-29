/**
 * 
 */
package com.terrier.finances.gestion.services.utilisateurs.business.model.v12;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import com.terrier.finances.gestion.communs.utils.data.BudgetDateTimeUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Définition d'un utilisateur de la BDD
 * @author vzwingma
 *
 */
@Document(collection = "utilisateurs")
@Getter @Setter @NoArgsConstructor
public class Utilisateur implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6535669274718528190L;
	
	@Id
	private String id;
	// Login
	private String login;
	
	private LocalDateTime dernierAcces;
	
	// Libellé
	private String libelle;
	/**
	 * Préférences
	 */
	private Map<UtilisateurPrefsEnum, String> prefsUtilisateur = new EnumMap<>(UtilisateurPrefsEnum.class);
	/**
	 * Droits
	 */
	private Map<UtilisateurDroitsEnum, Boolean> droits = new EnumMap<>(UtilisateurDroitsEnum.class);
	
	
	/**
	 * @param clePreference clé d'une préférence
	 * @param <T> Type de la préférence
	 * @return the preferences
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPreference(UtilisateurPrefsEnum clePreference) {
		return (T)prefsUtilisateur.get(clePreference);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.login;
	}
	
	public String toFullString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Utilisateur [id=").append(login)
				.append(", dateDernierAcces=")
				.append(dernierAcces != null ? BudgetDateTimeUtils.getLibelleDate(dernierAcces) : "nulle").append(", libelle=").append(libelle)
				.append("]");
		return builder.toString();
	}

}
