package au.com.haystacker.secureawsconfig.parameters.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation at the field level which indicates that the field to which
 * it is applied should be bound to an entry in AWS Parameter Store.
 *
 * @author Tim Hay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AwsParameter {
    /**
     * The remainder of the path to the AWS Parameter Store parameter.
     * This will be prefixed with the root configured by the {@code secure-aws-config.parameters.pathBase} property.
     */
    String name();
}