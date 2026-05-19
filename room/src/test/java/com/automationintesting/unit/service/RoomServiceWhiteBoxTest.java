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
import static org.mockito.Mockito.when;

public class RoomServiceWhiteBoxTest {

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
    public void testPath1() throws SQLException {
        // Path 1: 1-2-3-9 → token null → UNAUTHORIZED
        // Kode asli tidak handle ini → kemungkinan FAIL atau exception

        RoomResult result = roomService.deleteRoom(1, null);

        System.out.println("Test Case - TC-UNIT-08");
        System.out.println("Path     : 1-2-3-9");
        System.out.println("Expected : 401 UNAUTHORIZED");
        System.out.println("Actual   : " + result.getHttpStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getHttpStatus());
    }

    @Test
    public void testPath2() throws SQLException {
        // Path 2: 1-2-4-5-9 → token ada tapi invalid → FORBIDDEN
        when(authRequests.postCheckAuth("invalid-token")).thenReturn(false);

        RoomResult result = roomService.deleteRoom(1, "invalid-token");

        System.out.println("Test Case - TC-UNIT-09");
        System.out.println("Path     : 1-2-4-5-9");
        System.out.println("Expected : 403 FORBIDDEN");
        System.out.println("Actual   : " + result.getHttpStatus());
        assertEquals(HttpStatus.FORBIDDEN, result.getHttpStatus());
    }

    @Test
    public void testPath3() throws SQLException {
        // Path 3: 1-2-4-6-7-9 → token valid, roomId tidak ada → NOT_FOUND
        when(authRequests.postCheckAuth("valid-token")).thenReturn(true);
        when(roomDB.delete(999)).thenReturn(false);

        RoomResult result = roomService.deleteRoom(999, "valid-token");

        System.out.println("Test Case - TC-UNIT-10");
        System.out.println("Path     : 1-2-4-6-7-9");
        System.out.println("Expected : 404 NOT_FOUND");
        System.out.println("Actual   : " + result.getHttpStatus());
        assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus());
    }

    @Test
    public void testPath4() throws SQLException {
        // Path 4: 1-2-4-6-8-9 → token valid, roomId ada → ACCEPTED
        when(authRequests.postCheckAuth("valid-token")).thenReturn(true);
        when(roomDB.delete(1)).thenReturn(true);

        RoomResult result = roomService.deleteRoom(1, "valid-token");

        System.out.println("Test Case - TC-UNIT-11");
        System.out.println("Path     : 1-2-4-6-8-9");
        System.out.println("Expected : 202 ACCEPTED");
        System.out.println("Actual   : " + result.getHttpStatus());
        assertEquals(HttpStatus.ACCEPTED, result.getHttpStatus());
    }
}