package com.calendar.factory;

import com.calendar.service.CalendarServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ServiceFactory.
 */
class ServiceFactoryTest {

    @Test
    void testCreateService_Success() {
        CalendarServiceImpl service = ServiceFactory.createService();

        assertNotNull(service);
        assertEquals(0, service.getEventCount());
    }

    @Test
    void testCreateService_ReturnsNewInstance() {
        CalendarServiceImpl service1 = ServiceFactory.createService();
        CalendarServiceImpl service2 = ServiceFactory.createService();

        assertNotSame(service1, service2);
    }

    @Test
    void testCreateService_IndependentInstances() {
        CalendarServiceImpl service1 = ServiceFactory.createService();
        CalendarServiceImpl service2 = ServiceFactory.createService();

        // Add event to service1
        service1.addEvent(com.calendar.model.Event.create("Test",
            java.time.LocalDateTime.of(2025, 12, 15, 10, 0),
            java.time.LocalDateTime.of(2025, 12, 15, 11, 0)));

        // service2 should be independent
        assertEquals(1, service1.getEventCount());
        assertEquals(0, service2.getEventCount());
    }
}
