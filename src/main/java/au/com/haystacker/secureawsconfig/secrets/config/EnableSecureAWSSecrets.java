package au.com.haystacker.secureawsconfig.secrets.config;

import au.com.haystacker.secureawsconfig.secrets.annotation.AwsSecretAnnotationProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ AWSClientBuilderConfiguration.class, AwsSecretsConfiguration.class })
@ComponentScan(basePackageClasses = {AwsSecretAnnotationProcessor.class} )
public @interface EnableSecureAWSSecrets {
}