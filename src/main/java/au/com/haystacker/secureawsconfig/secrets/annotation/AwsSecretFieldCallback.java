package au.com.haystacker.secureawsconfig.secrets.annotation;

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
public final class AwsSecretFieldCallback extends FieldCallbackBase {

    private static final Logger LOG = LoggerFactory.getLogger(AwsSecretFieldCallback.class);

    public AwsSecretFieldCallback(ConfigurableListableBeanFactory bf, Object bean, Map<String, String> awsSecrets) {
        super(bf, bean, awsSecrets);
    }

    @Override
    protected String getDeclaredAnnotationKey(Field field) {
        final String secretName = field.getDeclaredAnnotation(AwsSecret.class).secretName();
        LOG.debug("Secret name = " + secretName);

        return secretName;
    }

    @Override
    protected boolean isAnnotationPresent(Field field) {
        return field.isAnnotationPresent(AwsSecret.class);
    }

    public boolean annotatedTypeIsValid(final Class<?> field) {
        return (String.class.equals(field));
    }
}