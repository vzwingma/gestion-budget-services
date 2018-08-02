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
public abstract class AbstractComponentListener implements AbstractUIService {

	
	
	/**
	 * Controleur
	 * @param classNameControleur
	 * @return controleur correspondant
	 */
	public <C extends AbstractUIController<? extends CustomComponent>> C getControleur(Class<C> classNameControleur) {
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
