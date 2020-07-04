package au.com.haystacker.secureawsconfig.secrets.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 * @author Tim Hay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AwsSecret {
    /**
     * The key to an entry in AWS Secrets Manager.
     * The entry will be found in the secret named by the {@code secure-aws-config.secrets.secretName} property.
     *
     * @return key to an entry in the secret.
     */
    String secretKey();
}
