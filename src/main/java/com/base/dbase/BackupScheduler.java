package com.base.dbase;

import com.base.dbase.services.BackupService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    @Autowired
    private BackupService backupService;

    // Wait 30 seconds on first run, then every 15 minutes after
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 15 * 60 * 1000)
    public void backupTask() {
        System.out.println("[Backup] Scheduler triggered.");
        backupService.tryPushBuffer();
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("[Backup] App shutting down — attempting final backup push...");
        backupService.tryPushBuffer();
    }
}