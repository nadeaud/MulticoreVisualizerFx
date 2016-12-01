package didier.multicore.visualizer.fx.internal;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import didier.multicore.visualizer.fx.view.MulticoreVisualizerZestFxRootPart;
import javafx.scene.Node;

public class MulticoreVisualizerFxUIModule extends ZestFxModule {

	private Boolean m_configured = false;
	
	@Override
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
	}

	@Override
	protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindNodePartAdapters(adapterMapBinder);
		/* For now, only the handler on the threads is used to trigger selection changed. */
		//adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(VisualizerNodeSelected.class);	
	}
	
	@Override
	protected void bindRootPartAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(IDomain.CONTENT_VIEWER_ROLE)).to(MulticoreVisualizerZestFxRootPart.class)
				.in(AdaptableScopes.typed(IViewer.class));
	}
	
	@Override
	protected void bindIContentPartFactory() {
		/* For some reason, configure is called twice. Added condition to avoid binding twice, will need to investigate on why it
		 * is called twice. Seems to work in example 
		 * https://github.com/eclipse/gef/blob/master/org.eclipse.gef.zest.examples/src/org/eclipse/gef/zest/examples/CustomNodeExample.java
		 */
		//binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		//	}).toInstance(new VisualizerContentPartFactory());
		binder().bind(IContentPartFactory.class).to(VisualizerContentPartFactory.class)
			.in(AdaptableScopes.typed(IViewer.class));
		//.to(ZestFxContentPartFactory.class).in(AdaptableScopes.typed(IViewer.class));
	}
}
