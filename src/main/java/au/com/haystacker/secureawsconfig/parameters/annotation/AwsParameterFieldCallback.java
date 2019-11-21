package au.com.haystacker.secureawsconfig.parameters.annotation;

import au.com.haystacker.secureawsconfig.annotation.FieldCallbackBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 *
 * @author Tim Hay
 */
public class AwsParameterFieldCallback extends FieldCallbackBase
{

    private static final Logger LOG = LoggerFactory.getLogger(AwsParameterFieldCallback.class);

    public AwsParameterFieldCallback(ConfigurableListableBeanFactory bf, Object bean, Map<String, String> awsParameters) {
        super(bf, bean, awsParameters);
    }

    @Override
    protected String getDeclaredAnnotationKey(Field field) {
        final String parameterName = field.getDeclaredAnnotation(AwsParameter.class).name();
        LOG.debug("Parameter name = " + parameterName);

        return parameterName;
    }

    @Override
    protected boolean isAnnotationPresent(Field field) {
        return field.isAnnotationPresent(AwsParameter.class);
    }

    public boolean annotatedTypeIsValid(Class<?> field) {
        return (String.class.equals(field));
    }

}