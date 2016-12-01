A new version of the CDT MulticoreVisualizer based on JavaFX and the Eclipse GEF framework.

Installations steps :
1. Follow the guide Getting Started with CDT Development on Eclipsepedia
2. Clone and import this project into Eclipse
3. Install e(fx)clipse from the Neon repository (tested with 2.4.0.2016051121)
3. Set the multicore-visualizer-fx.target as target
4. Modify the policy for Forbidden reference in Deprecated and restricted API 
in Compiler, Error/Warning from error to warning.
5. Add -Dosgi.framework.extensions=org.eclipse.fx.osgi in VM arguments for 
the debug configuration
project as an external JAR in the Libraries.
