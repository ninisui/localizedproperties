package com.triadsoft.properties.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.triadsoft.properties.editor.Activator;

/**
 * Pagina de preferencias para los Wildcards Paths
 * 
 * @author Triad (flores.leonardo@gmail.com)
 */
public class LocalizedPropertiesPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public LocalizedPropertiesPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Activator.getString("preferences.description"));
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new WilcardPathEditor(
				PreferenceConstants.WILDCARD_PATHS_PREFERENCES,
				Activator.getString("preferences.wildcardTitle.label"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new EncodingEditor(
				PreferenceConstants.LANGUAGE_CONTENT_TYPE_PREFERENCES,
				Activator.getString("preferences.encodingTitle.label"),
				getFieldEditorParent()));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}