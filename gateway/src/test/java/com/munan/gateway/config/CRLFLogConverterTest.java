package com.munan.gateway.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.MockitoAnnotations;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;

class CRLFLogConverterTest {

    private CRLFLogConverter converter;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        converter = new CRLFLogConverter();
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        reset(mock(ILoggingEvent.class));
        closeable.close();
        converter = null;
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReturnInputStringWhenMarkerListIsEmpty() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getMarkerList()).thenReturn(null);
        when(event.getLoggerName()).thenReturn("org.hibernate.example.Logger");
        String input = "Test input string";

        String result = converter.transform(event, input);

        assertEquals(input, result);
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReturnInputStringWhenMarkersContainCRLFSafeMarker() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        Marker marker = MarkerFactory.getMarker("CRLF_SAFE");
        List<Marker> markers = Collections.singletonList(marker);
        when(event.getMarkerList()).thenReturn(markers);
        String input = "Test input string";

        String result = converter.transform(event, input);

        assertEquals(input, result);
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReturnInputStringWhenMarkersNotContainCRLFSafeMarker() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        Marker marker = MarkerFactory.getMarker("CRLF_NOT_SAFE");
        List<Marker> markers = Collections.singletonList(marker);
        when(event.getMarkerList()).thenReturn(markers);
        when(event.getLoggerName()).thenReturn("org.hibernate.example.Logger");
        String input = "Test input string";

        String result = converter.transform(event, input);

        assertEquals(input, result);
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReturnInputStringWhenLoggerIsSafe() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLoggerName()).thenReturn("org.hibernate.example.Logger");
        String input = "Test input string";

        String result = converter.transform(event, input);

        assertEquals(input, result);
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReplaceNewlinesAndCarriageReturnsWithUnderscoreWhenMarkersDoNotContainCRLFSafeMarkerAndLoggerIsNotSafe() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        List<Marker> markers = Collections.emptyList();
        when(event.getMarkerList()).thenReturn(markers);
        when(event.getLoggerName()).thenReturn("com.mycompany.myapp.example.Logger");
        String input = "Test\ninput\rstring";

        String result = converter.transform(event, input);

        assertEquals("Test_input_string", result);
    }

    @Test
    @Timeout(value = 30)
    void transformShouldReplaceNewlinesAndCarriageReturnsWithAnsiStringWhenMarkersDoNotContainCRLFSafeMarkerAndLoggerIsNotSafeAndAnsiElementIsNotNull() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        List<Marker> markers = Collections.emptyList();
        when(event.getMarkerList()).thenReturn(markers);
        when(event.getLoggerName()).thenReturn("com.mycompany.myapp.example.Logger");
        String input = "Test\ninput\rstring";
        converter.setOptionList(List.of("red"));

        String result = converter.transform(event, input);

        assertEquals("Test_input_string", result);
    }

    @Test
    @Timeout(value = 30)
    void isLoggerSafeShouldReturnTrueWhenLoggerNameStartsWithSafeLogger() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLoggerName()).thenReturn("org.springframework.boot.autoconfigure.example.Logger");

        boolean result = converter.isLoggerSafe(event);

        assertTrue(result);
    }

    @Test
    @Timeout(value = 30)
    void isLoggerSafeShouldReturnFalseWhenLoggerNameDoesNotStartWithSafeLogger() {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLoggerName()).thenReturn("com.mycompany.myapp.example.Logger");
        boolean result = converter.isLoggerSafe(event);

        assertFalse(result);
    }

    @Test
    @Timeout(value = 30)
    void testToAnsiString() {
        AnsiElement ansiElement = AnsiColor.RED;

        String result = converter.toAnsiString("input", ansiElement);

        assertThat(result).isEqualTo("input");
    }
}
