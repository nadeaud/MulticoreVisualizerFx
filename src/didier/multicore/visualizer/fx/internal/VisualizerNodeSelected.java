package didier.multicore.visualizer.fx.internal;

import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;

import didier.multicore.visualizer.fx.models.VisualizerNode;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class VisualizerNodeSelected extends AbstractInteractionPolicy implements IOnClickPolicy {
	
	@Override
	public IVisualPart<? extends Node> getHost() {
		// TODO Auto-generated method stub
		return (IVisualPart<? extends Node>) super.getHost();
	}

	@Override
	public void click(MouseEvent e) {
		if(e.getClickCount() == 1) {
			if(getHost() instanceof VisualizerNode ) {
				//VisualizerNode node = (VisualizerNode) getHost().getVisual();
				//node.onClickAction();
			}else {
				//getHost().getShape().setStyle("-fx-fill: rgb(250, 0, 0);");
				//getHost().refreshVisual();
			}
		}
		
	}

}
