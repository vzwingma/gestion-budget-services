package com.terrier.finances.gestion.communs.utilisateur.model.api;

import java.util.Map;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurDroitsEnum;
import com.terrier.finances.gestion.communs.utilisateur.enums.UtilisateurPrefsEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
	@ApiModelProperty(notes = "Id de l'utilisateur associé", required=true)
	private String idUtilisateur;
	
	@ApiModelProperty(notes = "Date de dernier accès", required=false)
	private Long lastAccessTime;

	@ApiModelProperty(notes = "Liste des droits utilisateur", required = false)
	private Map<UtilisateurDroitsEnum, Boolean> droits;
	
	@ApiModelProperty(notes = "Liste des préférences utilisateurs", required=false)
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
