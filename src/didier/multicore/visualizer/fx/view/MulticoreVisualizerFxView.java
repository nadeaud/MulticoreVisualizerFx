package didier.multicore.visualizer.fx.view;

import java.util.List;

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
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel;
import didier.multicore.visualizer.fx.models.StreamingElementNode;
import didier.multicore.visualizer.fx.utils.model.HsailWaveModel;
import didier.multicore.visualizer.fx.view.MulticoreVisualizerGraph.GpuGraph;

public class MulticoreVisualizerFxView extends ZestFxUiView {
	
	private AmdFijiNanoModel fGpuModel;
	private Graph fCurrentGraph;	
	private boolean gpu_visualizer = true;
	private MulticoreVisualizerFx multicoreVisualizer;
	
	/* Constructor */
	
	public MulticoreVisualizerFxView() {
		super(Guice.createInjector(Modules.override(new MulticoreVisualizerFxUIModule()).with((Module) new ZestFxUiModule())));
		
		if(!gpu_visualizer) {
			/* Default graph that will be erased later on. */
			fCurrentGraph = MulticoreVisualizerGraph.createDefaultGraph();
			setGraph(fCurrentGraph);
		}else {
			/* Base graph create from static model in AmdFijiNanoModel */
			GpuGraph graph = MulticoreVisualizerGraph.createGPUVisualizerGraph();
			fCurrentGraph = graph.fGraph;
			fGpuModel = graph.fGpuModel;
			setGraph(fCurrentGraph);
		}
		multicoreVisualizer = new MulticoreVisualizerFx(this, gpu_visualizer);
	}
	
	/* Dispose */
	
	@Override
	public void dispose() {
		multicoreVisualizer.dispose();
		multicoreVisualizer = null;
		super.dispose();
	}
	
	/* Get methods */
	
	
	/* Graphic methods */	

	/* Modify the display to show the actual cpus and threads. */
	@SuppressWarnings("restriction")
	public void resetCanvas(VisualizerModel model) {
		fCurrentGraph = MulticoreVisualizerGraph.createVisualizerGraph(model, multicoreVisualizer);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setGraph(fCurrentGraph);
			}
		});
	}
	
	/* Modify the display to show wave information */
	public void resetCanvas(List<HsailWaveModel> models) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(fGpuModel == null || models == null)
					return;
				
				for(HsailWaveModel model : models) {
					javafx.scene.shape.Rectangle rect = fGpuModel.getSe(model.se_id).getCu(model.cu_id)
							.getSIMD(model.simd_id).getRectangle();
					javafx.scene.layout.HBox hbox = fGpuModel.getSe(model.se_id).getCu(model.cu_id)
							.getSIMD(model.simd_id).getHBox();
					if(rect == null)
						continue;
					rect.setStyle("-fx-fill: yellow;");	
					javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(3.0);
					circle.setStyle("-fx-fill: brown;");
					hbox.getChildren().add(circle);
				}
			}
		});
	}
}
