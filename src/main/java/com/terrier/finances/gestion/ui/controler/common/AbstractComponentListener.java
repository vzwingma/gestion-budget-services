/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.common;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Window;

/**
 * Controleur de listeners
 * @author vzwingma
 *
 */
public abstract class AbstractComponentListener extends AbstractUIService {

	
	
	/**
	 * Controleur
	 * @param classNameControleur
	 * @return controleur correspondant
	 */
	public <CTRL extends AbstractUIController<? extends CustomComponent>> CTRL getControleur(Class<CTRL> classNameControleur) {
		return getUISession().getControleur(classNameControleur);
	}
	
	
	/**
	 * Set popup modale
	 * @param popupModale enregistre la popup
	 */
	@Override
	public void setPopupModale(Window popupModale){
		getUISession().setPopupModale(popupModale);
	}
}
