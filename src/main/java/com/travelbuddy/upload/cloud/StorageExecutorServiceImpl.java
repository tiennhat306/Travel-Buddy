package com.travelbuddy.upload.cloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StorageExecutorServiceImpl implements StorageExecutorService{
    private final AsyncTaskExecutor taskExecutor;

    private final StorageService storageService;

    @Override
    public void makeFilePermanent(String id) {
        execute(() -> storageService.makeFilePermanent(id));
    }

    @Override
    public void deleteFile(String id) {
        execute(() -> storageService.deleteFile(id));
    }

    @Override
    public void deleteFiles(List<String> mediaIdsToDelete) {
        execute(() -> storageService.deleteFiles(mediaIdsToDelete));
    }

    private void execute(Runnable runnable) {
        taskExecutor.execute(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Error while storage task, ERROR: [{}]", e.getMessage(), e);
            }
        });
    }
}
