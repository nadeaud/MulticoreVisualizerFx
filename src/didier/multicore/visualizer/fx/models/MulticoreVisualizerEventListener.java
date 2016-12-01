package didier.multicore.visualizer.fx.models;

import org.eclipse.cdt.dsf.debug.service.IBreakpointsExtension.IBreakpointHitDMEvent;
import org.eclipse.cdt.dsf.debug.service.IRunControl.ISuspendedDMEvent;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;

public class MulticoreVisualizerEventListener {
	
	private MulticoreVisualizerFx fMulticoreVisualizerFX;
	
	public MulticoreVisualizerEventListener (MulticoreVisualizerFx controller) {
		fMulticoreVisualizerFX = controller;
	}
	
	@DsfServiceEventHandler
	public void handleEvent (final ISuspendedDMEvent event) {
		fMulticoreVisualizerFX.triggerUpdateCanvas();
	}
	
	@DsfServiceEventHandler
	public void handleEvent (final IBreakpointHitDMEvent event) {
		fMulticoreVisualizerFX.triggerUpdateCanvas();
	}

}
