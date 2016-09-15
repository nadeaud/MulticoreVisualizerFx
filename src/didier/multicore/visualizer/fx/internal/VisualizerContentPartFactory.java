package didier.multicore.visualizer.fx.internal;

import java.util.Map;

import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

public class VisualizerContentPartFactory extends ZestFxContentPartFactory {
	
	@Inject
	private Injector injector;
	
	@Override
	public IContentPart<Node, ? extends Node> createContentPart(
			Object content, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		
		if(content instanceof org.eclipse.gef.graph.Node) {
			VisualizerContentPart part = new VisualizerContentPart(content);
			if(part != null) {
				injector.injectMembers(part);
			}
			return part;			
		}
		
		return super.createContentPart(content, contextBehavior, contextMap);
	}

}
