package cn.promptness.calculus.config.change;

import com.google.common.collect.Sets;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class ConfigChangeEvent extends ApplicationEvent {

    private final Set<String> keyList;


    public ConfigChangeEvent(String... key) {
        super(key);
        this.keyList = Sets.newHashSet(key);
    }

    public Set<String> changedKeys() {
        return keyList;
    }
}
