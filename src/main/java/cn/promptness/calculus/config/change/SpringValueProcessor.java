package cn.promptness.calculus.config.change;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * SpringValue处理器
 *
 * @author lynn
 * @date 2020/4/22 18:30
 * @since v1.0.0
 */
@Slf4j
@Component
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;
    private final SpringValuePlaceholderHelper springValuePlaceholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public SpringValueProcessor(SpringValuePlaceholderHelper springValuePlaceholderHelper, SpringValueRegistry springValueRegistry) {
        this.springValuePlaceholderHelper = springValuePlaceholderHelper;
        this.springValueRegistry = springValueRegistry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        for (Field field : findAllField(clazz)) {
            processFieldValue(bean, beanName, field);
        }
        for (Method method : findAllMethod(clazz)) {
            processMethodValue(bean, beanName, method);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private List<Field> findAllField(Class clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                res.add(field);
            }
        });
        return res;
    }

    private List<Method> findAllMethod(Class clazz) {
        final List<Method> res = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                res.add(method);
            }
        });
        return res;
    }

    private void processFieldValue(Object bean, String beanName, Field field) {
        // register @Value on field
        Value valueAnnotation = field.getAnnotation(Value.class);
        if (valueAnnotation == null) {
            return;
        }
        Set<String> keys = springValuePlaceholderHelper.extractPlaceholderKeys(valueAnnotation.value());

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, valueAnnotation.value(), bean, beanName, field);
            springValueRegistry.register(beanFactory, key, springValue);
        }
    }

    private void processMethodValue(Object bean, String beanName, Method method) {
        //register @Value on method
        Value valueAnnotation = method.getAnnotation(Value.class);
        if (valueAnnotation == null) {
            return;
        }
        //skip Configuration bean methods
        if (method.getAnnotation(Bean.class) != null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            log.error("Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters", bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
            return;
        }

        Set<String> keys = springValuePlaceholderHelper.extractPlaceholderKeys(valueAnnotation.value());

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, valueAnnotation.value(), bean, beanName, method);
            springValueRegistry.register(beanFactory, key, springValue);
            log.info("Monitoring {}", springValue);
        }
    }
}
