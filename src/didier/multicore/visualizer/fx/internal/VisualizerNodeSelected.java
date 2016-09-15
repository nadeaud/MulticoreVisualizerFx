package didier.multicore.visualizer.fx.internal;

import org.eclipse.gef.mvc.fx.policies.IFXOnClickPolicy;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;

import didier.multicore.visualizer.fx.models.VisualizerNode;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class VisualizerNodeSelected extends AbstractInteractionPolicy<Node> implements IFXOnClickPolicy {
	
	@Override
	public NodePart getHost() {
		// TODO Auto-generated method stub
		return (NodePart) super.getHost();
	}

	@Override
	public void click(MouseEvent e) {
		if(e.getClickCount() == 1) {
			if(getHost().getContent() instanceof VisualizerNode ) {
				VisualizerNode node = (VisualizerNode) getHost().getContent();
				node.onClickAction();
			}else {
				//getHost().getShape().setStyle("-fx-fill: rgb(250, 0, 0);");
				//getHost().refreshVisual();
			}
		}
		
	}

}
