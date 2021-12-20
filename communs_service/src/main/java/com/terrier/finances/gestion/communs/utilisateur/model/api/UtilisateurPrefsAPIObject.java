package com.terrier.finances.gestion.communs.utilisateur.model.api;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * Objet API des préférences utilisateur
 * @author vzwingma
 *
 */
@Getter @Setter @NoArgsConstructor
public class UtilisateurPrefsAPIObject extends AbstractAPIObjectModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7746155427438885252L;

	/**
	 * @param idUtilisateur :Id de l'utilisateur associé
	 */
	@NonNull
	private String idUtilisateur;

	/**
	 * @param lastAccessTime : Date de dernier accès
	 */
	private Long lastAccessTime;

	/**
	 * @param droits : Liste des droits utilisateur
	 */
	private Map<UtilisateurDroitsEnum, Boolean> droits;

	/**
	 * @param preferences : Liste des préférences utilisateurs
	 */
	private Map<UtilisateurPrefsEnum, String> preferences;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UtilisateurPrefsAPIObject [idUtilisateur=").append(idUtilisateur);
		if(lastAccessTime != null){
			builder.append(", lastAccessTime=")
				.append(lastAccessTime);
		}
		if(this.preferences != null){
			builder.append(", preferences=").append(preferences);
		}
		builder.append("]");
		return builder.toString();
	}

}
