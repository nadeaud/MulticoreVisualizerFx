package didier.multicore.visualizer.fx.internal;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import didier.multicore.visualizer.fx.models.StreamingElementNode;
import javafx.scene.Node;

public class VisualizerContentPartFactory extends ZestFxContentPartFactory implements IContentPartFactory {
	
	@Inject
	private Injector injector;
	
	@Override
	public IContentPart<? extends Node>  createContentPart(
			Object content,	Map<Object, Object> contextMap) {
		
		if(content instanceof StreamingElementNode) {
			GPUVisualizerContentPart part = new GPUVisualizerContentPart(content);
			if(part != null) {
				injector.injectMembers(part);
			}
			return part;
		}
		if(content instanceof org.eclipse.gef.graph.Node) {
			VisualizerContentPart part = new VisualizerContentPart(content);
			if(part != null) {
				injector.injectMembers(part);
			}
			return part;			
		}
		
		return super.createContentPart(content, contextMap);
	}

}
