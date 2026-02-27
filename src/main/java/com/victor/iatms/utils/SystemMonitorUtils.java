package com.victor.iatms.utils;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.io.File;

/**
 * 系统监控工具类
 * 使用 OSHI 库获取真实的服务器系统信息
 */
@Slf4j
public class SystemMonitorUtils {

    private static final SystemInfo systemInfo = new SystemInfo();

    /**
     * 获取CPU使用率 (0-100)
     */
    public static int getCpuUsage() {
        try {
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            // 获取CPU负载（需要调用两次，第二次才能获取到准确值）
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            return (int) Math.round(Math.min(100, Math.max(0, cpuLoad)));
        } catch (Exception e) {
            log.warn("获取CPU使用率失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取内存使用率 (0-100)
     */
    public static int getMemoryUsage() {
        try {
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = totalMemory - availableMemory;
            double usagePercent = (double) usedMemory / totalMemory * 100;
            return (int) Math.round(Math.min(100, Math.max(0, usagePercent)));
        } catch (Exception e) {
            log.warn("获取内存使用率失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取磁盘使用率 (0-100)
     * 获取根目录磁盘使用率
     */
    public static int getDiskUsage() {
        try {
            FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
            for (OSFileStore store : fileSystem.getFileStores()) {
                // 获取根目录磁盘使用率
                if ("/".equals(store.getMount()) || "C:\\".equals(store.getMount()) || "C:".equals(store.getMount())) {
                    long totalSpace = store.getTotalSpace();
                    long usableSpace = store.getUsableSpace();
                    long usedSpace = totalSpace - usableSpace;
                    double usagePercent = (double) usedSpace / totalSpace * 100;
                    return (int) Math.round(Math.min(100, Math.max(0, usagePercent)));
                }
            }
            // 如果没找到根目录，返回第一个磁盘的使用率
            if (!fileSystem.getFileStores().isEmpty()) {
                OSFileStore store = fileSystem.getFileStores().iterator().next();
                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                long usedSpace = totalSpace - usableSpace;
                double usagePercent = (double) usedSpace / totalSpace * 100;
                return (int) Math.round(Math.min(100, Math.max(0, usagePercent)));
            }
            return 0;
        } catch (Exception e) {
            log.warn("获取磁盘使用率失败: {}", e.getMessage());
            // 尝试使用Java方式获取
            return getDiskUsageFallback();
        }
    }

    /**
     * 备用方案获取磁盘使用率（使用Java原生方式）
     */
    private static int getDiskUsageFallback() {
        try {
            File[] roots = File.listRoots();
            for (File root : roots) {
                if ("C".equals(root.getCanonicalPath().substring(0, 1))) {
                    long totalSpace = root.getTotalSpace();
                    long freeSpace = root.getFreeSpace();
                    long usedSpace = totalSpace - freeSpace;
                    double usagePercent = (double) usedSpace / totalSpace * 100;
                    return (int) Math.round(Math.min(100, Math.max(0, usagePercent)));
                }
            }
            if (roots.length > 0) {
                File root = roots[0];
                long totalSpace = root.getTotalSpace();
                long freeSpace = root.getFreeSpace();
                long usedSpace = totalSpace - freeSpace;
                double usagePercent = (double) usedSpace / totalSpace * 100;
                return (int) Math.round(Math.min(100, Math.max(0, usagePercent)));
            }
            return 0;
        } catch (Exception e) {
            log.warn("备用获取磁盘使用率失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取操作系统名称
     */
    public static String getOsName() {
        try {
            return System.getProperty("os.name");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 获取系统运行时间（秒）
     */
    public static long getUptime() {
        try {
            return systemInfo.getOperatingSystem().getSystemUptime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取系统健康状态
     * 根据CPU、内存、磁盘使用率综合判断
     */
    public static String getSystemHealth() {
        int cpu = getCpuUsage();
        int memory = getMemoryUsage();
        int disk = getDiskUsage();

        // 如果任一指标超过90%，返回不健康
        if (cpu > 90 || memory > 90 || disk > 90) {
            return "critical";
        }
        // 如果任一指标超过70%，返回警告
        if (cpu > 70 || memory > 70 || disk > 70) {
            return "warning";
        }
        // 否则返回健康
        return "healthy";
    }
}

