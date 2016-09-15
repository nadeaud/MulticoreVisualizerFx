package didier.multicore.visualizer.fx.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCore;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.gef.graph.Node;

public class VisualizerNode extends Node {
	
	private int NodeID = -1;
	
	private VisualizerCore m_core;
	
	private ArrayList<VisualizerThread> m_thread;
	
	public VisualizerNode(Map<String, Object> attrs) {
		super(attrs);
		m_thread = new ArrayList<>();
	}
	
	public void onClickAction() {
		System.out.println("Node selected");
	}

	public int getNodeID() {
		return NodeID;
	}

	public void setNodeID(int nodeID) {
		NodeID = nodeID;
	}

	public VisualizerCore getCore() {
		return m_core;
	}

	public void setCore(VisualizerCore m_core) {
		this.m_core = m_core;
	}
	
	public void addThread(VisualizerThread thread) {
		m_thread.add(thread);
	}
	
	public void removeThread(VisualizerThread thread) {
		m_thread.remove(thread);
	}
	
	public void clearThread() {
		m_thread.clear();
	}
	
	public List<VisualizerThread> getThreads() {
		return m_thread;
	}

}
