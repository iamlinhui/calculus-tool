package cn.promptness.calculus.data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Constant {

    public static final String TITLE = "差账分析工具";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36";

    public static boolean ENHANCE_SWITCH = false;

    public static final Date DEFAULT_DATE = new Date(Timestamp.valueOf("1970-01-01 00:00:00").getTime());

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final Integer CORE_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 任务分配线程池
     */
    public static final ExecutorService TASK_THREAD_POOL = new ThreadPoolExecutor(
            CORE_SIZE * 2,
            CORE_SIZE * 3,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(CORE_SIZE * 5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 任务分配线程池
     */
    public static final ExecutorService SUB_TASK_THREAD_POOL = new ThreadPoolExecutor(
            CORE_SIZE * 2,
            CORE_SIZE * 3,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(CORE_SIZE * 5),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

}
