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
	protected Group doCreateVisual() {
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
		//m_gridPane.setStyle("-fx-background-color : #1a9850; -fx-border-color: #1a9850; -fx-border-width: 5;");
		m_gridPane.setStyle("-fx-background-color : rgba(0,64,0,1); -fx-border-color: rgba(0,64,0,1); -fx-border-width: 6;");
		m_gridPane.setPrefSize(100, 100);
		m_gridPane.setMouseTransparent(false);
		m_gridPane.setHgap(6.0);
		m_gridPane.setVgap(6.0);
		
		int i=0,j=0;
		if(m_node != null) {
			GpuStreamingElement element = m_node.getElement();
			for(GpuComputeUnit cu : element.getComputeUnits()) {
				
				for(GpuSIMD simd : cu.getSIMDs()) {
					
					if(i == 0) {
						ColumnConstraints cc = new ColumnConstraints();
						cc.setPercentWidth(100.0/(double)cu.getSIMDs().size());
						cc.setFillWidth(true);
						m_gridPane.getColumnConstraints().add(cc);
					}
					StackPane sPane = new StackPane();
					Rectangle rect = new ResizableRectangle(50.0,50.0);
					rect.setStyle("-fx-fill: rgba(0,128,0,0.8);");
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
					hbox.setFillHeight(true);
					hbox.setAlignment(Pos.CENTER_LEFT);
					simd.setHBox(hbox);
					simd.setRectangle(rect);
					sPane.getChildren().add(rect);
					sPane.getChildren().add(hbox);
					sPane.setAlignment(hbox, Pos.CENTER_LEFT);
					m_gridPane.add(sPane, j, i);
					j++;

				}
				j=0;
				i++;
				
				RowConstraints rc = new RowConstraints();
				double percent = 100.0/(double)element.getComputeUnits().size();
				rc.setPercentHeight(percent);
				rc.setFillHeight(true);
				m_gridPane.getRowConstraints().add(rc);
				
			}
			
		}
		group.getChildren().add(m_gridPane);
		return group;
	}

}
