package au.com.haystacker.secureawsconfig.secrets.annotation;

import au.com.haystacker.secureawsconfig.parameters.annotation.AwsParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AwsSecretFieldCallbackTest {

    @Mock
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Spy
    private ClassWithFields bean = new ClassWithFields();

    @Mock
    private Map<String, String> awsSecrets;

    @InjectMocks
    private AwsSecretFieldCallback target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldIgnoreFieldWithoutAnnotation() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(ClassWithFields.class, "fieldWithoutAnnotations");

        target.doWith(field);

        verifyZeroInteractions(awsSecrets);
    }

    @Test
    public void shouldIgnoreFieldWithOtherAnnotation() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(ClassWithFields.class, "fieldWithOtherAnnotations");

        target.doWith(field);

        verifyZeroInteractions(awsSecrets);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectAnnotatedNonStringField() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(ClassWithFields.class, "nonStringFieldWithAnnotation");

        target.doWith(field);
    }

    @Test
    public void shouldProcessStringFieldWithAnnotation() throws IllegalAccessException {

        final String secretName = "my-secret";
        final String beanName = secretName + "String";
        final String beanValue = "the secret value";
        when(awsSecrets.get(eq(secretName))).thenReturn(beanValue);
        when(configurableBeanFactory.containsBean(eq(beanName))).thenReturn(true);
        when(configurableBeanFactory.getBean(eq(beanName))).thenReturn(beanValue);

        Field field = ReflectionUtils.findField(ClassWithFields.class, "fieldWithAnnotations");

        target.doWith(field);

        verify(awsSecrets).get(eq(secretName));
        assertEquals(beanValue, bean.getFieldWithAnnotations());
    }

    @Test
    public void shouldRegisterSecretFieldOnFirstRequest() {

        when(configurableBeanFactory.containsBean(any())).thenReturn(false);

        String beanName = "mysql-passwordString";
        String beanValue = "password123";
        when(configurableBeanFactory.initializeBean(eq(beanValue), eq(beanName)))
                .thenReturn(beanValue);

        target.getBeanInstance(beanName, beanValue);

        verify(configurableBeanFactory).registerSingleton(eq(beanName), eq(beanValue));
    }

    @Test
    public void shouldGetPreviouslyRegisteredSecretField() {

        when(configurableBeanFactory.containsBean(any())).thenReturn(true);

        String beanName = "mysql-passwordString";
        String beanValue = "password123";
        target.getBeanInstance(beanName, beanValue);

        verify(configurableBeanFactory, never()).registerSingleton(anyString(), anyString());
        verify(configurableBeanFactory).getBean(eq(beanName));
    }

    @Test
    public void annotatedStringShouldBeValid() {
        assertTrue(target.annotatedTypeIsValid(String.class));
    }

    @Test
    public void annotatedNonStringShouldNotBeValid() {
        assertFalse(target.annotatedTypeIsValid(Integer.class));
    }

}

/**
 * Variables of type {@code Field} cannot be mocked, as that class is declared to be final.
 * So let's provide a dummy class containg various annotated members to use in our tests.
 */
class ClassWithFields {

    private String fieldWithoutAnnotations;

    @AwsParameter(name = "my-parameter")
    private String fieldWithOtherAnnotations;

    @AwsSecret(secretKey = "my-bad-type-secret")
    private Integer nonStringFieldWithAnnotation;

    @AwsSecret(secretKey = "my-secret")
    private String fieldWithAnnotations;

    public String getFieldWithAnnotations() {
        return this.fieldWithAnnotations;
    }
}
