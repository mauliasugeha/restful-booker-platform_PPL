package com.automationintesting.unit.service;

import com.automationintesting.db.AuthDB;
import com.automationintesting.model.Auth;
import com.automationintesting.model.Decision;
import com.automationintesting.model.Token;
import com.automationintesting.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthServiceWhiteBoxTest {

    @Mock
    private AuthDB authDB;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPath1() throws SQLException {
        Auth auth = new Auth("admin", "password");

        when(authDB.queryCredentials(auth)).thenReturn(true);
        when(authDB.insertToken(any(Token.class))).thenReturn(true);

        Decision result = authService.queryCredentials(auth);

        System.out.println("Test Case - TC-UNIT-O1");
        System.out.println("Expected Output : 200 OK");
        System.out.println("Actual   : " + result.getStatus());
        System.out.println("Token    : " + result.getToken());

        assertEquals(HttpStatus.OK, result.getStatus());
        assertNotNull(result.getToken());
    }

    @Test
    public void testPath2() throws SQLException {
        Auth auth = new Auth("admin", "password");

        when(authDB.queryCredentials(auth)).thenReturn(true);
        when(authDB.insertToken(any(Token.class))).thenReturn(false);

        Decision result = authService.queryCredentials(auth);

        System.out.println("Test Case - TC-UNIT-O2");
        System.out.println("Expected : 500 INTERNAL_SERVER_ERROR");
        System.out.println("Actual   : " + result.getStatus());
        System.out.println("Token    : " + result.getToken());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertNull(result.getToken());
    }

    @Test
    public void testPath3() throws SQLException {
        Auth auth = new Auth("admin", "salah_password");

        when(authDB.queryCredentials(auth)).thenReturn(false);

        Decision result = authService.queryCredentials(auth);

        System.out.println("Test Case - TC-UNIT-O3");
        System.out.println("Expected : 403 FORBIDDEN");
        System.out.println("Actual   : " + result.getStatus());
        System.out.println("Token    : " + result.getToken());

        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
        assertNull(result.getToken());
    }
}