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
* @AwsXXX - the annotation which binds a class member to a location in AWS Parameter Store or AWS Secrets Manager.

See below for further details.

### Annotations for injecting values from AWS Parameter Store
See AWS's documentation for [AWS Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html) for further information on setting up parameters for your application, and why it's a good idea to do so.

#### Configuration
Use of these annotations requires the following configuration in your `application.yml`:
```yaml
secure-config:
  parameters:
    region: ap-southeast-2
    pathBase: /sampleapi/prod
```
`secure-config.parameters.region` is the region in which your account created the AWS Parameter Store parameters.

`secure-config.parameters.pathBase` is the root of the hierarchy for the parameters used by your application. Do not end with a trailing slash.

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

The value for `name` will be prefixed with the root configured by the `secure-config.parameters.pathBase` property.

### Annotations for injecting values from AWS Secrets Manager

-----

## Further reading

Related:

* [AWS Security Best Practices](https://d0.awsstatic.com/whitepapers/Security/AWS_Security_Best_Practices.pdf) Whitepaper
* [The Right Way to Store Secrets using Parameter Store](https://aws.amazon.com/blogs/mt/the-right-way-to-store-secrets-using-parameter-store/)
* [Using AWS Systems Manager Parameter Store to Secure String parameters in AWS CloudFormation templates](https://aws.amazon.com/blogs/mt/using-aws-systems-manager-parameter-store-secure-string-parameters-in-aws-cloudformation-templates/)

