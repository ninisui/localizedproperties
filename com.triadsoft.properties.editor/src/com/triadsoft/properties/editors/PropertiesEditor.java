package com.triadsoft.properties.editors;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.triadsoft.common.properties.ILocalizedPropertyFileListener;
import com.triadsoft.common.properties.PropertyFile;
import com.triadsoft.properties.editor.Activator;
import com.triadsoft.properties.model.Property;
import com.triadsoft.properties.model.ResourceList;
import com.triadsoft.properties.model.utils.PropertyTableViewer;
import com.triadsoft.properties.model.utils.WildcardPath;

/**
 * <p>
 * Editor que muestra la grilla de datos de las propiedades, viendo en la
 * primera columna las claves y en las dem�s los distintos idiomas.
 * </p>
 * <p>
 * El editor intentar� descubrir seg�n el path de los archivos de recursos,los
 * distintos idiomas a los que se encuentran los resource bundle. Para �sto
 * utiliza el WildcardPath.
 * </p>
 * <p>
 * Segun los distintos lenguajes de programacion, los archivos de recursos est�n
 * en distintas ubicaciones con distintas convenciones en el nombre de los
 * archivos y la ubicacion de los mismos.
 * </p>
 * <p>
 * Por �ste motivo es que el Properties editor intenta buscar seg�n el path del
 * archivos cual es el wildcard path de cada uno.
 * </p>
 * 
 * @author Triad (flores.leonardo@gmail.com)
 * @see WildcardPath
 * @see ResourceBundle
 */
public class PropertiesEditor extends MultiPageEditorPart implements
		IResourceChangeListener, ILocalizedPropertyFileListener {

	public static final String KEY_COLUMN_ID = "key_column";

	/** The text editor used in page 0. */
	private PropertyTableViewer tableViewer;

	private TextEditor textEditor;

	/** The text widget used in page 2. */
	private StyledText text;

	private ResourceList resource;

	private boolean isTableModified;

	private boolean isTextModified;

	/**
	 * Crea un editor de propiedades
	 */
	public PropertiesEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public ResourceList getResource() {
		return resource;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		resource = new ResourceList(file);
		createPage0();
		//createPage1();
		setPartName(resource.getFileName());
	}

	/**
	 * Crea la pagina que contiene la tabla donde se muestran los recursos
	 */
	private void createPage0() {
		Locale locale = resource.getDefaultLocale();
		tableViewer = new PropertyTableViewer(this, getContainer(), locale);
		tableViewer.setLocales(resource.getLocales());
		tableViewer.setInput(resource);
		int index = addPage(tableViewer.getControl());
		setPageText(index, Activator.getString("editor.tab.properties"));
	}

	/**
	 * Crea la solapa que contiene el texto
	 */
	void createPage1() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, Activator.getString("editor.tab.preview"));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, Activator.getString("editor.tab.preview"));
	}

	protected void tableChanged() {
		isTableModified = true;
		if (!super.isDirty()) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	protected void handlePropertyChange(int propertyId) {
		if (propertyId == IEditorPart.PROP_DIRTY && getActivePage() == 0) {
			isTableModified = isDirty();
		} else if (propertyId == IEditorPart.PROP_DIRTY && getActivePage() == 1) {
			isTextModified = isDirty();
		}
		super.handlePropertyChange(propertyId);
	}

	@Override
	public boolean isDirty() {
		return isTableModified || isTextModified || super.isDirty();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		tableViewer.dispose();
		resource.dispose();
		super.dispose();
	}

	void updateTitle() {
		IEditorInput input = getEditorInput();
		// setTitle(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		// getEditor(0).doSave(monitor);
		resource.save();
		isTableModified = false;
		isTextModified = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	/**
	 * Sobre escribo el save as... porque no puedo sobrescribir varios archivos
	 * juntos. Nada para hacer porque nunca ser� llamado
	 */
	public void doSaveAs() {
		// Nada para hacer porque nunca ser� llamado;
	}

	/**
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void setFocus() {
		switch (getActivePage()) {
		case 0:
			tableViewer.getTable().setFocus();
			break;
		case 1:
			textEditor.setFocus();
			break;
		}
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/**
	 * No se permite el save as porque hay varios archivos que se est�n editando
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int pageIndex) {

		PropertyFile pf = resource.getPropertyFile(resource.getDefaultLocale());
		if (pageIndex == 1) {
			if (isTableModified) {
				textEditor.getDocumentProvider().getDocument(
						textEditor.getEditorInput()).set(pf.asText());
			}
		} else if (pageIndex == 0) {
			if (isTextModified) {
				try {
					PropertyFile npf = new PropertyFile(textEditor
							.getDocumentProvider().getDocument(
									textEditor.getEditorInput()).get(), pf
							.getEncoding(), pf.getSeparator());
					npf.setFile(pf.getFile());
					resource.setPropertyFile(npf, resource.getDefaultLocale());
					tableViewer.setLocales(resource.getLocales());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.pageChange(pageIndex);
	}

	/**
	 * Escucha los cambios de los recursos en el workspace, y si es alguno de
	 * los que le interesa, obtiene los locales actualizados y se los setea a la
	 * tabla para que actualice las columnas
	 * 
	 * @param event
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}

		if (this.resource.resourceChanged(event)) {
			tableViewer.setLocales(resource.getLocales());
		}
	}

	/**
	 * Cada vez que se agrega o modifica el valor de una columna se actualiza
	 * 
	 * @param key
	 *            Clave que se modifica
	 * @param value
	 *            Valor modificado
	 * @param locale
	 *            Locale en el que se modifico el valor
	 */
	public void valueChanged(String key, String value, Locale locale) {
		tableChanged();
		resource.changeValue(key, value, locale);
		tableViewer.refresh();
	}

	public void keyChanged(String oldKey, String key) {
		tableChanged();
		resource.keyChanged(oldKey, key);
		tableViewer.refresh();
	}

	public void removeKey(String key) {
		tableChanged();
		resource.removeKey(key);
		tableViewer.refresh();
	}

	public void addKey(String key) {
		tableChanged();
		resource.addKey(key);
		tableViewer.refresh();
		Object[] properties = resource.getProperties();
		for (int i = 0; i < properties.length; i++) {
			if (((Property) properties[i]).getKey().equals(key)) {
				tableViewer.setSelection(
						new StructuredSelection(properties[i]), true);
				tableViewer.getTable().setSelection(new int[] { i });
				break;
			}

		}
	}
}
