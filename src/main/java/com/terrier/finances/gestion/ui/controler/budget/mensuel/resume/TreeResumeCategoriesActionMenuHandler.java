/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.budget.mensuel.resume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrier.finances.gestion.ui.components.budget.mensuel.components.TreeGridResumeCategories;
import com.vaadin.event.Action;

/**
 * Controleur du menu du tableau des résumés
 * @author vzwingma
 *
 */
public class TreeResumeCategoriesActionMenuHandler implements Action.Handler{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4256635378437805758L;


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeResumeCategoriesActionMenuHandler.class);


	private static final Action COLLAPSE_CATEGORIE = new Action("Ferme la categorie");

	private static final Action EXTEND_CATEGORIE = new Action("Ouvre la categorie");
	
	private static final Action EXTEND_ALL_CATEGORIES = new Action("Ouvre toutes les categories");

	private static final Action COLLAPSE_ALL_CATEGORIES = new Action("Ferme toutes les catégories");

	/**
	 * Liste des actions du menu
	 * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Action[] getActions(Object target, Object sender) {

		TreeGridResumeCategories tree = (TreeGridResumeCategories)sender;
        if (target == null) {
            // Context menu in an empty space -> add a new main category
            return new Action[]{ EXTEND_ALL_CATEGORIES, COLLAPSE_ALL_CATEGORIES };

        } else {
        	String niveauCategorie = (String)target;
            // Context menu for a sous category
        	if(Integer.parseInt(niveauCategorie) < 100){
        		niveauCategorie = tree.getParent().getId();
        	}
    		Action actionCategorie = null; // tree.isExpanded(niveauCategorie) ? EXTEND_CATEGORIE : COLLAPSE_CATEGORIE;
    		return new Action[]{ actionCategorie, EXTEND_ALL_CATEGORIES, COLLAPSE_ALL_CATEGORIES };
        }
	}

	/* (non-Javadoc)
	 * @see com.vaadin.event.Action.Handler#handleAction(com.vaadin.event.Action, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void handleAction(Action action, Object sender, Object target) {
		TreeGridResumeCategories tree = (TreeGridResumeCategories)sender;
		
		/*
		if(EXTEND_ALL_CATEGORIES.equals(action) || COLLAPSE_ALL_CATEGORIES.equals(action)){
			for (Object objItemId : tree.getItemIds()) {
				Integer itemId = (Integer)objItemId;
				LOGGER.trace("item {} : {}", itemId, tree.getItem(itemId));
				tree.setCollapsed(itemId, COLLAPSE_ALL_CATEGORIES.equals(action));
			}
		}
		else if(target != null){
			Integer niveauCategorie = (Integer)target;
            // Context menu for a sous category
        	if(niveauCategorie < 100){
        		niveauCategorie = (Integer)tree.getParent(niveauCategorie);
        	}
			LOGGER.trace("item {} : {}", target, tree.getItem(niveauCategorie));
			tree.setCollapsed(niveauCategorie, COLLAPSE_CATEGORIE.equals(action));
		}		
		*/
	}
}
