package com.base.dbase;

import com.base.dbase.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    @Autowired
    private BackupService backupService;

    // Every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void backupTask() {
        backupService.syncUsers();
        System.out.println("Backup sync done");
    }
}

