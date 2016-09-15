package didier.multicore.visualizer.fx.internal;

import java.util.Arrays;

import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCore;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.zest.fx.parts.NodePart;

import didier.multicore.visualizer.fx.controller.MulticoreVisualizerFx;
import didier.multicore.visualizer.fx.models.VisualizerFxThread;
import didier.multicore.visualizer.fx.models.VisualizerNode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
	Node m_shape;
	VBox m_vbox;
	private ImageView iconImageView;
	private Text labelText;
	
	protected Node createVisualizerShape() {
		GeometryNode<?> shape = new GeometryNode<>(new org.eclipse.gef.geometry.planar.Rectangle());
		shape.setUserData("defaultShape");
		
		shape.getStyleClass().add("shape");

		shape.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
				Arrays.asList(new Stop(0, new Color(1, 1, 1, 1)))));
		shape.setStroke(new Color(0, 0, 0, 1));
		shape.setStrokeType(StrokeType.INSIDE);
		return shape;
	}
	
	@Override
	protected Group createVisual() {
		
		final Group group = new Group() {

			@Override
			public boolean isResizable() {
				return true;
			}

			@Override
			protected void layoutChildren() {
				// we directly layout our children from within resize
			};

			@Override
			public double maxHeight(double width) {
				return m_vbox.maxHeight(width);
			}

			@Override
			public double maxWidth(double height) {
				return m_vbox.maxWidth(height);
			}

			@Override
			public double minHeight(double width) {
				return m_vbox.minHeight(width);
			}

			@Override
			public double minWidth(double height) {
				return m_vbox.minWidth(height);
			}

			@Override
			public double prefHeight(double width) {
				return m_vbox.prefHeight(width);
			}

			@Override
			public double prefWidth(double height) {
				return m_vbox.prefWidth(height);
			}

			@Override
			public void resize(double w, double h) {
				// for shape we use the exact size
				m_shape.resize(w, h);
				// for vbox we use the preferred size
				m_vbox.setPrefSize(w, h);
				m_vbox.autosize();
				// and we relocate it to be horizontally and vertically centered
				// w.r.t. the shape
				Bounds vboxBounds = m_vbox.getLayoutBounds();
				m_vbox.relocate((w - vboxBounds.getWidth()) / 2, (h - vboxBounds.getHeight()) / 2);
			};
		};
		m_shape = createVisualizerShape();
		
		Pane nestedPane = new Pane();
		Scale scale = new Scale();
		nestedPane.setStyle("-fx-background-color: green;");
		nestedPane.getTransforms().add(scale);
		scale.setX(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		scale.setY(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		
		// initialize image view
		iconImageView = new ImageView();
		iconImageView.setImage(null);
		iconImageView.getStyleClass().add(CSS_CLASS_ICON);
		
		// initialize text
		labelText = new Text();
		labelText.setText("");
		labelText.getStyleClass().add(CSS_CLASS_LABEL);
		
		HBox hbox = new HBox();
		hbox.getChildren().addAll(iconImageView, labelText);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		
		
		VBox threadBox = new VBox();
		threadBox.setAlignment(Pos.CENTER);
		if(m_node != null) {
			for(VisualizerThread thread : m_node.getThreads()) {
				ObservableMap<String, Object> map = m_node.getAttributes();
				Object controller = map.get("VisualizerController");
				if(controller instanceof MulticoreVisualizerFx) {
					VisualizerFxThread n = new VisualizerFxThread(thread, (MulticoreVisualizerFx)controller, 5.0, Color.RED);
					threadBox.getChildren().add(n);
				}else {
					VisualizerFxThread n = new VisualizerFxThread(thread, null, 0.0, 0.0, 5, Color.YELLOW);
					threadBox.getChildren().add(n);
				}
			}
		}
		nestedPane.getChildren().add(threadBox);
		VBox.setVgrow(nestedPane, Priority.ALWAYS);
		
		m_vbox = new VBox();
		m_vbox.setMouseTransparent(false);
		m_vbox.setAlignment(Pos.CENTER);
		m_vbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		m_vbox.getChildren().addAll(hbox);
		
		
		group.getChildren().addAll(m_shape, m_vbox);
		return group;
	}
}
