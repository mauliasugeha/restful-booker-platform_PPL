package com.automationintesting.unit.service;

import com.automationintesting.db.BookingDB;
import com.automationintesting.model.db.Booking;
import com.automationintesting.model.db.BookingDates;
import com.automationintesting.model.db.CreatedBooking;
import com.automationintesting.model.service.BookingResult;
import com.automationintesting.requests.AuthRequests;
import com.automationintesting.service.BookingService;
import com.automationintesting.service.DateCheckValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class UpdateBookingWhiteBoxTest {

    @Mock
    private AuthRequests authRequests;

    @Mock
    private BookingDB bookingDB;

    @Mock
    private DateCheckValidator dateCheckValidator;

    @InjectMocks
    private BookingService bookingService;

    private final String VALID_TOKEN = "admin_token_123";
    private final String INVALID_TOKEN = "fake_token";
    private final int BOOKING_ID = 1;
    private final int ROOM_ID = 101;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // TC-UNIT-01
    @Test
    public void testUnit1_InvalidToken() throws SQLException {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking validBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(INVALID_TOKEN)).thenReturn(false);

        BookingResult result = bookingService.updateBooking(BOOKING_ID, validBooking, INVALID_TOKEN);

        System.out.println("TC-UNIT-01");
        System.out.println("Expected Output : 403 FORBIDDEN");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    // TC-UNIT-02
    @Test
    public void testUnit2_BookingNotFound() throws SQLException {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking validBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);
        when(dateCheckValidator.isValid(any(BookingDates.class))).thenReturn(true);
        when(bookingDB.checkForBookingConflict(any(Booking.class))).thenReturn(false);
        when(bookingDB.update(anyInt(), any(Booking.class))).thenReturn(null);

        BookingResult result = bookingService.updateBooking(999, validBooking, VALID_TOKEN);

        System.out.println("TC-UNIT-02");
        System.out.println("Expected Output : 404 NOT_FOUND");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        assertNull(result.getCreatedBooking());
    }

    // TC-UNIT-03
    @Test
    public void testUnit3_PastDates() throws SQLException {
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking pastDateBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);
        when(dateCheckValidator.isValid(any(BookingDates.class))).thenReturn(false);

        BookingResult result = bookingService.updateBooking(BOOKING_ID, pastDateBooking, VALID_TOKEN);

        System.out.println("TC-UNIT-03");
        System.out.println("Expected Output : 409 CONFLICT");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    // TC-UNIT-18
    @Test
    public void testUnit4_ReversedDates() throws SQLException {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking reversedDateBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);
        when(dateCheckValidator.isValid(any(BookingDates.class))).thenReturn(false);

        BookingResult result = bookingService.updateBooking(BOOKING_ID, reversedDateBooking, VALID_TOKEN);

        System.out.println("TC-UNIT-18");
        System.out.println("Expected Output : 409 CONFLICT");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    // TC-UNIT-19
    @Test
    public void testPath5_RoomOccupied() throws SQLException {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking validBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);
        when(dateCheckValidator.isValid(any(BookingDates.class))).thenReturn(true);
        when(bookingDB.checkForBookingConflict(any(Booking.class))).thenReturn(true);

        BookingResult result = bookingService.updateBooking(BOOKING_ID, validBooking, VALID_TOKEN);

        System.out.println("TC-UNIT-19");
        System.out.println("Expected Output : 409 CONFLICT");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    // TC-UNIT-20
    @Test
    public void testPath6_UpdateSuccess() throws SQLException {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Booking validBooking = new Booking(ROOM_ID, 2, "Maulia", "Sugeha", true, new BookingDates(checkIn, checkOut));

        when(authRequests.postCheckAuth(VALID_TOKEN)).thenReturn(true);
        when(dateCheckValidator.isValid(any(BookingDates.class))).thenReturn(true);
        when(bookingDB.checkForBookingConflict(any(Booking.class))).thenReturn(false);
        when(bookingDB.update(anyInt(), any(Booking.class))).thenReturn(new CreatedBooking(BOOKING_ID, validBooking));

        BookingResult result = bookingService.updateBooking(BOOKING_ID, validBooking, VALID_TOKEN);

        System.out.println("TC-UNIT-20");
        System.out.println("Expected Output : 200 OK");
        System.out.println("Actual Output   : " + result.getStatus() + "\n");

        assertEquals(HttpStatus.OK, result.getStatus());
    }
}