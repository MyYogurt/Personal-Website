package org.moisiadis.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class IPService {
	private static final HashSet<String> ip = new HashSet<>();
	private static final int blockTime = 60000;

	public static boolean contains(final String ip) {
		return IPService.ip.contains(ip);
	}

	public static void add(final String ipAddress) {
		ip.add(ipAddress);
		final Thread thread = new Thread(() -> {
			try {
				Thread.sleep(blockTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ip.remove(ipAddress);
		});
		thread.start();
	}

	public static int getBlockTime() {
		return blockTime;
	}
}
