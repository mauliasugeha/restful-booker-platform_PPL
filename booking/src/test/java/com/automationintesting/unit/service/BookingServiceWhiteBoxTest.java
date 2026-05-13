package com.automationintesting.unit.service;

import com.automationintesting.db.BookingDB;
import com.automationintesting.model.db.Booking;
import com.automationintesting.model.db.BookingDates;
import com.automationintesting.model.db.CreatedBooking;
import com.automationintesting.model.service.BookingResult;
import com.automationintesting.requests.MessageRequests;
import com.automationintesting.service.BookingService;
import com.automationintesting.service.DateCheckValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceWhiteBoxTest {

    @Mock
    private BookingDB bookingDB;

    @Mock
    private DateCheckValidator dateCheckValidator;

    @Mock
    private MessageRequests messageRequests;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPath1() throws SQLException {
        Booking booking = new Booking();
        // Path 1: Tanggal tidak valid
        when(dateCheckValidator.isValid(any())).thenReturn(false);

        BookingResult result = bookingService.createBooking(booking);

        System.out.println("Test Case - TC-UNIT-04");
        System.out.println("Expected : 409 CONFLICT");
        System.out.println("Actual   : " + result.getStatus());

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    @Test
    public void testPath2() throws SQLException {
        Booking booking = new Booking();
        // Path 2: Tanggal valid, tapi ada double booking
        when(dateCheckValidator.isValid(any())).thenReturn(true);
        when(bookingDB.checkForBookingConflict(booking)).thenReturn(true);

        BookingResult result = bookingService.createBooking(booking);

        System.out.println("Test Case - TC-UNIT-05");
        System.out.println("Expected : 409 CONFLICT");
        System.out.println("Actual   : " + result.getStatus());

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    @Test
    public void testPath3() throws SQLException {
        Booking booking = new Booking();
        CreatedBooking createdBooking = new CreatedBooking();

        // Path 3: Sukses booking, email null
        when(dateCheckValidator.isValid(any())).thenReturn(true);
        when(bookingDB.checkForBookingConflict(booking)).thenReturn(false);
        when(bookingDB.create(booking)).thenReturn(createdBooking);

        BookingResult result = bookingService.createBooking(booking);

        System.out.println("Test Case - TC-UNIT-06");
        System.out.println("Expected : 201 CREATED");
        System.out.println("Actual   : " + result.getStatus());

        assertEquals(HttpStatus.CREATED, result.getStatus());
        verify(messageRequests, never()).postMessage(any());
    }

    @Test
    public void testPath4() throws SQLException {
        Booking booking = new Booking();
        booking.setEmail("admin@admin123.com");
        booking.setPhone("08123456789");
        CreatedBooking createdBooking = new CreatedBooking();

        BookingDates dates = new BookingDates();
        dates.setCheckin(java.time.LocalDate.now());
        dates.setCheckout(java.time.LocalDate.now().plusDays(3));
        booking.setBookingDates(dates);
        
        // Path 4: Sukses booking dengan kontak lengkap
        when(dateCheckValidator.isValid(any())).thenReturn(true);
        when(bookingDB.checkForBookingConflict(booking)).thenReturn(false);
        when(bookingDB.create(booking)).thenReturn(createdBooking);

        BookingResult result = bookingService.createBooking(booking);

        System.out.println("Test Case - TC-UNIT-07");
        System.out.println("Expected : 201 CREATED");
        System.out.println("Actual   : " + result.getStatus());

        assertEquals(HttpStatus.CREATED, result.getStatus());
        verify(messageRequests, times(1)).postMessage(any());
    }
}