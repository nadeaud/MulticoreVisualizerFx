package didier.multicore.visualizer.fx.view;

import java.util.List;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IHSAWaveExecutionContext;
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
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuComputeUnit;
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuSIMD;
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuStreamingElement;
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
	
	public void resetCanvas (IDMContext[] ctxs) {
		clearCanvas();
		fillCanvas(ctxs);
	}
	
	/* Remove the waves from the display. */
	public void clearCanvas () {
		for (GpuStreamingElement se : fGpuModel.getStreamingEngines()) {
			for (GpuComputeUnit cu : se.getComputeUnits()) {
				for (GpuSIMD simd : cu.getSIMDs()) {
					javafx.scene.shape.Rectangle rect = simd.getRectangle();
					javafx.scene.layout.HBox hbox = simd.getHBox();
					if(hbox == null || rect == null)
						continue;
					
					rect.setStyle("-fx-fill: #fc8d59;");	
					hbox.getChildren().clear();
				}
			}
		}
	}
	
	/* Insert waves into the display */
	public void fillCanvas(IDMContext[] ctxs) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(fGpuModel == null || ctxs == null)
					return;
				
				for(IDMContext ctx : ctxs) {
					if (! (ctx instanceof IHSAWaveExecutionContext))
						continue;
					IHSAWaveExecutionContext wave = (IHSAWaveExecutionContext) ctx;
					
					int se_id = Integer.parseInt(wave.getSE());
					int cu_id = Integer.parseInt(wave.getCU());
					int simd_id = Integer.parseInt(wave.getSIMD());
					
					GpuSIMD simd = fGpuModel.getSe(se_id) == null ? 
							null : fGpuModel.getSe(se_id).getCu(cu_id) == null ?
									null : fGpuModel.getSe(se_id).getCu(cu_id).getSIMD(simd_id);
					if(simd == null)
						continue;
					
					javafx.scene.shape.Rectangle rect = simd.getRectangle();
					javafx.scene.layout.HBox hbox = simd.getHBox();
					if(hbox == null || rect == null)
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
