package au.com.haystacker.secureawsconfig.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 *
 * @author Tim Hay
 */
public abstract class FieldCallbackBase implements ReflectionUtils.FieldCallback {

    private static final Logger LOG = LoggerFactory.getLogger(FieldCallbackBase.class);

    private static String ERROR_ENTITY_VALUE_NOT_SAME =
            "The annotation value should have same type as the injected generic type.";

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;
    private Map<String, String> awsEntries;

    public FieldCallbackBase(ConfigurableListableBeanFactory bf, Object bean, Map<String, String> awsEntries) {
        configurableBeanFactory = bf;
        this.bean = bean;
        this.awsEntries = awsEntries;
    }

    protected abstract String getDeclaredAnnotationKey(Field field);

    protected abstract boolean isAnnotationPresent(Field field);

    @Override
    public void doWith(Field field)
            throws IllegalArgumentException, IllegalAccessException {
        if (!isAnnotationPresent(field)) {
            return;
        }

        LOG.debug("Field name = " + field.getName());

        ReflectionUtils.makeAccessible(field);
        Class<?> fieldType = field.getType();

        if (annotatedTypeIsValid(fieldType)) {
            String entryName = getDeclaredAnnotationKey(field);
            String entryValue = awsEntries.get(entryName);
            String beanName = entryName + fieldType.getSimpleName();

            Object beanInstance = getBeanInstance(beanName, entryValue);
            field.set(bean, beanInstance);
        } else {
            throw new IllegalArgumentException(ERROR_ENTITY_VALUE_NOT_SAME);
        }
    }

    public boolean annotatedTypeIsValid(Class<?> field) {
        return (String.class.equals(field));
    }

    public Object getBeanInstance(String beanName, String beanValue) {
        Object registeredBean = null;
        if (!configurableBeanFactory.containsBean(beanName)) {
            LOG.debug("Creating new bean named '{}'.", beanName);

            String toRegister = beanValue;

            registeredBean = configurableBeanFactory.initializeBean(toRegister, beanName);
            configurableBeanFactory.registerSingleton(beanName, registeredBean);
            LOG.debug("Bean named '{}' created successfully.", beanName);
        } else {
            registeredBean = configurableBeanFactory.getBean(beanName);
            LOG.debug(
                    "Bean named '{}' already exists, used as current bean reference.", beanName);
        }
        return registeredBean;
    }
}