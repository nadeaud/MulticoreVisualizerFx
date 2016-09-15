package didier.multicore.visualizer.fx.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCPU;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCore;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerModel;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;
import didier.multicore.visualizer.fx.models.VisualizerBuilder;
import didier.multicore.visualizer.fx.models.VisualizerNode;

@SuppressWarnings("restriction")
public class MulticoreVisualizerGraph extends AbstractZestExample {

	public MulticoreVisualizerGraph(String title) {
		super("Zest Test Graph");
	}

	public static Graph createDefaultGraph() {
		String id = "Test";
		List<Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.LABEL__NE, "A", ZestProperties.TOOLTIP__N, "Alpha", ZestProperties.CSS_ID__NE,
						id + "A"),
				n(ZestProperties.LABEL__NE, "B", ZestProperties.TOOLTIP__N, "Beta", ZestProperties.CSS_ID__NE,
						id + "B"),
				n(ZestProperties.LABEL__NE, "C", ZestProperties.TOOLTIP__N, "Gamma", ZestProperties.CSS_ID__NE,
						id + "C")));
		
		
		List<Edge> edges = new ArrayList<>();
		
		HashMap<String, Object> attrs = new HashMap<>();		
		attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new GridLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);
	}
	
	public static Graph createSecondGraph() {
		String id = "Test";
		List<Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.LABEL__NE, "A", ZestProperties.TOOLTIP__N, "Alpha", ZestProperties.CSS_ID__NE,
						id + "A"),
				n(ZestProperties.LABEL__NE, "B", ZestProperties.TOOLTIP__N, "Beta", ZestProperties.CSS_ID__NE,
						id + "B")));
		
		
		List<Edge> edges = new ArrayList<>();
		
		HashMap<String, Object> attrs = new HashMap<>();		
		attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new GridLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);
	}
	
	public static Graph createVisualizerGraph(VisualizerModel model, MulticoreVisualizerFx controller) {
		
		model.sort();

		if(model.getCPUCount() > 0) {
			VisualizerCPU cpu = model.getCPU(0);
			List<VisualizerCore> cores = cpu.getCores();
			
			List<Edge> edges = new ArrayList<>();
			List<VisualizerNode> nodes = new ArrayList<>();
			for(VisualizerCore core : cores) {
				//nodes.add(n(ZestProperties.LABEL__NE, Integer.toString(core.m_id), ZestProperties.CSS_ID__NE, "test"));
				VisualizerNode node = VisualizerBuilder.buildNode(ZestProperties.LABEL__NE, Integer.toString(core.m_id), "VisualizerController", controller);
				for(VisualizerThread thread : model.getThreads()) {
					if(thread.getCore() == core) {
						node.addThread(thread);
					}
				}
				nodes.add(node);

			}
			HashMap<String, Object> attrs = new HashMap<>();
			attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new GridLayoutAlgorithm());
			return new Graph(attrs, nodes, edges);
		}
		
		return null;		
	}
	
	@Override
	protected Graph createGraph() {
		return createDefaultGraph();
	}
	

	/*
	@Override
	protected Module createModule() {
		return new CustomModule();
	}
	*/
}
