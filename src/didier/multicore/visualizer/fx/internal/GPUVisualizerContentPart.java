package didier.multicore.visualizer.fx.internal;

import org.eclipse.debug.internal.ui.views.memory.SetPaddedStringPreferencePage;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.zest.fx.parts.NodePart;

import didier.multicore.visualizer.fx.internal.VisualizerContentPart.CustomPane;
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuComputeUnit;
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuSIMD;
import didier.multicore.visualizer.fx.models.AmdFijiNanoModel.GpuStreamingElement;
import didier.multicore.visualizer.fx.models.StreamingElementNode;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class GPUVisualizerContentPart extends NodePart{
	
	private StreamingElementNode m_node;

	public GPUVisualizerContentPart(Object content) {
		if(content instanceof StreamingElementNode) {
			m_node = (StreamingElementNode) content;
		}
	}
	
	private Node m_shape;
	private VBox m_vbox;
	private AnchorPane canvas;
	private GridPane m_gridPane;
	
	protected Node createDefaultShape() {
		GeometryNode<?> shape = new GeometryNode<>(new org.eclipse.gef.geometry.planar.Rectangle());
		shape.setUserData("defaultShape");
		
		shape.getStyleClass().add("shape");

		shape.setFill(Color.GREEN);
		shape.setStroke(new Color(0, 0, 0, 1));
		shape.setStrokeType(StrokeType.INSIDE);
		return shape;
	}
	
	public static class ResizableRectangle extends javafx.scene.shape.Rectangle {
		public ResizableRectangle(double w, double h) {
			super(w,h);
			
		}
		
		@Override
		public double minWidth(double height) {
	        return 0;
	    }
		@Override
		public double maxWidth(double height) {
	        return Double.MAX_VALUE;
	    }
		@Override
		public double minHeight(double width) {
	        return 0;
	    }
		@Override
		public double maxHeight(double width) {
	        return Double.MAX_VALUE;
	    }
		
		@Override
		public boolean isResizable() {
			return true;
		}
		
		@Override
		public void resize(double width, double height) {
			setWidth(width);
			setHeight(height);
			
		}
	}
	
	@Override 
	protected Group createVisual() {
		System.out.print(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
		final Group group = new Group() {
			@Override
			public boolean isResizable() {
				return true;
			}
			
			@Override
			public void resize(double w, double h) {
				w = w < 0 ? 0 : w;
				h = h < 0 ? 0 : h;
				m_gridPane.setPrefSize(w, h);
			}
		};
		
		m_gridPane = new GridPane();
		m_gridPane.setStyle("-fx-background-color : #1a9850; -fx-border-color: #1a9850; -fx-border-width: 5;");
		m_gridPane.setPrefSize(100, 100);
		m_gridPane.setMouseTransparent(false);
		m_gridPane.setHgap(6.0);
		m_gridPane.setVgap(6.0);
		/*
		canvas = new AnchorPane();
		canvas.setStyle("-fx-background-color : lightcyan; -fx-border-color: silver; -fx-border-width: 3;");
		canvas.setPrefSize(100, 100);
		canvas.setMouseTransparent(false);
		
		m_vbox = new VBox(3.0);
		m_vbox.setFillWidth(true);
		*/
		//m_vbox.maxHeightProperty().bind(canvas.heightProperty().subtract(10));
		
		int i=0,j=0;
		if(m_node != null) {
			GpuStreamingElement element = m_node.getElement();
			for(GpuComputeUnit cu : element.get_cus()) {
				
				for(GpuSIMD simd : cu.get_simds()) {
					
					if(i == 0) {
						ColumnConstraints cc = new ColumnConstraints();
						cc.setPercentWidth(100.0/(double)cu.get_simds().size());
						cc.setFillWidth(true);
						m_gridPane.getColumnConstraints().add(cc);
					}
					StackPane sPane = new StackPane();
					Rectangle rect = new ResizableRectangle(50.0,50.0);
					rect.setStyle("-fx-fill: #91cf60;");
					rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent me) {
							if(me.getClickCount() == 1) {
								System.out.println("Hi");
								if(rect != null) {
									rect.setStyle("-fx-fill: #fc8d59;");
								}
							}
						}
					});
					HBox hbox = new HBox(2);
					simd.setHBox(hbox);
					simd.setRectangle(rect);
					sPane.getChildren().add(rect);
					sPane.getChildren().add(hbox);
					m_gridPane.add(sPane, j, i);
					j++;
					//rect.widthProperty().bind(canvas.widthProperty().subtract(50.0).divide(cu.get_simds().size()));
					//rect.heightProperty().bind(canvas.heightProperty().
					//		subtract(10.0).
					//		divide(element.get_cus().size()*cu.get_simds().size()));
					//hbox.setHgrow(rect, Priority.ALWAYS);
					//hbox.getChildren().add(rect);	
					//hbox.setFillHeight(true);
					//hbox.prefWidthProperty().bind(m_vbox.widthProperty());
					//hbox.maxWidthProperty().bind(canvas.widthProperty());

				}
				j=0;
				i++;
				
				RowConstraints rc = new RowConstraints();
				double percent = 100.0/(double)element.get_cus().size();
				rc.setPercentHeight(percent);
				rc.setFillHeight(true);
				//rc.setVgrow(Priority.ALWAYS);
				m_gridPane.getRowConstraints().add(rc);
				
				//m_vbox.setVgrow(hbox, Priority.ALWAYS);
				//m_vbox.getChildren().add(hbox);
			}
			
		}
		/*
		m_vbox.setMouseTransparent(false);
		canvas.setTopAnchor(m_vbox, 5.0);
		canvas.setBottomAnchor(m_vbox, 5.0);

		canvas.setLeftAnchor(m_vbox, 5.0);
		canvas.setRightAnchor(m_vbox, 5.0);
		canvas.getChildren().add(m_vbox);
		*/
		group.getChildren().add(m_gridPane);
		return group;
	}

}
