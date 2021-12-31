package cn.promptness.calculus.config.change;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * SpringValue 变更监听器
 *
 * @author lynn
 * @date 2020/4/22 18:27
 * @since v1.0.0
 */
@Slf4j
@Component
public class SpringValueChangeListener implements ApplicationListener<ConfigChangeEvent> {

    private final boolean typeConverterHasConvertIfNecessaryWithFieldParameter;
    private final ConfigurableBeanFactory beanFactory;
    private final TypeConverter typeConverter;
    private final SpringValuePlaceholderHelper springValuePlaceholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public SpringValueChangeListener(ConfigurableListableBeanFactory beanFactory, SpringValuePlaceholderHelper springValuePlaceholderHelper, SpringValueRegistry springValueRegistry) {
        this.typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();
        this.beanFactory = beanFactory;
        this.typeConverter = this.beanFactory.getTypeConverter();
        this.springValuePlaceholderHelper = springValuePlaceholderHelper;
        this.springValueRegistry = springValueRegistry;
    }

    @Override
    public void onApplicationEvent(ConfigChangeEvent changeEvent) {
        Set<String> keys = changeEvent.changedKeys();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            // 1. check whether the changed key is relevant
            Collection<SpringValue> targetValues = springValueRegistry.get(beanFactory, key);
            if (targetValues == null || targetValues.isEmpty()) {
                continue;
            }

            // 2. update the value
            for (SpringValue val : targetValues) {
                updateSpringValue(val);
            }
        }
    }

    private void updateSpringValue(SpringValue springValue) {
        try {
            Object value = resolvePropertyValue(springValue);
            springValue.update(value);

            log.info("Auto update hippo changed value successfully, new value: {}, {}", value, springValue);
        } catch (Throwable ex) {
            log.error("Auto update hippo changed value failed, {}", springValue.toString(), ex);
        }
    }

    /**
     * Logic transplanted from DefaultListableBeanFactory
     *
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency(org.springframework.beans.factory.config.DependencyDescriptor, String, Set, TypeConverter)
     */
    private Object resolvePropertyValue(SpringValue springValue) {
        // value will never be null, as @Value and @HippoJsonValue will not allow that
        Object value = springValuePlaceholderHelper.resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());
        if (springValue.isField()) {
            // org.springframework.beans.TypeConverter#convertIfNecessary(java.lang.Object, java.lang.Class, java.lang.reflect.Field) is available from Spring 3.2.0+
            if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
            } else {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType());
            }
        } else {
            value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(), springValue.getMethodParameter());
        }
        return value;
    }

    private boolean testTypeConverterHasConvertIfNecessaryWithFieldParameter() {
        try {
            TypeConverter.class.getMethod("convertIfNecessary", Object.class, Class.class, Field.class);
        } catch (Throwable ex) {
            return false;
        }

        return true;
    }
}
