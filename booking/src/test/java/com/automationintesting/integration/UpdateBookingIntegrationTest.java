package com.automationintesting.integration;

import com.automationintesting.api.BookingApplication;
import com.automationintesting.db.BookingDB;
import com.automationintesting.model.db.Booking;
import com.automationintesting.model.db.BookingDates;
import com.automationintesting.model.db.CreatedBooking;
import com.automationintesting.model.service.BookingResult;
import com.automationintesting.requests.AuthRequests;
import com.automationintesting.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BookingApplication.class)
public class UpdateBookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingDB bookingDB;

    @MockitoBean
    private AuthRequests authRequests;

    private final String VALID_TOKEN = "admin_token_123";
    private final String INVALID_TOKEN = "fake_token_123";
    private final int ROOM_ID = 101;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(bookingService, "authRequests", authRequests);
    }

    // TC-INT-09
    @Test
    public void testIntegration_UpdateSuccess() throws SQLException {
        LocalDate initialCheckIn = LocalDate.now().plusDays(10);
        LocalDate initialCheckOut = LocalDate.now().plusDays(15);
        Booking initialBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(initialCheckIn, initialCheckOut));

        CreatedBooking dbRecord = bookingDB.create(initialBooking);
        int realBookingId = dbRecord.getBookingid();

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);

        LocalDate newCheckIn = LocalDate.now().plusDays(20);
        LocalDate newCheckOut = LocalDate.now().plusDays(25);
        Booking updatedBookingData = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(newCheckIn, newCheckOut));

        BookingResult result = bookingService.updateBooking(realBookingId, updatedBookingData, VALID_TOKEN);

        System.out.println("TC-INT-09");
        System.out.println("Expected : 200 OK");
        System.out.println("Actual   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertNotNull(result.getCreatedBooking());
        assertEquals(newCheckIn, result.getCreatedBooking().getBooking().getBookingDates().getCheckin());
    }

    // TC-INT-10
    @Test
    public void testIntegration_BookingNotFound() throws SQLException {
        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);

        LocalDate newCheckIn = LocalDate.now().plusDays(5);
        LocalDate newCheckOut = LocalDate.now().plusDays(10);
        Booking updateData = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(newCheckIn, newCheckOut));

        BookingResult result = bookingService.updateBooking(9999, updateData, VALID_TOKEN);

        System.out.println("TC-INT-10");
        System.out.println("Expected : 404 NOT_FOUND");
        System.out.println("Actual   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    // TC-INT-11
    @Test
    public void testIntegration_RoomOccupiedConflict() throws SQLException {
        LocalDate conflictCheckIn = LocalDate.now().plusDays(5);
        LocalDate conflictCheckOut = LocalDate.now().plusDays(10);
        Booking otherBooking = new Booking(ROOM_ID, 2, "Latifa", "Anggia", true, new BookingDates(conflictCheckIn, conflictCheckOut));
        bookingDB.create(otherBooking);

        LocalDate myCheckIn = LocalDate.now().plusDays(20);
        LocalDate myCheckOut = LocalDate.now().plusDays(25);
        Booking myBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(myCheckIn, myCheckOut));
        CreatedBooking myDbRecord = bookingDB.create(myBooking);
        int myBookingId = myDbRecord.getBookingid();

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);

        Booking updateDataConflict = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(conflictCheckIn, conflictCheckOut));
        BookingResult result = bookingService.updateBooking(myBookingId, updateDataConflict, VALID_TOKEN);

        System.out.println("TC-INT-11");
        System.out.println("Expected : 409 CONFLICT");
        System.out.println("Actual   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    // TC-INT-12
    @Test
    public void testIntegration_AuthInvalid() throws SQLException {
        when(authRequests.postCheckAuth(INVALID_TOKEN)).thenReturn(false);

        LocalDate newCheckIn = LocalDate.now().plusDays(5);
        LocalDate newCheckOut = LocalDate.now().plusDays(10);
        Booking updateData = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(newCheckIn, newCheckOut));

        BookingResult result = bookingService.updateBooking(1, updateData, INVALID_TOKEN);

        System.out.println("TC-INT-12");
        System.out.println("Expected : 403 FORBIDDEN");
        System.out.println("Actual   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
        assertNull(result.getCreatedBooking());
    }

    //TC-INT-13
    @Test
    public void testIntegration_InvalidDate() throws SQLException {
        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        Booking updateData = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        BookingResult result = bookingService.updateBooking(1, updateData, VALID_TOKEN);

        System.out.println("TC-INT-13");
        System.out.println("Expected : 403 CONFLICT");
        System.out.println("Actual   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }
}