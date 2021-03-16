/**
 * 
 */
package com.terrier.finances.gestion.communs.api.model;

import com.terrier.finances.gestion.communs.abstrait.AbstractAPIObjectModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * API /Actuator/Infos
 * @author vzwingma
 *
 */
@Getter @Setter
public class Info extends AbstractAPIObjectModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1415425535189056299L;
	
	private App app;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppInfo [name=").append(app.getName()).append("version=").append(app.getVersion()).append(", name=").append(app.getName()).append(", description=")
				.append(app.getDescription()).append("]");
		return builder.toString();
	}
	
	
	/**
	 * App Info
	 * @author vzwingma
	 *
	 */
	@Getter @Setter
	public class App implements Serializable {
		
		// UID
		private static final long serialVersionUID = -3934171045494468262L;
		
		private String version;
		private String name;
		private String description;
	}
}
