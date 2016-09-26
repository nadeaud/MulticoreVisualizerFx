package didier.multicore.visualizer.fx.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.dsf.concurrent.ConfinedToDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.ImmediateCountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateDataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IProcesses.IThreadDMData;
import org.eclipse.cdt.dsf.debug.service.IStack.IFrameDMData;
import org.eclipse.cdt.dsf.gdb.launching.GDBProcess;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.MulticoreVisualizerUIPlugin;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCPU;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerCore;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerExecutionState;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerModel;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.model.VisualizerThread;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.ui.view.MulticoreVisualizerSelectionFinder;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.utils.DSFDebugModel;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.utils.DSFSessionState;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.utils.DebugViewUtils;
import org.eclipse.cdt.dsf.gdb.multicorevisualizer.internal.utils.IDSFTargetDataProxy;
import org.eclipse.cdt.dsf.gdb.service.IGDBHardwareAndOS.ICPUDMContext;
import org.eclipse.cdt.dsf.gdb.service.IGDBHardwareAndOS.ICoreDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcessDMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.IDMVMContext;
import org.eclipse.cdt.visualizer.ui.util.SelectionManager;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ISelection;

import didier.multicore.visualizer.fx.view.MulticoreVisualizerFxView;


@SuppressWarnings("restriction")
public class MulticoreVisualizerFx {

	private static final String THE_THREAD_ID_DOES_NOT_CONVERT_TO_AN_INTEGER = "The thread id does not convert to an integer: "; //$NON-NLS-1$

	/**
	 * Proxy to the target data needed to build the model
	 */
	private IDSFTargetDataProxy fTargetData;

	/** DSF debug context session object. */
	protected DSFSessionState m_sessionState;
	
	private SelectionManager m_selectionManager;

	/** View linked with this MulticoreVisualizer */
	private MulticoreVisualizerFxView m_view;
	
	private VisualizerModel m_model;
	
	private Boolean m_initialized = false;

	// This is used to cache the CPU and core
	// contexts, each time the model is recreated.  This way
	// we can avoid asking the backend for the CPU/core
	// geometry each time we want to update the load information.
	protected List<IDMContext> m_cpuCoreContextsCache = null;

	public MulticoreVisualizerFx(MulticoreVisualizerFxView view, boolean gpuVisualizer) {
		m_view = view;
		
		if(!gpuVisualizer) {
			initializeMulticoreVisualizer();
		}
	}
	
	// If there is a debug session started and the Visualizer has been initialized
	private Boolean isInitialized() {
		return m_initialized;
	}
	
	public void initializeMulticoreVisualizer() {
		updateDebugContext();
		
		// If there is no valid session, don't proceed any further
		if(m_sessionState == null)
			return;
		
		fTargetData = new DSFDebugModel();
		m_model = new VisualizerModel(m_sessionState.getSessionID());
		m_cpuCoreContextsCache = new ArrayList<IDMContext>();
		
		getVisualizerModel(m_model);
		m_initialized = true;
	}

	public void updateCanvas(final VisualizerModel model) {
		m_view.resetCanvas(model);
	}

	// --- Visualizer model update methods ---

	/** 
	 * Starts visualizer model request.
	 */
	protected void getVisualizerModel(final VisualizerModel model) {
		m_sessionState.execute(new DsfRunnable() { @Override public void run() {
			// get model asynchronously starting at the top of the hierarchy
			getCPUs(model, new ImmediateRequestMonitor() {
				@Override
				protected void handleCompleted() {
					updateCanvas(model);
				}
			});
		}});
	}

	// --- DSF Context Management ---
	
	/** Trigger a change in the DebugViewSelection based
	 * 	on the selection from the visualizer.
	 */
	public void selectionChanged(ISelection selection) {
		MulticoreVisualizerSelectionFinder selectionFinder = new MulticoreVisualizerSelectionFinder();
		ISelection debugViewSelection = selectionFinder.findSelection(selection);
		DebugViewUtils.setDebugViewSelection(debugViewSelection);
	}

	/** Updates debug context being displayed by canvas.
	 *  Returns true if canvas context actually changes, false if not.
	 */
	public boolean updateDebugContext()
	{			 
		String sessionId = null;
		IAdaptable debugContext = DebugUITools.getDebugContext();
		if (debugContext instanceof IDMVMContext) {
			sessionId = ((IDMVMContext)debugContext).getDMContext().getSessionId();
		} else if (debugContext instanceof GdbLaunch) {
			GdbLaunch gdbLaunch = (GdbLaunch)debugContext;
			if (gdbLaunch.isTerminated() == false) {
				sessionId = gdbLaunch.getSession().getId();
			}
		} else if (debugContext instanceof GDBProcess) {
			ILaunch launch = ((GDBProcess)debugContext).getLaunch();
			if (launch.isTerminated() == false &&
					launch instanceof GdbLaunch) {
				sessionId = ((GdbLaunch)launch).getSession().getId();
			}
		}

		return setDebugSession(sessionId);
	}

