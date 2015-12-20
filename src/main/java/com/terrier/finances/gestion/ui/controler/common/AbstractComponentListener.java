/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.common;

import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.ui.CustomComponent;

/**
 * Controleur de listeners
 * @author vzwingma
 *
 */
public abstract class AbstractComponentListener {

	
	
	/**
	 * Controleur
	 * @param classNameControleur
	 * @return controleur correspondant
	 */
	public <CTRL extends AbstractUIController<? extends CustomComponent>> CTRL getControleur(Class<CTRL> classNameControleur) {
		return UISessionManager.getSession().getControleur(classNameControleur);
	}
}
