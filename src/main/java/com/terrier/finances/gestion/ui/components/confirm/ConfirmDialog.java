package com.terrier.finances.gestion.ui.components.confirm;

import com.terrier.finances.gestion.ui.sessions.UISessionManager;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * Used to confirm events.
 * 
 * @author Patrick Oberg, Joonas Lehtinen, Tommi Laukkanen
 */
public final class ConfirmDialog extends Window implements Button.ClickListener {

	
	private static final long serialVersionUID = -4028700652105475717L;
	private static final int ONE_HUNDRED_PERCENT = 100;
	private static final int CONFIRMATION_DIALOG_WIDTH = 400;

	private final transient ConfirmationDialogCallback callback;
	private final Button okButton;
	private final Button cancelButton;

	private VerticalLayout mainLayout;
	
	/**
	 * * Constructor for configuring confirmation dialog. * @param caption the
	 * dialog caption. * @param question the question. * @param okLabel the Ok
	 * button label. * @param cancelLabel the cancel button label. * @param
	 * callback the callback.
	 */
	public ConfirmDialog(final String caption, final String question, final String okLabel, final String cancelLabel, final ConfirmationDialogCallback callback) {

		super(caption);
		setWidth(CONFIRMATION_DIALOG_WIDTH, Unit.PIXELS);
		okButton = new Button(okLabel, this);
		okButton.setStyleName("friendly");
		cancelButton = new Button(cancelLabel, this);
		cancelButton.setStyleName("danger");
		cancelButton.focus();
		setModal(true);
		setResizable(false);
		setContent(buildMainLayout());
		this.callback = callback;

		Label label = new Label(question, ContentMode.HTML);

		if (question != null) {
			mainLayout.addComponent(label);
			mainLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		}

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.addComponent(okButton);
		mainLayout.addComponent(buttonLayout);

		((VerticalLayout) getContent()).setHeight(ONE_HUNDRED_PERCENT, Unit.PERCENTAGE);
		((VerticalLayout) getContent()).setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
	}
	

	
	/**
	 * @return layout
	 */
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		return mainLayout;
	}


	/** * Event handler for button clicks. * @param event the click event. */
	public void buttonClick(final ClickEvent event) {
		if (getParent() != null) {
			UISessionManager.get().getSession().getPopupModale().close();
		}
		callback.response(event.getSource() == okButton);
	}

	/** * Interface for confirmation dialog callbacks. */
	public interface ConfirmationDialogCallback {

		/** * The user response. * @param ok True if user clicked ok. */
		void response(boolean ok);
	}
}