	/** Sets debug context being displayed by canvas.
	 *  Returns true if canvas context actually changes, false if not.
	 */
	public boolean setDebugSession(String sessionId) {
		boolean changed = false;

		if (m_sessionState != null &&
				! m_sessionState.getSessionID().equals(sessionId))
		{			
			m_sessionState.dispose();
			m_sessionState = null;
			changed = true;
		}

		if (m_sessionState == null &&
				sessionId != null)
		{
			m_sessionState = new DSFSessionState(sessionId);
			//m_sessionState.addServiceEventListener(fEventListener);
			// start timer that updates the load meters
			changed = true;
		}

		return changed;
	}

	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getCPUs(final VisualizerModel model, final RequestMonitor rm) {
		fTargetData.getCPUs(m_sessionState, new ImmediateDataRequestMonitor<ICPUDMContext[]>() {
			@Override
			protected void handleCompleted() {
				ICPUDMContext[] cpuContexts = isSuccess() ? getData() : null;
				getCores(cpuContexts, model, rm);
			}
		});
	}

	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getCores(ICPUDMContext[] cpuContexts, final VisualizerModel model, final RequestMonitor rm)
	{
		if (cpuContexts == null || cpuContexts.length == 0) {
			// Whoops, no CPU data.
			// We'll fake a CPU and use it to contain any cores we find.

			model.addCPU(new VisualizerCPU(0));

			// Collect core data.
			fTargetData.getCores(m_sessionState, new ImmediateDataRequestMonitor<ICoreDMContext[]>() {
				@Override
				protected void handleCompleted() {
					// Get Cores
					ICoreDMContext[] coreContexts = isSuccess() ? getData() : null;

					ICPUDMContext cpu = null;
					if (coreContexts != null && coreContexts.length > 0) {
						// TODO: This keeps the functionality to the same level before change: 459114, 
						// although it's noted that this does not cover the possibility to have multiple CPU's 
						// within the list of resolved cores
						cpu = DMContexts.getAncestorOfType(coreContexts[0], ICPUDMContext.class);
					}

					// Continue
					getThreads(cpu, coreContexts, model, rm);
				}
			});
		} else {
			// save CPU contexts
			m_cpuCoreContextsCache.addAll(Arrays.asList(cpuContexts));

			final CountingRequestMonitor crm = new ImmediateCountingRequestMonitor(rm);
			crm.setDoneCount(cpuContexts.length);

			for (final ICPUDMContext cpuContext : cpuContexts) {
				int cpuID = Integer.parseInt(cpuContext.getId());
				model.addCPU(new VisualizerCPU(cpuID));

				// Collect core data.
				fTargetData.getCores(m_sessionState, cpuContext, new ImmediateDataRequestMonitor<ICoreDMContext[]>() {
					@Override
					protected void handleCompleted() {
						ICoreDMContext[] coreContexts = isSuccess() ? getData() : null;
						getThreads(cpuContext, coreContexts, model, crm);
					}
				});
			}
		}
	}
	
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getThreads(final ICPUDMContext cpuContext, 
			                  ICoreDMContext[] coreContexts,
			                  final VisualizerModel model,
			                  RequestMonitor rm)
	{
		if (coreContexts == null || coreContexts.length == 0) {
			// no cores for this cpu context
			// That's fine.
			rm.done();
		} else {
			// save core contexts
			m_cpuCoreContextsCache.addAll(Arrays.asList(coreContexts));
			
			int cpuID = Integer.parseInt(cpuContext.getId());
			VisualizerCPU cpu = model.getCPU(cpuID);

			final CountingRequestMonitor crm = new ImmediateCountingRequestMonitor(rm);
			crm.setDoneCount(coreContexts.length);

			for (final ICoreDMContext coreContext : coreContexts) {
				int coreID = Integer.parseInt(coreContext.getId());
				cpu.addCore(new VisualizerCore(cpu, coreID));
				
				// Collect thread data
				fTargetData.getThreads(m_sessionState, cpuContext, coreContext, new ImmediateDataRequestMonitor<IDMContext[]>() {
					@Override
					protected void handleCompleted() {
						IDMContext[] threadContexts = isSuccess() ? getData() : null;
						getThreadData(cpuContext, coreContext, threadContexts, model, crm);
					}
				});
			}			
		}
	}
	
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getThreadData(final ICPUDMContext  cpuContext,
						         final ICoreDMContext coreContext,
							     IDMContext[] threadContexts,
							     final VisualizerModel model,
							     RequestMonitor rm)
	{
		if (threadContexts == null || threadContexts.length == 0) {
			// no threads for this core
			// That's fine.
			rm.done();
		} else {
			final CountingRequestMonitor crm = new ImmediateCountingRequestMonitor(rm);
			crm.setDoneCount(threadContexts.length);

			for (IDMContext threadContext : threadContexts) {
				final IMIExecutionDMContext execContext =
					DMContexts.getAncestorOfType(threadContext, IMIExecutionDMContext.class);
				// Don't add the thread to the model just yet, let's wait until we have its data and execution state.
				// Collect thread data
				fTargetData.getThreadData(m_sessionState, cpuContext, coreContext, execContext, new ImmediateDataRequestMonitor<IThreadDMData>() {
					@Override
					protected void handleCompleted() {
						IThreadDMData threadData = isSuccess() ? getData() : null;
						getThreadExecutionState(cpuContext, coreContext, execContext, threadData, model, crm);
					}
				});
			}
		}
	}
	
