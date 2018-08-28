/**
 * 
 */
package com.terrier.finances.gestion.ui.components.abstrait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.controler.common.AbstractUIController;
import com.terrier.finances.gestion.ui.sessions.UserSessionsManager;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * @author vzwingma
 *
 * @param <C> controleur
 */
public abstract class AbstractUIComponent<C extends AbstractUIController<?>> extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6504689977742498737L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUIComponent.class);
	
	private C controleur;
	
	/**
	 * Démarrage du controleur
	 */
	public void startControleur(){
		controleur = createControleur();
		LOGGER.trace("[{}] Démarrage du controleur {}", controleur.getIdSession(), controleur);
		controleur.start();
		
	}
	
	
	/**
	 * @return the controleur
	 */
	public C getControleur() {
		return controleur;
	}

	/**
	 * Recherche du parent à partir de sa classe
	 * @param composant composant racine
	 * @param classeParent classe recherchée
	 * @return objet parent correspondant
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getParentComponent(Component composant, Class<T> classeParent){
		if(composant.getParent() != null){
			if(composant.getParent().getClass().equals(classeParent)){
				return (T)composant.getParent();
			}
			return getParentComponent(composant.getParent(), classeParent);
		}
		return null;		
	}


	/**
	 * @return création d'un controleur
	 */
	public abstract C createControleur();
	
}
