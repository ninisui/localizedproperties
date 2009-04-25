package com.triadsoft.properties.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.triadsoft.properties.editor.Activator;

/**
 * Permite crear un archivo de propiedades localizado por el idioma
 */

public class LocalizedPropertiesPage extends WizardPage {
	private Text containerText;

	private Text fileText;

	private ISelection selection;

	private List wildcardList;

	private Label filenameLabel;

	private Label langLabel;

	private Label countryLabel;

	private Text rootText;

	private Text languageText;

	private Text countryText;

	private Label filepathLabel;

	private Text filepathText;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public LocalizedPropertiesPage(ISelection selection) {
		super("wizardPage");
		setTitle("Multi-page Editor File");
		setDescription("This wizard creates a new file with *.properties extension that can be opened by a multi-page editor.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		layout.verticalSpacing = 9;
		Label rootLabel = new Label(container, SWT.NULL);
		rootLabel.setText("&Wildcard Paths:");
		GridData pathsData = new GridData();
		pathsData.horizontalSpan = 4;
		rootLabel.setLayoutData(pathsData);

		wildcardList = new List(container, SWT.SINGLE);
		wildcardList.setItems(Activator.getWildcardPaths());
		GridData wildData = new GridData(GridData.FILL_HORIZONTAL);
		wildData.horizontalSpan = 4;
		wildcardList.setLayoutData(wildData);

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		rootLabel = new Label(container, SWT.NULL);
		rootLabel.setText("&Root:");

		filenameLabel = new Label(container, SWT.NULL);
		filenameLabel.setText("&Filename");

		langLabel = new Label(container, SWT.NULL);
		langLabel.setText("Language");

		countryLabel = new Label(container, SWT.NULL);
		countryLabel.setText("Country");

		rootText = new Text(container, SWT.BORDER | SWT.SINGLE);
		rootText.setText("locale");
		GridData fullData = new GridData(GridData.FILL_HORIZONTAL);
		rootText.setLayoutData(fullData);

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		fileText.setLayoutData(fullData);

		languageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		languageText.setText("en");
		languageText.setLayoutData(fullData);

		countryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		countryText.setText("AR");
		countryText.setLayoutData(fullData);

		filepathLabel = new Label(container, SWT.NULL);
		filepathLabel.setText("File path");
		GridData filepath = new GridData();
		filepath.horizontalSpan = 4;
		filepathLabel.setLayoutData(filepath);

		filepathText = new Text(container, SWT.BORDER | SWT.SINGLE);
		filepathText.setText("<resultado>");
		GridData filepathData = new GridData(GridData.FILL_HORIZONTAL);
		filepathData.horizontalSpan = 4;
		filepathText.setLayoutData(filepathData);
		filepathText.setEnabled(false);

		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new_file");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("properties") == false) {
				updateStatus("File extension must be \"properties\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
}