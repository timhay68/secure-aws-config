package au.com.haystacker.secureawsconfig.parameters.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

/**
 *
 *
 * @author Tim Hay
 */
@Component
public class AwsParameterAnnotationProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AwsParameterAnnotationProcessor.class);

    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    private Map<String, String> awsParameters;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        scanAwsParameterAnnotation(bean, beanName);

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    protected void scanAwsParameterAnnotation(Object bean, String beanName) {
        configureFieldInjection(bean);
    }

    private void configureFieldInjection(Object bean) {
        Class<?> managedBeanClass = bean.getClass();
        ReflectionUtils.FieldCallback fieldCallback =
                new AwsParameterFieldCallback(configurableBeanFactory, bean, awsParameters);
        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
    }
}