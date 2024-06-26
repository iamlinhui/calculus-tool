package cn.promptness.calculus.data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Constant {

    String TITLE = "差账分析工具";

    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36";

    AtomicBoolean ENHANCE_SWITCH = new AtomicBoolean(false);

    Date DEFAULT_DATE = new Date(Timestamp.valueOf("1970-01-01 00:00:00").getTime());

    String DATE_FORMAT = "yyyy-MM-dd";

    Integer CORE_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 父线程池
     */
    ExecutorService TASK_THREAD_POOL = new ThreadPoolExecutor(
            CORE_SIZE * 2,
            CORE_SIZE * 3,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(CORE_SIZE * 5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 子线程池
     */
    ExecutorService SUB_TASK_THREAD_POOL = new ThreadPoolExecutor(
            CORE_SIZE * 2,
            CORE_SIZE * 3,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(CORE_SIZE * 10),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

}
