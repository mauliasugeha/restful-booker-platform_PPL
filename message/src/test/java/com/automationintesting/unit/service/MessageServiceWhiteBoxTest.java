package com.automationintesting.unit.service;

import com.automationintesting.db.MessageDB;
import com.automationintesting.model.service.MessageResult;
import com.automationintesting.requests.AuthRequests;
import com.automationintesting.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MessageServiceWhiteBoxTest {

    @Mock
    private MessageDB messageDB;

    @Mock
    private AuthRequests authRequests;

    @Autowired
    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void initialiseMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPath1() throws SQLException {
        // Path 1: 1-2-3-4-7
        when(authRequests.postCheckAuth("abc")).thenReturn(true);
        when(messageDB.delete(1)).thenReturn(true);

        MessageResult result = messageService.deleteMessage(1, "abc");

        System.out.println("Test Case - TC-UNIT-11");
        System.out.println("Path     : 1-2-3-4-7");
        System.out.println("Expected : 202 ACCEPTED");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.ACCEPTED, result.getHttpStatus());
    }

    @Test
    public void testPath2() throws SQLException {
        // Path 2: 1-2-3-5-7
        when(authRequests.postCheckAuth("abc")).thenReturn(true);
        when(messageDB.delete(999)).thenReturn(false);

        MessageResult result = messageService.deleteMessage(999, "abc");

        System.out.println("Test Case - TC-UNIT-12");
        System.out.println("Path     : 1-2-3-5-7");
        System.out.println("Expected : 404 NOT_FOUND");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus());
    }

    @Test
    public void testPath3() throws SQLException {
        // Path 3: 1-2-6-7
        when(authRequests.postCheckAuth("invalid-token")).thenReturn(false);

        MessageResult result = messageService.deleteMessage(1, "invalid-token");

        System.out.println("Test Case - TC-UNIT-13");
        System.out.println("Path     : 1-2-6-7");
        System.out.println("Expected : 403 FORBIDDEN");
        System.out.println("Actual   : " + result.getHttpStatus());

        assertEquals(HttpStatus.FORBIDDEN, result.getHttpStatus());
    }
}