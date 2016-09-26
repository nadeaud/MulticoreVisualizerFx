package didier.multicore.visualizer.fx.internal;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.zest.fx.parts.NodePart;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;
import didier.multicore.visualizer.fx.models.VisualizerFxThread;
import didier.multicore.visualizer.fx.models.VisualizerNode;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

public class VisualizerContentPart extends NodePart {
	
	public VisualizerContentPart(Object content) {
		if(content instanceof VisualizerNode) {
			m_node = (VisualizerNode) content;
		}
		
	}
	
	private VisualizerNode m_node;
	private Node m_shape;
	private VBox m_vbox;
	private CustomPane canvas;

	
	protected Node createDefaultShape() {
		GeometryNode<?> shape = new GeometryNode<>(new org.eclipse.gef.geometry.planar.Rectangle());
		shape.setUserData("defaultShape");
		
		shape.getStyleClass().add("shape");

		shape.setFill(Color.GREEN);
		shape.setStroke(new Color(0, 0, 0, 1));
		shape.setStrokeType(StrokeType.INSIDE);
		return shape;
	}

	public final class CustomPane extends Pane {
		@Override
		public boolean isResizable() {
	        return true;
	    }
	}
	
	@Override
	protected Group createVisual() {
		// container set-up
		final Group group = new Group() {
			
			@Override
			public boolean isResizable() {
				return true;
			}
			
			@Override
			public void resize(double w, double h) {
				w = w < 0 ? 0 : w;
				h = h < 0 ? 0 : h;
				canvas.setPrefSize(w, h);
			}
		};

		canvas = new CustomPane();
		canvas.setStyle("-fx-background-color : green;");
		canvas.setPrefSize(100, 100);
		canvas.setMouseTransparent(false);
		
		m_vbox = new VBox();
		
		org.eclipse.gef.graph.Node node = getContent();
		if(node instanceof VisualizerNode) {
			VisualizerNode vNode = (VisualizerNode) node;
			for(VisualizerThread thread : vNode.getThreads()) {
				Object controller = vNode.getAttributes().get("VisualizerController");
				VisualizerFxThread c = new VisualizerFxThread(thread,
						controller instanceof MulticoreVisualizerFx ? (MulticoreVisualizerFx) controller : null,
								5.0, 
								Color.BLUE);
				m_vbox.getChildren().add(c);
			}
		}
		m_vbox.setMouseTransparent(false);
		
		canvas.getChildren().add(m_vbox);
		group.getChildren().add(canvas);
		return group;
	}
}
