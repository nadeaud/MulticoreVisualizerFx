package didier.multicore.visualizer.fx.view;

import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;

public class MulticoreVisualizerZestFxRootPart extends ZestFxRootPart {
	
	@SuppressWarnings("restriction")
	@Override
	protected void doActivate() {
		super.doActivate();
		String custom_css_style = this.getClass().getResource("style.css").toExternalForm();
		
		//getViewer().getAdapter(GridModel.class).setShowGrid(false);
		//getVisual().getScene().getStylesheets().add(custom_css_style);
	}

}
