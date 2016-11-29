package didier.multicore.visualizer.fx.models;

import java.util.ArrayList;
import java.util.List;

public class AmdFijiNanoModel {
	
	public static class GpuStreamingElement {
		private int id;		
		private ArrayList<GpuComputeUnit> m_cus;
		
		public GpuStreamingElement(int id, int no_cus, int simdPerCu, int corePerSimd) {
			this.id = id;
			m_cus = new ArrayList<>();
			for(int i=0; i<no_cus; i++) {
				m_cus.add(new GpuComputeUnit(i, simdPerCu,  corePerSimd));
			}
		}
		
		public int getId() { 
			return id; 
		}
		
		public GpuComputeUnit getCu(int id) {
			return id < m_cus.size() ? m_cus.get(id) : null;
		}

		public ArrayList<GpuComputeUnit> get_cus() {
			return m_cus;
		}

		public void set_cus(ArrayList<GpuComputeUnit> m_cus) {
			this.m_cus = m_cus;
		}
		

	}
	
	public static class GpuComputeUnit {
		private int id;	
		private ArrayList<GpuSIMD> m_simds;
		
		public GpuComputeUnit(int id, int no_simd, int corePerSIMD) {
			this.id = id;
			m_simds = new ArrayList<>(no_simd);
			for(int i = 0; i< no_simd; i++) {
				m_simds.add(i, new GpuSIMD(i, corePerSIMD));
			}
		}
		
		public int getId() { 
			return id; 
		}
		
		public GpuSIMD getSIMD(int id) {
			return id < m_simds.size() ? m_simds.get(id) : null;
		}

		public ArrayList<GpuSIMD> get_simds() {
			return m_simds;
		}

		public void set_simds(ArrayList<GpuSIMD> m_simds) {
			this.m_simds = m_simds;
		}		
	}
	
	public static class GpuSIMD {
		// Rectangle object that will be used to modify the display (state, waves, etc)
		private javafx.scene.shape.Rectangle m_rectangle;
		private javafx.scene.layout.HBox m_hbox;
		private int id;		
		private int no_cores;
		
		public GpuSIMD(int id, int _no_cores) {
			this.id = id;
			no_cores = _no_cores;
		}
		
		public int getId() { 
			return id;
		}
		public javafx.scene.layout.HBox getHBox() {
			return m_hbox;
		}
		public void setHBox(javafx.scene.layout.HBox hbox) {
			m_hbox = hbox;
		}
		public int getNo_cores() {
			return no_cores;
		}
		public javafx.scene.shape.Rectangle getRectangle() {
			return m_rectangle;
		}
		public void setRectangle(javafx.scene.shape.Rectangle rectangle) {
			this.m_rectangle = rectangle;
		}
	}

	public static final int no_se = 4;
	public static final int cuPerSe = 16;
	public static final int simdPerCu = 4;
	public static final int corePerSimd = 16;
	
	private ArrayList<GpuStreamingElement> m_se;
	
	public AmdFijiNanoModel() {
		m_se = new ArrayList<>(no_se);
		for(int i = 0; i<no_se; i++) {
			m_se.add(new GpuStreamingElement(i, cuPerSe, simdPerCu, corePerSimd));
		}
	}
	
	public ArrayList<GpuStreamingElement> getStreamingEngines() {
		return m_se;
	}
	
	public GpuStreamingElement getSe(int id) {
		return ( id < m_se.size() ? m_se.get(id) : null);
	}
}
