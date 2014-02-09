/*
 	Copyright (c) 2014 code.fm
 	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
 */
package ductive.config;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ductive.stats.StatsProvider;
import ductive.stats.ValueConverter;
import ductive.stats.Values;
import ductive.stats.annotations.Stats;

public class DefaultStats {
	
	@Stats("jvm")
	public Object jvmStats() {

		StatsProvider classLoadingStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
				return Values.add("loaded",bean.getLoadedClassCount()).add("loaded_total",bean.getTotalLoadedClassCount()).add("unloaded",bean.getUnloadedClassCount()).map();
			}
		};
		
		StatsProvider memoryPoolStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				Map<String,Object> result = new HashMap<>();
				List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
				for(MemoryPoolMXBean i : beans) {
					result.put(mangle(i.getName()),
						Values.add("collection_usage",usageToStats(i.getCollectionUsage()))
							.add("peak_usage",usageToStats(i.getPeakUsage()))
							.add("usage",usageToStats(i.getUsage()))
							.map()
					);
				}
				return result;
			}
		};

		
		StatsProvider compilationStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				CompilationMXBean bean = ManagementFactory.getCompilationMXBean();
				if(bean==null)
					return null;
				return Values.add("total_compilation_time",bean.getTotalCompilationTime()).map();
			}
		};
		
		StatsProvider memoryStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
				return Values
						.add("heap_memory_usage",usageToStats(bean.getHeapMemoryUsage()))
						.add("non_heap_memory_usage",usageToStats(bean.getNonHeapMemoryUsage()))
						.add("object_pending_finalization_count",bean.getObjectPendingFinalizationCount())
					.map();
			}
		};
		
		
		StatsProvider gcStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				Map<String,Object> individualStats = new HashMap<>();
				
				long totalCount = 0;
				long totalTime = 0;
				for( GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
					
					long count = bean.getCollectionCount();
					long time = bean.getCollectionTime();
					
					String name = bean.getName();
					
					individualStats.put(mangle(name),Values.add("count",totalCount).add("time",totalTime).map());
					
					totalCount += count;
					totalTime += time;
				}
				
				Map<String,Object> stats = new HashMap<>();
				stats.put("individual",individualStats);
				
				stats.put("total",Values.add("count",totalCount).add("time",totalTime).map());
				
				return stats;
			}
		};
		
		StatsProvider threadStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				ThreadMXBean bean = ManagementFactory.getThreadMXBean();
				
				return Values.add("thread_counts",
						Values.add("total_started",bean.getTotalStartedThreadCount())
							.add("daemon",bean.getDaemonThreadCount())
							.add("peak",bean.getPeakThreadCount())
							.add("current",bean.getThreadCount())
							.map()
						).map();
			}
		};

		StatsProvider runtimeStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
				return Values
						.add("uptime",bean.getUptime())
						.add("start_time",bean.getStartTime())
						.add("management_spec_version",bean.getManagementSpecVersion())
						.add("spec",
								Values.add("name",bean.getSpecName())
								.add("vendor",bean.getSpecVendor())
								.add("version",bean.getSpecVersion())
								.map())
						.add("vm",
								Values.add("name",bean.getVmName())
								.add("vendor",bean.getVmVendor())
								.add("version",bean.getVmVersion())
								.map()
						).map();
			}
		};
		
		StatsProvider osStats = new StatsProvider() {
			@Override public Object query(String[][] paths, ValueConverter[] converters) {
				OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
				return Values
						.add("version",bean.getVersion())
						.add("available_processors",bean.getAvailableProcessors())
						.add("arch",bean.getArch())
						.add("load_average",bean.getSystemLoadAverage())
						.map();
			}
		};

		return Values
				.add("gc",gcStats)
				.add("memory",memoryStats)
				.add("memory_pools",memoryPoolStats)
				.add("compilation",compilationStats)
				.add("class_loading",classLoadingStats)
				.add("threads",threadStats)
				.add("runtime",runtimeStats)
				.add("os",osStats)
			.map();
	}

	protected static Object usageToStats(MemoryUsage usage) {
		if(usage==null)
			return null;
		return Values.add("committed",usage.getCommitted()).add("init",usage.getInit()).add("max",usage.getMax()).add("used",usage.getUsed()).map();
	}

	protected static String mangle(String name) {
		return name.replaceAll("[\\s\\.]","_").toLowerCase(Locale.US);
	}

}
