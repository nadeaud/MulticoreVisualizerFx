package didier.multicore.visualizer.fx.models;

import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.ZestProperties;

public class VisualizerBuilder {
	
	private static int ID = 0;
	
	public static class Builder {
		
		private Map<String, Object> attrs = new HashMap<>();
		private Object key;
		
		public Builder() {			
		}
		
		public VisualizerBuilder.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}
		
		public VisualizerNode buildVisualizerNode() {
			return new VisualizerNode(attrs);
		}
		
	}
	
	private static String genID() {
		return Integer.toString(ID++);
	}
	
	public static VisualizerNode buildNode(Object... attrs) {
		
		Builder builder = new Builder();
		String id = genID();
		builder.attr(ZestProperties.CSS_ID__NE, id).attr(ZestProperties.LABEL__NE, id);
		builder.attr("node-type", "VisualizerNode");
		builder.attr("layout_resizable", true);
		for(int i = 0; i < attrs.length; i += 2) {
			builder.attr(attrs[i].toString(), attrs[i+1]);
		}
		return builder.buildVisualizerNode();		
		
		
		
	}

}
