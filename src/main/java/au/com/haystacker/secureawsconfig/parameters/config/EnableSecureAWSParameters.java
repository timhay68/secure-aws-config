package au.com.haystacker.secureawsconfig.parameters.config;

import au.com.haystacker.secureawsconfig.parameters.annotation.AwsParameterAnnotationProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

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
@Target(ElementType.TYPE)
@Import({ AWSClientBuilderConfiguration.class, AwsParameterStoreConfiguration.class })
@ComponentScan(basePackageClasses = {AwsParameterAnnotationProcessor.class} )
public @interface EnableSecureAWSParameters {
}