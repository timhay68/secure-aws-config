# Overview

This project contains annotations for use in injecting values into bean properties from one of two storage locations:
* AWS Secrets Manager or
* AWS Parameter Store.

Project is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

-----

## Usage - installation

### Gradle, Maven

To use the annotations, you need to add the dependency to your project:

```groovy
implementation "au.com.haystacker:secure-aws-config:$secure-aws-config-version"
```

```xml
<dependency>
  <groupId>au.com.haystacker</groupId>
  <artifactId>secure-aws-config</artifactId>
  <version>${secure-aws-config-version}</version>
</dependency>
```

## Usage - coding
Each storage location for your secured configuration data has two associated annotations:
* @EnableSecureXXX to import the appropriate AWS client builder, and the @Configuration corresponding to the location-specific store
* @AwsXXX - the annotation which binds a class field to a location in AWS Parameter Store or AWS Secrets Manager.

See below for further details.

### Annotations for injecting values from AWS Parameter Store
See AWS's documentation for [AWS Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html) for further information on setting up parameters for your application, and why it's a good idea to do so.

#### Configuration
Use of these annotations requires the following configuration in your `application.yml`:
```yaml
secure-aws-config:
  parameters:
    region: ap-southeast-2
    pathBase: /sampleapi/prod
```
`secure-aws-config.parameters.region` is the region in which your account created the AWS Parameter Store parameters.

`secure-aws-config.parameters.pathBase` is the root of the hierarchy for the parameters used by your application. Do not end with a trailing slash.

Please refer to [Providing AWS Credentials](#providing-aws-credentials) for details on providing the AWS credentials required to be able to invoke the AWS Systems Manager SDK APIs.

#### @EnableSecureAWSParameters
This annotation imports the configuration required to build a client which knows how to connect to AWS for the purpose of retrieving values from the AWS Parameter Store.
It also scans for the Spring components which know how to process the `@AwsParameter` annotation, using the configurations to retrieve values from AWS Parameter Store.

In your application, you will have an `@Configuration`-annotated class which uses the `@AwsParameter` annotation to bind a field to an entry in AWS Parameter Store.

You should add `@EnableSecureAWSParameters` to this class. 
For example:
```java
@Configuration
@EnableSecureAWSParameters
public class AwsParameterDbCredentials {

    private static final Logger LOG = LoggerFactory.getLogger(AwsParameterDbCredentials.class);

    @AwsParameter(name = "/db/username")
    private String username;

    @AwsParameter(name = "/db/password")
    private String password;

    public AwsParameterDbCredentials() {
    }

    @Bean
    public DbCredentials dbCredentials() {
        return new DbCredentials(username, password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
```
#### @AwsParameter
This annotation should be applied to a class field to which you wish to bind a value from the AWS Parameter Store.
See above for an example of how to use this annotation.

`@AwsParameter` has one required parameter: `name`, which should be set to the remainder of the path to the AWS Parameter Store parameter.

The value for `name` will be appended to the root configured by the `secure-aws-config.parameters.pathBase` property.

### Annotations for injecting values from AWS Secrets Manager
See AWS's documentation for [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) for further information on setting up secrets for your application, and why it's a good idea to do so.

#### Configuration
Use of these annotations requires the following configuration in your `application.yml`:
```yaml
secure-aws-config:
  secrets:
    region: ap-southeast-2
    secretName: sampleapi/pdev
```
`secure-aws-config.secrets.region` is the region in which your account created the AWS Secrets Manager entry.

`secure-aws-config.secrets.secretName` is the name of the secret containing an entry used by your application.

Please refer to [Providing AWS Credentials](#providing-aws-credentials) for details on providing the AWS credentials required to be able to invoke the AWS Secrets Manager SDK APIs.

#### @EnableSecureAWSSecrets
This annotation imports the configuration required to build a client which knows how to connect to AWS for the purpose of retrieving values from the AWS Secrets Manager.
It also scans for the Spring components which know how to process the `@AwsSecret` annotation, using the configurations to retrieve values from AWS Secrets Manager.

In your application, you will have an `@Configuration`-annotated class which uses the `@AwsSecret` annotation to bind a field to an entry in your AWS Secrets Manager secret.

You should add `@EnableSecureAWSSecrets` to this class. 
For example:
```java
@Configuration
@EnableSecureAWSSecrets
public class AwsSecretDbCredentials {

    private static final Logger LOG = LoggerFactory.getLogger(AwsSecretDbCredentials.class);

    @AwsSecret(secretName = "mysql-username")
    private String username;

    @AwsSecret(secretName = "mysql-password")
    private String password;

    public AwsSecretDbCredentials() {
    }

    @Bean
    public DbCredentials dbCredentials() {
        return new DbCredentials(username, password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
```
#### @AwsSecret
This annotation should be applied to a class field to which you wish to bind an entry's value from AWS Secrets Manager.
See above for an example of how to use this annotation.

`@AwsSecret` has one required parameter: `secretName`, which should be set to the key to an entry in AWS Secrets Manager.

The value for `secretName` is the key to an entry in the secret named by the `secure-aws-config.secrets.secretName` property.

### Providing AWS Credentials
In order for the respective SDKs to be able to execute their API calls, credentials must be provided for a role or user 
with the correct permissions to do so.
The two client builders in this project each require injection of an `AwsCredentialsProvider` bean, to provide those credentials. 

Assuming applications using the features of this project will ultimately be deployed to an EC2 server in AWS, 
a default `AwsCredentialsProvider` bean is provided, which is auto-configured by `AWSSecureConfigConfiguration` in the 
fashion of a Spring Boot Starter. If your application provides an alternative `AwsCredentialsProvider` bean, the default 
one will 'back off'.

It is very useful to be able to provide your own `AwsCredentialsProvider` bean, for example, when running your 
application locally on your own development workstation, in which case you may wish to provide 
a `ProfileCredentialsProvider` bean.

-----

## Further reading

Related:

* [AWS Security Best Practices](https://d0.awsstatic.com/whitepapers/Security/AWS_Security_Best_Practices.pdf) Whitepaper
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [AWS Systems Manager](https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html)
* [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html) 
* [Using Instance Profiles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_switch-role-ec2_instance-profiles.html)
