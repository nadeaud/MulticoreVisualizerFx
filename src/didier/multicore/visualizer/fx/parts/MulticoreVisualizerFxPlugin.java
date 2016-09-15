package didier.multicore.visualizer.fx.parts;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MulticoreVisualizerFxPlugin extends AbstractUIPlugin {
	
	// The plug-in ID
		public static final String PLUGIN_ID = "MulticoreVisualizerFxUIPlugin"; //$NON-NLS-1$

		// The shared instance
		private static MulticoreVisualizerFxPlugin plugin;

		
		/**
		 * The constructor
		 */
		public MulticoreVisualizerFxPlugin() {
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
		 */
		public void start(BundleContext context) throws Exception {
			super.start(context);
			plugin = this;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
		 */
		public void stop(BundleContext context) throws Exception {
			plugin = null;
			super.stop(context);
		}

		/**
		 * Returns the shared instance
		 *
		 * @return the shared instance
		 */
		public static MulticoreVisualizerFxPlugin getDefault() {
			return plugin;
		}
	

}
