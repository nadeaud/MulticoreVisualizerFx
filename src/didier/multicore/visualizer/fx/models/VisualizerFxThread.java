package didier.multicore.visualizer.fx.models;


import java.util.HashSet;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCPU;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCore;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.cdt.visualizer.ui.util.SelectionUtils;
import org.eclipse.jface.viewers.ISelection;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class VisualizerFxThread extends Circle {
	
	final protected VisualizerThread m_thread; 	
	
	public static class VisualizerThreadEventHandler implements EventHandler<MouseEvent> {
		
		private VisualizerFxThread m_visualThread;
		private MulticoreVisualizerFx m_controller;
		
		public VisualizerThreadEventHandler(VisualizerFxThread visualThread, MulticoreVisualizerFx controller) {
			m_visualThread = visualThread;
			m_controller = controller;
		}

		@Override
		public void handle(MouseEvent event) {
			if(event != null && m_visualThread != null && event.getClickCount() == 1) {
				VisualizerThread thread = m_visualThread.m_thread;
				VisualizerCore core = thread.getCore();
				VisualizerCPU cpu = core.getCPU();
				
				HashSet<Object> selectedObjects = new HashSet<Object>();
				selectedObjects.add(thread);
				selectedObjects.add(cpu);
				selectedObjects.add(core);
				
				if(m_controller != null ) {
					ISelection selection = SelectionUtils.toSelection(selectedObjects);
					m_controller.selectionChanged(selection);
				}
			}
			
		}
		
	}
	
	public VisualizerFxThread(VisualizerThread thread, MulticoreVisualizerFx controller, double centerX, double centerY, double radius, Paint fill) {
		super(centerX, centerY, radius, fill);
		m_thread = thread;
		VisualizerThreadEventHandler handler = new VisualizerThreadEventHandler(this, controller);
		setEventHandler(handler);
	}
	
	public VisualizerFxThread(VisualizerThread thread, MulticoreVisualizerFx controller, double radius, Paint fill) {
		super(radius, fill);
		m_thread = thread;
		VisualizerThreadEventHandler handler = new VisualizerThreadEventHandler(this, controller);
		setEventHandler(handler);
	}
	
	public void setEventHandler(EventHandler<? super MouseEvent> handler) {
		setOnMouseClicked(handler);
	}

}
