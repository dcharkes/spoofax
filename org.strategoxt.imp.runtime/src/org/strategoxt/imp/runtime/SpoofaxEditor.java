package org.strategoxt.imp.runtime;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.strategoxt.imp.runtime.dynamicloading.BadDescriptorException;
import org.strategoxt.imp.runtime.services.StrategoObserver;
import org.strategoxt.imp.runtime.services.outline.SpoofaxOutlinePage;

public class SpoofaxEditor extends UniversalEditor {
	
	@SuppressWarnings("hiding")
	public static final String EDITOR_ID = "org.eclipse.imp.runtime.spoofaxEditor";
	
	@Override
	public Object getAdapter(Class adapter) {
		
		// when opening the editor for a file with an unknown extension, let IMP handle it.
		if (getParseController() == null) {
			return super.getAdapter(adapter);
		}
		
		// for backward compatibility, we only instantiate a SpoofaxOutlinePage if an outline strategy is defined.
		if (adapter.equals(IContentOutlinePage.class)) {
			EditorState editorState = EditorState.getEditorFor(getParseController());
			StrategoObserver observer = null;
			try {
				observer = editorState.getDescriptor().createService(StrategoObserver.class, editorState.getParseController());
			} catch (BadDescriptorException e) {
				e.printStackTrace();
			}
			
			observer.getLock().lock();
			try {
				if (observer.getRuntime().lookupUncifiedSVar(SpoofaxOutlinePage.OUTLINE_STRATEGY) != null) {
					return new SpoofaxOutlinePage(EditorState.getEditorFor(getParseController()));
				}
			}
			finally {
				observer.getLock().unlock();
			}
		}

		if (adapter == IPropertySource.class) {
			// TODO
		}

		return super.getAdapter(adapter);
	}
}