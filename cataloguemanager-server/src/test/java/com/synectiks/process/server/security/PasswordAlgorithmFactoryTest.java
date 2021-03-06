/*
 * */
package com.synectiks.process.server.security;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.security.PasswordAlgorithmFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasswordAlgorithmFactoryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PasswordAlgorithm passwordAlgorithm1;
    @Mock
    private PasswordAlgorithm passwordAlgorithm2;

    private Map<String, PasswordAlgorithm> passwordAlgorithms;

    @Before
    public void setUp() throws Exception {
        this.passwordAlgorithms = ImmutableMap.<String, PasswordAlgorithm>builder()
                .put("algorithm1", passwordAlgorithm1)
                .put("algorithm2", passwordAlgorithm2)
                .build();
    }

    @Test
    public void testForPasswordShouldReturnFirstAlgorithm() throws Exception {
        when(passwordAlgorithm1.supports(anyString())).thenReturn(true);

        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);

        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isEqualTo(passwordAlgorithm1);
    }

    @Test
    public void testForPasswordShouldReturnSecondAlgorithm() throws Exception {
        when(passwordAlgorithm1.supports(anyString())).thenReturn(false);
        when(passwordAlgorithm2.supports(anyString())).thenReturn(true);

        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);

        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isEqualTo(passwordAlgorithm2);
    }

    @Test
    public void testForPasswordShouldReturnNull() throws Exception {
        when(passwordAlgorithm1.supports(anyString())).thenReturn(false);
        when(passwordAlgorithm2.supports(anyString())).thenReturn(false);

        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);

        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isNull();
    }

    @Test
    public void testDefaultPasswordAlgorithm() throws Exception {
        final PasswordAlgorithm defaultPasswordAlgorithm = mock(PasswordAlgorithm.class);

        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(Collections.<String, PasswordAlgorithm>emptyMap(),
                defaultPasswordAlgorithm);

        assertThat(passwordAlgorithmFactory.defaultPasswordAlgorithm()).isEqualTo(defaultPasswordAlgorithm);
    }
}