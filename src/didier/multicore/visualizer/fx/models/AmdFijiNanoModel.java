package didier.multicore.visualizer.fx.models;

import java.util.ArrayList;
import java.util.List;

public class AmdFijiNanoModel {
	
	public static class GpuStreamingElement {
		private int id;		
		public int getId() { return id; }
		public void setId(int _id) { id = _id; }
		
		private ArrayList<GpuComputeUnit> m_cus;

		public ArrayList<GpuComputeUnit> get_cus() {
			return m_cus;
		}

		public void set_cus(ArrayList<GpuComputeUnit> m_cus) {
			this.m_cus = m_cus;
		}
		
		public GpuStreamingElement(int no_cus, int simdPerCu, int corePerSimd) {
			m_cus = new ArrayList<>();
			for(int i=0; i<no_cus; i++) {
				m_cus.add(new GpuComputeUnit(simdPerCu,  corePerSimd));
			}
		}
	}
	
	public static class GpuComputeUnit {
		private int id;		
		public int getId() { return id; }
		public void setId(int _id) { id = _id; }
		
		public GpuComputeUnit(int no_simd, int corePerSIMD) {
			m_simds = new ArrayList<>(no_simd);
			for(int i = 0; i< no_simd; i++) {
				m_simds.add(new GpuSIMD(corePerSIMD));
			}
		}
		
		private ArrayList<GpuSIMD> m_simds;

		public ArrayList<GpuSIMD> get_simds() {
			return m_simds;
		}

		public void set_simds(ArrayList<GpuSIMD> m_simds) {
			this.m_simds = m_simds;
		}		
	}
	
	public static class GpuSIMD {
		private int id;		
		public int getId() { return id; }
		public void setId(int _id) { id = _id; }
		
		private int no_cores;
		
		public GpuSIMD(int _no_cores) {
			no_cores = _no_cores;
		}
		public int getNo_cores() {
			return no_cores;
		}
	}

	private int no_se = 4;
	private int cuPerSe = 16;
	private int simdPerCu = 4;
	private int corePerSimd = 16;
	
	private List<GpuStreamingElement> m_se;
	
	public AmdFijiNanoModel() {
		m_se = new ArrayList<>(no_se);
		for(int i = 0; i<no_se; i++) {
			m_se.add(new GpuStreamingElement(cuPerSe, simdPerCu, corePerSimd));
		}
	}
	
	public List<GpuStreamingElement> getStreamingEngines() {
		return m_se;
	}
	
	public GpuStreamingElement getSe(int id) {
		return ( id < m_se.size() ? m_se.get(id) : null);
	}
}
