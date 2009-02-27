package iaas.dltk.bpelscript.internal;

import iaas.dltk.bpelscript.IBPELScriptColorConstants;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

public class BPELScriptSourceViewerConfiguration extends
		ScriptSourceViewerConfiguration {

	private AbstractScriptScanner fCodeScanner;
	private AbstractScriptScanner fStringScanner;
	private AbstractScriptScanner fCommentScanner;

	public BPELScriptSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[] { new DefaultIndentLineAutoEditStrategy() };
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer,
			String contentType) {
		return new String[] { "\t", "        " };
	}

	protected ContentAssistPreference getContentAssistPreference() {
		return BPELScriptContentAssistPreference.getDefault();
	}

	// This method called from base class.
	protected void initializeScanners() {
		// This is our code scanner
		this.fCodeScanner = new BPELScriptCodeScanner(
				this.getColorManager(), this.fPreferenceStore);
		// This is default scanners for partitions with same color.
		this.fStringScanner = new SingleTokenScriptScanner(this
				.getColorManager(), this.fPreferenceStore,
				IBPELScriptColorConstants.BPELSCRIPT_STRING);
		this.fCommentScanner = new SingleTokenScriptScanner(this
				.getColorManager(), this.fPreferenceStore,
				IBPELScriptColorConstants.BPELSCRIPT_COMMENT);
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(this
				.getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				this.fCodeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(this.fStringScanner);
		reconciler.setDamager(dr, IBPELScriptColorConstants.BPELSCRIPT_STRING);
		reconciler.setRepairer(dr, IBPELScriptColorConstants.BPELSCRIPT_STRING);

		dr = new DefaultDamagerRepairer(this.fCommentScanner);
		reconciler.setDamager(dr, IBPELScriptColorConstants.BPELSCRIPT_COMMENT);
		reconciler.setRepairer(dr, IBPELScriptColorConstants.BPELSCRIPT_COMMENT);

		return reconciler;
	}

	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		if (this.fCodeScanner.affectsBehavior(event)) {
			this.fCodeScanner.adaptToPreferenceChange(event);
		}
		if (this.fStringScanner.affectsBehavior(event)) {
			this.fStringScanner.adaptToPreferenceChange(event);
		}
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return this.fCodeScanner.affectsBehavior(event)
				|| this.fStringScanner.affectsBehavior(event);
	}

	@Override
	public IInformationPresenter getOutlinePresenter(ScriptSourceViewer viewer,
			boolean doCodeResolve) {
		return null;
	}

	
	
}