	/** Invoked when getThreads() request completes. */
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getThreadExecutionState(final ICPUDMContext cpuContext,
			                               final ICoreDMContext coreContext,
			                               final IMIExecutionDMContext execContext,
			                               final IThreadDMData threadData,
			                               final VisualizerModel model,
			                               final RequestMonitor rm)
	{
		// Get the execution state
		fTargetData.getThreadExecutionState(m_sessionState, cpuContext, coreContext, execContext, 
				threadData, new ImmediateDataRequestMonitor<VisualizerExecutionState>() {
			@Override
			protected void handleCompleted() {
				final VisualizerExecutionState state = isSuccess() ? getData() : null;
				if (state != null && !(state.equals(VisualizerExecutionState.RUNNING)) ) {
					// Get the frame data
					fTargetData.getTopFrameData(m_sessionState, execContext, new ImmediateDataRequestMonitor<IFrameDMData>() {
						@Override
						protected void handleCompleted() {
							IFrameDMData frameData = isSuccess() ? getData() : null;
							getThreadExecutionStateDone(cpuContext, coreContext, execContext, threadData, 
									frameData, state, model, rm);
						}
					});
				} else {
					// frame data is not valid
					getThreadExecutionStateDone(cpuContext, coreContext, execContext, threadData, 
							null, state, model, rm);
				}
			}
		});
	}
	
	/** Invoked when getThreadExecutionState() request completes. */
	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void getThreadExecutionStateDone(ICPUDMContext cpuContext,
			                                   ICoreDMContext coreContext,
			                                   IMIExecutionDMContext execContext,
			                                   IThreadDMData threadData,
			                                   IFrameDMData frame,
			                                   VisualizerExecutionState state,
			                                   VisualizerModel model,
			                                   RequestMonitor rm)
	{
		int cpuID  = Integer.parseInt(cpuContext.getId());
		VisualizerCPU  cpu  = model.getCPU(cpuID);
		int coreID = Integer.parseInt(coreContext.getId());
		VisualizerCore core = cpu.getCore(coreID);
		
		if (state == null) {
			// Unable to obtain execution state.  Assume running
			state = VisualizerExecutionState.RUNNING;
		}

		IMIProcessDMContext processContext =
				DMContexts.getAncestorOfType(execContext, IMIProcessDMContext.class);
		int pid = Integer.parseInt(processContext.getProcId());
		int tid; 
        try {
            tid = Integer.parseInt(execContext.getThreadId());
        } catch (NumberFormatException e) {
            rm.setStatus(new Status(IStatus.ERROR, MulticoreVisualizerUIPlugin.PLUGIN_ID, IStatus.ERROR,
                    "Unxepected thread id format:" + execContext.getThreadId(), e)); //$NON-NLS-1$
            rm.done();
            assert false : THE_THREAD_ID_DOES_NOT_CONVERT_TO_AN_INTEGER + execContext.getThreadId();
            return;
        }

		String osTIDValue = threadData.getId();

		// If we can't get the real Linux OS tid, fallback to using the gdb thread id
		int osTid = (osTIDValue == null) ? tid : Integer.parseInt(osTIDValue);

		// add thread if not already there - there is a potential race condition where a 
		// thread can be added twice to the model: once at model creation and once more 
		// through the listener.   Checking at both places to prevent this.
		VisualizerThread t = model.getThread(tid);
		if (t == null) {
			model.addThread(new VisualizerThread(core, pid, osTid, tid, state, frame));
		}
		// if the thread is already in the model, update it's parameters.  
		else {
			t.setCore(core);
			t.setTID(osTid);
			t.setState(state);
			t.setLocationInfo(frame);
		}
		
		rm.done();
	}
}
