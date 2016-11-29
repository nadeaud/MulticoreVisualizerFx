package didier.multicore.visualizer.fx.models;

import org.eclipse.gef.graph.Node;

import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuStreamingElement;
import javafx.scene.shape.Rectangle;


public class StreamingElementNode extends Node{
	
	private GpuStreamingElement element;

	public GpuStreamingElement getElement() {
		return element;
	}

	public void setElement(GpuStreamingElement element) {
		this.element = element;
	}

}
