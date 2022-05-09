package cn.promptness.calculus.cache;

import cn.promptness.calculus.enums.FileRecordTypeEnum;
import cn.promptness.calculus.pojo.LocalDbFile;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * DB文件夹名单缓存
 *
 * @author lynn
 * @date 2021/9/23 11:00
 * @since v1.0.0
 */
@Component
@Slf4j
public class LocalDbFileCache {

    private final Table<String, Date, LocalDbFile> localDbFileTable = HashBasedTable.create();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<LocalDbFile> needRemoveList = new CopyOnWriteArrayList<>();
    private final Queue<ScheduledFuture<?>> taskFutures = new ArrayBlockingQueue<>(1);
    private static final Integer CORE_SIZE = Runtime.getRuntime().availableProcessors();


    @Value("${localDbFileCache.checkSecond:60}")
    private Long checkSecond;
    @Value("${localDbFileDmp:../calculus/${spring.profiles.active}/file.dmp}")
    private String localDbFileDmp;
    @Value("${needRemoveDmp:../calculus/${spring.profiles.active}/remove.dmp}")
    private String needRemoveDmp;
    @Value("${localDbFileCountLimit:1000}")
    private Integer localDbFileCountLimit;

    /**
     * 任务分配线程池
     */
    private static final ExecutorService TASK_THREAD_POOL = new ThreadPoolExecutor(
            CORE_SIZE * 2,
            CORE_SIZE * 3,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(CORE_SIZE * 5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Resource
    private TaskScheduler taskScheduler;

    @PostConstruct
    private void init() {
        start();
    }

    @PostConstruct
    public void initNeedRemoveList() {
        needRemoveList.clear();
        File dmpFile = new File(needRemoveDmp);
        if (!dmpFile.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dmpFile.toPath()))) {
            Object object;
            while ((object = ois.readObject()) != null) {
                LocalDbFile localDbFile = (LocalDbFile) object;
                needRemoveList.add(localDbFile);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        log.info("NeedRemoveList大小为{}", needRemoveList.size());
    }

    @PostConstruct
    public void initFileCatalogue() {
        localDbFileTable.clear();
        File dmpFile = new File(localDbFileDmp);
        if (!dmpFile.exists()) {
            return;
        }
        try {
            lock.writeLock().lock();
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dmpFile.toPath()))) {
                Object object;
                while ((object = ois.readObject()) != null) {
                    LocalDbFile localDbFile = (LocalDbFile) object;
                    localDbFileTable.put(localDbFile.getKey(), localDbFile.getBusinessDate(), localDbFile);
                }
                log.info("LocalDbFileTable大小为{}", localDbFileTable.size());
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @PreDestroy
    public void cacheFileCatalogue() {
        try {
            lock.readLock().lock();
            File dmpFile = new File(localDbFileDmp);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dmpFile.toPath()))) {
                for (LocalDbFile localDbFile : localDbFileTable.values()) {
                    oos.writeObject(localDbFile);
                }
                oos.writeObject(null);
                oos.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @PreDestroy
    public void cacheNeedRemove() {
        File dmpFile = new File(needRemoveDmp);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dmpFile.toPath()))) {
            for (LocalDbFile localDbFile : needRemoveList) {
                oos.writeObject(localDbFile);
            }
            oos.writeObject(null);
            oos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public LocalDbFile getLocalDbFile(Integer loanChannelId, Date businessDate, FileRecordTypeEnum fileRecordTypeEnum) {
        try {
            lock.readLock().lock();
            LocalDbFile localDbFile = localDbFileTable.get(String.format("%s-%s", loanChannelId, fileRecordTypeEnum.name()), businessDate);
            if (localDbFile == null) {
                return null;
            }
            localDbFile.setHitTime(new Date());
            return localDbFile;
        } finally {
            lock.readLock().unlock();
        }
    }

    @SneakyThrows
    public void cacheLocalDbFile(LocalDbFile localDbFile) {
        try {
            lock.writeLock().lock();
            try (FileOutputStream fileOutputStream = new FileOutputStream(localDbFile.getLockPath())) {
                fileOutputStream.write(JSON.toJSONStringWithDateFormat(localDbFile, "yyyy-MM-dd HH:mm:ss").getBytes(StandardCharsets.UTF_8));
            }
            localDbFileTable.put(localDbFile.getKey(), localDbFile.getBusinessDate(), localDbFile);

            int removeCount = localDbFileTable.values().size() - localDbFileCountLimit;
            if (removeCount > 0) {
                List<LocalDbFile> collect = localDbFileTable.values().stream().sorted().collect(Collectors.toList());
                for (int i = 0; i < removeCount; i++) {
                    LocalDbFile needRemoveLocalDbFile = collect.get(i);
                    needRemoveList.add(needRemoveLocalDbFile);
                    localDbFileTable.remove(needRemoveLocalDbFile.getKey(), needRemoveLocalDbFile.getBusinessDate());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void start() {
        if (taskFutures.isEmpty()) {
            taskFutures.add(taskScheduler.schedule(this::remove, new PeriodicTrigger(checkSecond, TimeUnit.SECONDS)));
        }
    }


    private void remove() {
        for (LocalDbFile localDbFile : needRemoveList) {
            try {
                TASK_THREAD_POOL.submit(() -> getRetryTemplate().execute((RetryCallback<Boolean, Exception>) retryContext -> {
                    log.info("第{}次删除{}", retryContext.getRetryCount(), new Gson().toJson(localDbFile));
                    // 先删除lock.ok文件
                    FileUtils.forceDelete(new File(localDbFile.getLockPath()));
                    FileUtils.forceDelete(new File(localDbFile.getBasePath()));
                    return needRemoveList.remove(localDbFile);
                }));
            } catch (Throwable e) {
                log.error("删除文件失败{}{}", new Gson().toJson(localDbFile), e.getMessage());
            }
        }
    }

    private RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy(3, Collections.singletonMap(Throwable.class, true));
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(10000);
        retryTemplate.setRetryPolicy(policy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        return retryTemplate;
    }
}
