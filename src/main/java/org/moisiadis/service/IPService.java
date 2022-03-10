package org.moisiadis.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class IPService {
    private static final HashSet<String> ip = new HashSet<String>();
    private static int blockTime = 60000;

    public static boolean contains(String ip) {
        return IPService.ip.contains(ip);
    }

    public static void add(String ipAddress) {
        ip.add(ipAddress);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(blockTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ip.remove(ipAddress);
        });
        thread.start();
    }

    public static void setBlockTime(int blockTime) {
        IPService.blockTime = blockTime;
    }

    public static int getBlockTime() {
        return blockTime;
    }
}
