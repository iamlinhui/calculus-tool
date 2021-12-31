package cn.promptness.calculus.config.change;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SpringValue注册集合
 *
 * @author lynn
 * @date 2020/4/22 18:30
 * @since v1.0.0
 */
@Component
public class SpringValueRegistry {
    private static final long CLEAN_INTERVAL_IN_SECONDS = 5;
    private final Map<BeanFactory, Multimap<String, SpringValue>> registry = Maps.newConcurrentMap();
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Object lock = new Object();
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("ConfigThreadGroup");
    private static final String NAME_PREFIX = "SpringValueRegistry";
    private final AtomicLong threadNumber = new AtomicLong(1L);

    public void register(BeanFactory beanFactory, String key, SpringValue springValue) {
        if (!registry.containsKey(beanFactory)) {
            synchronized (lock) {
                if (!registry.containsKey(beanFactory)) {
                    registry.put(beanFactory, LinkedListMultimap.create());
                }
            }
        }

        registry.get(beanFactory).put(key, springValue);

        // lazy initialize
        if (initialized.compareAndSet(false, true)) {
            initialize();
        }
    }

    public Collection<SpringValue> get(BeanFactory beanFactory, String key) {
        Multimap<String, SpringValue> beanFactorySpringValues = registry.get(beanFactory);
        if (beanFactorySpringValues == null) {
            return null;
        }
        return beanFactorySpringValues.get(key);
    }

    private void initialize() {
        new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(THREAD_GROUP, runnable, THREAD_GROUP.getName() + "-" + NAME_PREFIX + "-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(
                () -> {
                    try {
                        scanAndClean();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }, CLEAN_INTERVAL_IN_SECONDS, CLEAN_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
    }

    private void scanAndClean() {
        Iterator<Multimap<String, SpringValue>> iterator = registry.values().iterator();
        while (!Thread.currentThread().isInterrupted() && iterator.hasNext()) {
            Multimap<String, SpringValue> springValues = iterator.next();
            // clear unused spring values
            springValues.entries().removeIf(springValue -> !springValue.getValue().isTargetBeanValid());
        }
    }
}