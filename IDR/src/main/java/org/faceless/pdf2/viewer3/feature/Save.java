// $Id: Save.java 21182 2015-03-31 14:44:19Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.faceless.pdf2.OutputProfile;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.Exporter;
import org.faceless.pdf2.viewer3.PDFBackgroundTask;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.SidePanel;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.ViewerEvent;
import org.faceless.pdf2.viewer3.ViewerFeature;
import org.faceless.pdf2.viewer3.ViewerWidget;
import org.faceless.pdf2.viewer3.util.DialogPanel;
import org.faceless.util.Langolier;

/**
 * <p>
 * Create a button that will allow the PDF to be saved to disk. If the PDF was originally
 * loaded from a File, the user will not be prompted for a filename, otherwise this feature
 * functions like {@link SaveAs}.
 * </p>
 * <div class="initparams">
 * The following <a href="../doc-files/initparams.html">initialization parameters</a> can be specified to configure this feature.
 * <table summary="">
 * <tr><th>promptOnOverwrite</th><td>true to prompt before overwriting files, false otherwise (the default)</td></tr>
 * <tr><th>disableUnlessDirty</th><td>true to disable this feature until the PDF has been marked as "dirty" (ie it has been altered), false to always enable this feature (the defualt)</td></tr>
 * </table>
 * </div>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Save</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class Save extends ViewerWidget implements DocumentPanelListener, PropertyChangeListener {

    private AbstractAction action;
    private boolean overwriteprompt = false;
    private boolean disableUnlessDirty = false;

    public Save() {
        super("Save");
        setButton("Document", "resources/icons/disk.png", "PDFViewer.tt.Save");
        setMenu("File\tSave", 's');
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                ViewerEvent ve = new ViewerEvent(event, getViewer());
                action(ve);
            }
        };
    }

    public void action(ViewerEvent event) {
        save(event, null, null, true, overwriteprompt);
        if (disableUnlessDirty) {
            action.setEnabled(false);
        }
    }

    public ActionListener createActionListener() {
        return action;
    }

    public boolean isEnabledByDefault() {
        return Util.hasFilePermission();
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);

        String val = getFeatureProperty(viewer, "promptOnOverwrite");
        if (val != null) {
            setPromptOnOverwrite("true".equals(val));
        }
        val = getFeatureProperty(viewer, "disableUnlessDirty");
        if (val != null) {
            disableUnlessDirty = true;
            action.setEnabled(false);
        }

        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        DocumentPanel docpanel = event.getDocumentPanel();
        if (type.equals("activated")) {
            if (disableUnlessDirty) {
                docpanel.addPropertyChangeListener(this);
            } else {
                propertyChange(new PropertyChangeEvent(docpanel, "dirty", Boolean.TRUE, Boolean.FALSE));
            }
        } else if (type.equals("permissionChanged") && docpanel != null && docpanel == getViewer().getActiveDocumentPanel()) {
            if (docpanel.hasPermission("Save")) {
                propertyChange(new PropertyChangeEvent(docpanel, "dirty", Boolean.TRUE, Boolean.FALSE));
            } else {
                action.setEnabled(false);
            }
        } else if (type.equals("deactivated")) {
            docpanel.removePropertyChangeListener(this);
            action.setEnabled(false);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        DocumentPanel docpanel = (DocumentPanel) event.getSource();
        if ("dirty".equals(event.getPropertyName())) {
            if (docpanel.hasPermission("Save") && docpanel.getPDF() != null && (docpanel.isDirty() || !disableUnlessDirty)) {
                action.setEnabled(true);
            }
        }
    }

    /**
     * Set whether this feature should prompt before overwriting a file
     * @param prompt whether to prompt before overwriting a file (detault is false)
     * @since 2.11.25
     */
    public void setPromptOnOverwrite(boolean prompt) {
        this.overwriteprompt = prompt;
    }

    /**
     * Set whether this feature should be disabled unless the PDF is marked as "dirty",
     * i.e. it has been changed since it was loaded. The default is false.
     * @param value whether to disable this feature unless the PDF is marked as dirty.
     * @since 2.16.1
     */
    public void setDisableUnlessDirty(boolean value) {
        this.disableUnlessDirty = value;
    }

    /**

    /**
     * Save the Document
     * @param event the ViewerEvent that launched this action
     * @param initialexporter the {@link Exporter} to choose by default, or null to default
     * to a {@link PDFExporter}
     * @param initialpath the Path to display by default, or <code>null</code> to use the same
     * path as the source file
     * @param displayprompt whether to prompt the user for a filename. Currently ignored.
     * @param overwriteprompt whether to prompt the user if we are about to overwrite a filename.
     * @since 2.11.10
     */
    public static void save(final ViewerEvent event, final Exporter initialexporter, final String initialpath, final boolean displayprompt, final boolean overwriteprompt) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                File file = null;
                if (initialpath != null) {
                    file = new File(initialpath);
                } else {
                    file = (File) event.getDocumentPanel().getClientProperty("file");
                }
                doSave(file, initialexporter, event, false, displayprompt, overwriteprompt);
                return null;
            }
        });
    }

    static void doSave(File file, final Exporter initialexporter, ViewerEvent event, boolean prompt, boolean displayprompt, boolean overwriteprompt) {
        final DocumentPanel docpanel = event.getDocumentPanel();
        final PDFViewer viewer = event.getViewer();
        final Preferences preferences = viewer.getPreferences();
        final PDF pdf = event.getPDF();

        if (file == null || prompt) {
            File directory = file == null ? null : file.getParentFile();
            if (directory == null && preferences != null) {
                try {
                    directory = new File((String) preferences.get("lastDirectory", null));
                } catch (Exception e) { }
            }
            if (directory == null) { // Slightly over paranoid, there appear to
                try {                // be Windows bugs in this area.
                    directory = new File(System.getProperty("user.dir"));
                } catch (SecurityException e) {
                    directory = File.listRoots()[0];
                }
            }
            if (file == null) {
                file = directory;
            } else if (file.getParentFile() == null) {
                file = new File(directory, file.getName());
            }

            SaveFileChooser chooser = new SaveFileChooser(file, docpanel, initialexporter, overwriteprompt);
            Util.fixFileChooser(chooser);
            if (chooser.showDialog(docpanel, UIManager.getString("FileChooser.saveButtonText")) == JFileChooser.APPROVE_OPTION) {
                final Exporter exporter = chooser.getExporter();
                final JComponent exporterComponent = chooser.getExporterComponent();
                final File targetfile = chooser.getSelectedFile();

                boolean save = true;
                if (chooser.isExporterPopupRequired()) {
                    DialogPanel dialog = new DialogPanel() {
                        public String validateDialog() {
                            return exporter.validateComponent(exporterComponent);
                        }
                    };
                    exporterComponent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    dialog.addComponent(exporterComponent);
                    dialog.setButtonText("ok", UIManager.getString("PDFViewer.Save"));
                    if (!dialog.showDialog(docpanel, Util.getUIString("PDFViewer.SavingFile", "PDF"))) {
                        save = false;
                    }
                }

                if (save && targetfile != null) { // Targetfile is never null if JFileChooser behaves - never assume this.
                    doSave1(pdf, exporter, exporterComponent, targetfile, file, viewer, preferences, docpanel);
                }
            }
        } else {
            final Exporter exporter = new PDFExporter();
            doSave1(pdf, exporter, null, file, file, viewer, preferences, docpanel);
        }
    }

    private static void doSave1(final PDF pdf, final Exporter exporter, final JComponent exporterComponent, final File file, final File origfile, final PDFViewer viewer, final Preferences preferences, final DocumentPanel docpanel) {
        if (preferences != null) {
            preferences.put("lastDirectory", file.getParent());
        }
        try {
            final File canonFile = file.getCanonicalFile();
            final java.util.List<PDFBackgroundTask> tasks = new ArrayList<PDFBackgroundTask>();
            final Set<PDF> pdfs = new LinkedHashSet<PDF>();
            pdfs.add(docpanel.getPDF());

            /* If the file we're saving to exists, render to a temporary file
             * in the same directory and rename. If it's the same file we've
             * read from, we need to ensure file backing store remains
             * uncorrupted. We do this by setting the "MultipleRevisions"
             * feature to required. This will preserve the file structure
             * for File based PDF (and do nothing otherwise)
             */
            pdf.getBasicOutputProfile().clearRequired(OutputProfile.Feature.MultipleRevisions);

            final File tempfile;
            if (file.exists()) {
                String tempname = file.getName();
                if (tempname.lastIndexOf(".") > 0) {
                    tempname = tempname.substring(0, tempname.lastIndexOf(".") + 1);
                }
                while (tempname.length() < 3) { // Minimum length of 3, apparently
                    tempname += 'x';
                }
                tempfile = File.createTempFile(tempname, null, file.getParentFile());
                if (canonFile.equals(origfile.getCanonicalFile())) {
                    pdf.getBasicOutputProfile().setRequired(OutputProfile.Feature.MultipleRevisions);
                }
            } else {
                tempfile = file;
            }

            DocumentPanel[] panels = viewer.getDocumentPanels();
            for (DocumentPanel panel : panels) {
                File f = (File) panel.getClientProperty("file");
                if (f != null && canonFile.equals(f.getCanonicalFile())) {
                    pdfs.add(panel.getPDF());
                    for (SidePanel sidePanel : panel.getSidePanels()) {
                        if (sidePanel instanceof PDFBackgroundTask) {
                            tasks.add((PDFBackgroundTask) sidePanel);
                        }
                    }
                }
            }

            final OutputStream out = new FileOutputStream(tempfile);
            Exporter.ExporterTask task = exporter.getExporter(docpanel, docpanel.getPDF(), exporterComponent, out);
            if (exporter instanceof PDFExporter) {
                task.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        // Run when we cancel or complete
                        if (event.getPropertyName().equals("state") && "running".equals(event.getOldValue())) {
                            if (tempfile!=file) {         // If we're writing to a temp file
                                if ("cancelled".equals(event.getNewValue())) {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        Util.displayThrowable(e, viewer);
                                    }
                                    if (!tempfile.delete()) {
                                        tempfile.deleteOnExit();
                                    }
                                } else if ("completed".equals(event.getNewValue())) {
                                    try {
                                        // Pause all related tasks
                                        for (PDFBackgroundTask task : tasks) {
                                            task.pause();
                                        }
                                        for (PDF p : pdfs) {
                                            p.close();
                                        }
                                        boolean windows = System.getProperty("os.name").startsWith("Windows");
                                        if (windows) {
                                            /* This is because of the following
                                             * situation on Windows:
                                             * 1. Open file A.pdf
                                             * 2. Close file
                                             * 3. Open file B.pdf save as A.pdf
                                             * If 3 happens before PDF object A
                                             * was gc'd, Windows will
                                             * refuse to delete the open file.
                                             * Ugly but necessary for now.
                                             */
                                            String[] pendinggc = Langolier.list();
                                            String test = "closer:" + file.getPath();
                                            for (int i=0;i<pendinggc.length;i++) {
                                                if (pendinggc[i].equals(test)) {
                                                    System.gc();
                                                    break;
                                                }
                                            }
                                        }
                                        if (windows && file.exists() && !file.delete()) {
                                            Exception e = new IOException("Couldn't delete \""+file+"\" before rename");
                                            Util.displayThrowable(e, viewer);
                                        } else if (!tempfile.renameTo(file)) {
                                            // If rename did not work, just copy
                                            // the contents
                                            InputStream tin = null;
                                            OutputStream fout = null;
                                            try {
                                                tin = new FileInputStream(tempfile);
                                                fout = new FileOutputStream(file);
                                                byte[] buf = new byte[Math.max(4096, tin.available())];
                                                for (int len = tin.read(buf); len != -1; len = tin.read(buf)) {
                                                    fout.write(buf, 0, len);
                                                }
                                            } catch (IOException e) {
                                                Util.displayThrowable(e, viewer);
                                            } finally {
                                                close(fout);
                                                close(tin);
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        Util.displayThrowable(e, viewer);
                                    } finally {
                                        // Unpause all related tasks
                                        for (PDFBackgroundTask task : tasks) {
                                            task.unpause();
                                        }
                                        if (!tempfile.delete()) {
                                            tempfile.deleteOnExit();
                                            // Truncate it as well
                                            try {
                                                new FileOutputStream(tempfile).close();
                                            } catch (IOException e) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
            task.start(viewer, Util.getUIString("PDFViewer.SavingFile", file.toString()));
        } catch (IOException e) {
            Util.displayThrowable(e, viewer);
        }
    }

    private static final void close(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
        }
    }
    
    private static final void close(OutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
        }
    }
    
    /** 
     * Extends the abomination that is JFileChooser to allow an options panel in the main dialog.
     */
    protected static class SaveFileChooser extends JFileChooser {

        // Some notes from testing - note Nimbus/Metal, Aqua and Windows all behave differently.

        // Aqua:
        // * Filter will always show all files, those that don't match are greyed out but are still selectable
        // * SaveDialog will grey out all files, but they're still selectable. Use a Custom dialog to prevent this
        // * With Save Dialog, directories are greyed out regardless of filter and "Save" cannot be selected. Double click will traverse in.
        // * With Custom Dialog, directories are not greyed out if filter allows thenm and "Save" or double click will traverse into it
        // * SelectedFile property is not changed when text field is edited, only when Save button is clicked.
        // * Calling setSelectedFile will set the filename but will not highlight the file if it exists.
        // 
        // Nimbus:
        // * Filter will only show selectable files, and this includes directories. Selecting "Save" on a directory
        //   will traverse into it.
        // * New Folder icon will create a folder called "NewFolder" immediately.
        // * Selecting a folder with custom dialog will cause SelectedFile->null, although text field is unchanged.
        // * SelectedFile property is not changed when text field is edited, only when Save button is clicked.
        // * Calling setSelectedFile will set the filename but will not highlight the file if it exists.
        // 
        // Windows:
        // * Filter will only show selectable files, and this includes directories. Selecting "Save" on a directory
        //   will traverse into it.
        // * Selecting a folder will change Save button text to Open,
        // * Selecting a folder with custom dialog will cause SelectedFile->null, although text field is unchanged.
        // * SelectedFile property is not changed when text field is edited, only when Save button is clicked.
        // * Calling setSelectedFile will set the filename but will not highlight the file if it exists.
        // * Initial filter is not set automatically to the first choosable option - this must be done manually.
        //
        // To sum up:
        // * Directories must be selectable if user is to change directory
        // * SelectedFile cannot be assumed to be set to anything useful until after the Save button
        // * Really, filter should be independent of file-format
        // * Must use CustomDialog instead of SaveDialog for Aqua interface to make sense. No other side-effects
        // * There is some voodoo regarding approveSelection - attempts to put this class into our own dialog
        //   with our own selection action failed, due to the SelectedFile not being set. So we have to use
        //   their dialogs

        private Map<FileFilter,Exporter> filters;
        private final JPanel jpanel;
        private final DocumentPanel docpanel;
        private final boolean overwriteprompt;
        private final JTextField filenamefield;
        private boolean panelpopup;
        private Exporter exporter;
        private JComponent accessory;

        /**
         * @param file the initial file
         * @param docpanel the DocumentPanel this relates to
         * @param initialexporter the default Exporter to choose
         * @param overwriteprompt if true, user will be prompted when attempting to overwrite a file
         */
        protected SaveFileChooser(File file, DocumentPanel docpanel, Exporter initialexporter, boolean overwriteprompt) {
            super(file);
            this.docpanel = docpanel;
            this.overwriteprompt = overwriteprompt;
            this.jpanel = new JPanel(new BorderLayout());
            filenamefield = Util.getJFileChooserFileName(this);
            setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);   // Required otherwise user can't traverse directories...
            setMultiSelectionEnabled(false);

            if (!file.isDirectory()) {
                ensureFileIsVisible(file);
                setSelectedFile(file);
            } else {
                setCurrentDirectory(file);
            }

            FileFilter initialfilter = null;
            PDFViewer viewer = docpanel.getViewer();
            ViewerFeature[] features = viewer.getFeatures();
            for (int i = 0; i < features.length; i++) {
                if (features[i] instanceof Exporter) {
                    Exporter exporter = (Exporter) features[i];
                    if (exporter.isEnabled(docpanel)) {
                        FileFilter filter = exporter.getFileFilter();
                        addChoosableFileFilter(filter);
                        filters.put(filter, exporter);
                        if (initialfilter == null || exporter.equals(initialexporter)) {
                            initialfilter = filter;
                        }
                    }
                }
            }
            if (filters.isEmpty()) {                        // No filters - always add a PDFExporter
                Exporter exporter = new PDFExporter();
                initialfilter = exporter.getFileFilter();
                addChoosableFileFilter(initialfilter);
                filters.put(initialfilter, exporter);
            }

            // Ideally FileFilter and format would be different, because you might want to save a
            // PDF with an extension of ".tif" and be able to browser those files. We don't do this
            // currently for simplicity - customizing JFileChooser is a nightmare - so the FileFilter
            // is required to determine which exporter to use.  
            // In the case where only one exporter is available, we can let the user browse all files.
            // 
            // Also, Windows L&F requires an initial filter to be set, even if it's AcceptAll
            //
            if (filters.size() == 1) {
                setAcceptAllFileFilterUsed(true);
                setFileFilter(initialfilter);                           // To ensure exporter & exporterComponent are set
                setFileFilter(getAcceptAllFileFilter());
            } else {
                setFileFilter(initialfilter);
            }
        }

        @Override public void setFileFilter(FileFilter filter) {
            if (filters == null) {
                this.filters = new LinkedHashMap<FileFilter,Exporter>();
            }
            File file = getSelectedFile();
            super.setFileFilter(filter);
            Exporter exporter = filters.get(filter);
            if (exporter != null) {
                String oldsuffix = this.exporter == null ? null : this.exporter.getFileSuffix();
                String filename = null;
                if (filenamefield != null) {
                    filename = filenamefield.getText();
                } else if (file != null) {
                    filename = file.getName();
                }
                if (filename != null && oldsuffix != null) {
                    if (filename.endsWith("." + oldsuffix)) {
                        filename = filename.substring(0, filename.length() - oldsuffix.length()) + exporter.getFileSuffix();
                        setSelectedFile(new File(file.getParentFile(), filename));
                    }
                }
                this.exporter = exporter;

                JComponent newaccessory = exporter.getComponent(docpanel, file);
                if (accessory != null && newaccessory == null && panelpopup) {
                    Util.patchJFileChooser(this, jpanel, false);                        // Here be dragons
                }
                if (accessory == null && newaccessory != null) {
                    panelpopup = !Util.patchJFileChooser(this, jpanel, true);
                }
                accessory = newaccessory;
                jpanel.removeAll();
                if (accessory != null) {
                    jpanel.add(accessory);
                }
                JDialog dialog = (JDialog)SwingUtilities.getAncestorOfClass(JDialog.class, jpanel);
                if (dialog != null) {
                    jpanel.revalidate();
                    dialog.revalidate();
                    dialog.pack();
                }
            }
        }

        @Override public void approveSelection() {
            File file = getSelectedFile();
            String msg = null;
            if (file == null) {
                return;
            }

            if (file.exists()) {
                if (!file.canWrite()) {
                    msg = Util.getUIString("PDFViewer.ReadOnly", file.getName());
                } else if (overwriteprompt) {
                    String prompt = UIManager.getString("PDFViewer.OverwriteFile");
                    if (JOptionPane.showConfirmDialog(docpanel.getViewer(), prompt, prompt, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            }
            try {
                if (file.toPath().getFileSystem().isReadOnly()) {                      // Java 7
                    msg = Util.getUIString("PDFViewer.ReadOnly", file.getName());
                }
            } catch (Throwable e) { }

            if (msg == null && accessory != null && !panelpopup) {
                msg = exporter.validateComponent(accessory);
            }
            if (msg != null) {
                JOptionPane.showMessageDialog(docpanel.getViewer(), msg, UIManager.getString("PDFViewer.Error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            super.approveSelection();
        }

        public Exporter getExporter() {
            return exporter;
        }

        public JComponent getExporterComponent() {
            return accessory;
        }

        public boolean isExporterPopupRequired() {
            return panelpopup && accessory != null;
        }

    }

}
