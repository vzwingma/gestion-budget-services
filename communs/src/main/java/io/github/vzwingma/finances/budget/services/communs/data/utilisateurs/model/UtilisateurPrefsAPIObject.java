package io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.model;

import io.github.vzwingma.finances.budget.services.communs.data.abstrait.AbstractAPIObjectModel;
import io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.enums.UtilisateurDroitsEnum;
import io.github.vzwingma.finances.budget.services.communs.data.utilisateurs.enums.UtilisateurPrefsEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
	@Schema(description = "Id de l'utilisateur")
	private String idUtilisateur;

	/**
	 * @param lastAccessTime : Date de dernier accès
	 */
	@Schema(description = "Date de dernier accès")
	private Long lastAccessTime;

	/**
	 * @param droits : Liste des droits utilisateur
	 *        	DROIT_CLOTURE_BUDGET,
	 * 			DROIT_RAZ_BUDGET
	 */
	@Schema(description = "Liste des droits utilisateur\n" +
			"\t * DROIT_CLOTURE_BUDGET,\n" +
			"\t * DROIT_RAZ_BUDGET")
	private Map<UtilisateurDroitsEnum, Boolean> droits;

	/**
	 * @param preferences : Liste des préférences utilisateurs
	 *        PREFS_STATUT_NLLE_DEPENSE
	 */
	@Schema(description = "Liste des préférences utilisateurs\n" +
			"\t *        PREFS_STATUT_NLLE_DEPENSE")
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
