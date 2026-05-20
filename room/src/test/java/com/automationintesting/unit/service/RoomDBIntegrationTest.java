package com.automationintesting.unit.service;

import com.automationintesting.db.RoomDB;
import com.automationintesting.model.service.RoomResult;
import com.automationintesting.requests.AuthRequests;
import com.automationintesting.requests.BookingRequests;
import com.automationintesting.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoomDBIntegrationTest {

    @Mock
    private RoomDB roomDB;

    @Mock
    private AuthRequests authRequests;

    @Mock
    private BookingRequests bookingRequests;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTC_INT_04() throws SQLException {
        // roomId ada di DB → ACCEPTED
        when(authRequests.postCheckAuth("valid-token")).thenReturn(true);
        when(roomDB.delete(1)).thenReturn(true);

        RoomResult result = roomService.deleteRoom(1, "valid-token");

        System.out.println("Test Case - TC-INT-04");
        System.out.println("Modul    : Room → RoomDB");
        System.out.println("Skenario : roomId ada di DB, token valid");
        System.out.println("Expected : 202 ACCEPTED");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.ACCEPTED, result.getHttpStatus());
        // Verifikasi roomDB.delete benar-benar dipanggil dengan roomId=1
        verify(roomDB).delete(1);
    }

    @Test
    public void testTC_INT_05() throws SQLException {
        // roomId tidak ada di DB → NOT_FOUND
        when(authRequests.postCheckAuth("valid-token")).thenReturn(true);
        when(roomDB.delete(999)).thenReturn(false);

        RoomResult result = roomService.deleteRoom(999, "valid-token");

        System.out.println("Test Case - TC-INT-05");
        System.out.println("Modul    : Room → RoomDB");
        System.out.println("Skenario : roomId tidak ada di DB, token valid");
        System.out.println("Expected : 404 NOT_FOUND");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus());
        // Verifikasi roomDB.delete benar-benar dipanggil dengan roomId=999
        verify(roomDB).delete(999);
    }

    @Test
    public void testTC_INT_06() throws SQLException {
        // Token invalid → roomDB tidak boleh dipanggil sama sekali
        when(authRequests.postCheckAuth("invalid-token")).thenReturn(false);

        RoomResult result = roomService.deleteRoom(1, "invalid-token");

        System.out.println("Test Case - TC-INT-06");
        System.out.println("Modul    : Room → RoomDB");
        System.out.println("Skenario : Token invalid, roomDB tidak dipanggil");
        System.out.println("Expected : 403 FORBIDDEN");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.FORBIDDEN, result.getHttpStatus());
        // Verifikasi roomDB.delete TIDAK dipanggil karena token invalid
        verify(roomDB, never()).delete(1);
    }
}