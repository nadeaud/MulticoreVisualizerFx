package didier.multicore.visualizer.fx.view;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerModel;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;
import didier.multicore.visualizer.fx.internal.MulticoreVisualizerFxUIModule;
import javafx.embed.swt.FXCanvas;

public class MulticoreVisualizerFxView extends ZestFxUiView {

	private MulticoreVisualizerFx multicoreVisualizer;
	
	public MulticoreVisualizerFxView() {
		//super();
		super(Guice.createInjector(Modules.override(new MulticoreVisualizerFxUIModule()).with((Module) new ZestFxUiModule())));
		setGraph(MulticoreVisualizerGraph.createDefaultGraph());
		multicoreVisualizer = new MulticoreVisualizerFx(this);
	}

	@SuppressWarnings("restriction")
	public void resetCanvas(VisualizerModel model) {
		Graph graph = MulticoreVisualizerGraph.createVisualizerGraph(model, multicoreVisualizer);
		FXCanvas canvas = getCanvas();
		//canvas.getScene().getStylesheets().clear();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				//canvas.getScene().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
				setGraph(graph);
			}
		});
	}

}
