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
    public void testPath1_InvalidMessageId() throws SQLException {
        // Path 1: 1-2-3-16
        MessageResult result = messageService.deleteMessage(-1, "valid_token");

        System.out.println("Test Case : TC-UNIT-12");
        System.out.println("Path      : 1-2-3-16");
        System.out.println("Input     : messageId = -1, authToken = valid_token");
        System.out.println("Expected  : 400 BAD_REQUEST");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.BAD_REQUEST, result.getHttpStatus());
    }

    @Test
    public void testPath2_NullToken() throws SQLException {
        // Path 2: 1-2-4-5-16

        when(authRequests.postCheckAuth(null)).thenReturn(false);

        MessageResult result = messageService.deleteMessage(1, null);

        System.out.println("Test Case : TC-UNIT-13");
        System.out.println("Path      : 1-2-4-5-16");
        System.out.println("Input     : messageId = 1, authToken = null");
        System.out.println("Expected  : 403 FORBIDDEN");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.FORBIDDEN, result.getHttpStatus());
    }

    @Test
    public void testPath3_InvalidToken() throws SQLException {
        // Path 3: 1-2-4-6-7-8-16
        when(authRequests.postCheckAuth("invalid_token")).thenReturn(false);

        MessageResult result = messageService.deleteMessage(1, "invalid_token");

        System.out.println("Test Case : TC-UNIT-14");
        System.out.println("Path      : 1-2-4-6-7-8-16");
        System.out.println("Input     : messageId = 1, authToken = invalid_token");
        System.out.println("Expected  : 403 FORBIDDEN");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.FORBIDDEN, result.getHttpStatus());
    }

    @Test
    public void testPath4_MessageNotFound() throws SQLException {
        // Path 4: 1-2-4-6-7-9-10-11-16
        when(authRequests.postCheckAuth("valid_token")).thenReturn(true);
        when(messageDB.delete(999)).thenReturn(false);

        MessageResult result = messageService.deleteMessage(999, "valid_token");

        System.out.println("Test Case : TC-UNIT-15");
        System.out.println("Path      : 1-2-4-6-7-9-10-11-16");
        System.out.println("Input     : messageId = 999, authToken = valid_token");
        System.out.println("Expected  : 404 NOT_FOUND");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus());
    }

    @Test
    public void testPath5_DeleteFailed() throws SQLException {
        // Path 5: 1-2-4-6-7-9-10-12-13-14-16
        when(authRequests.postCheckAuth("valid_token")).thenReturn(true);
        when(messageDB.delete(1)).thenReturn(false);

        MessageResult result = messageService.deleteMessage(1, "valid_token");

        System.out.println("Test Case : TC-UNIT-16");
        System.out.println("Path      : 1-2-4-6-7-9-10-12-13-14-16");
        System.out.println("Input     : messageId = 1, authToken = valid_token, delete gagal");
        System.out.println("Expected  : 500 INTERNAL_SERVER_ERROR");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getHttpStatus());
    }

    @Test
    public void testPath6_DeleteSuccess() throws SQLException {
        // Path 6: 1-2-4-6-7-9-10-12-13-15-16
        when(authRequests.postCheckAuth("valid_token")).thenReturn(true);
        when(messageDB.delete(1)).thenReturn(true);

        MessageResult result = messageService.deleteMessage(1, "valid_token");

        System.out.println("Test Case : TC-UNIT-17");
        System.out.println("Path      : 1-2-4-6-7-9-10-12-13-15-16");
        System.out.println("Input     : messageId = 1, authToken = valid_token, delete berhasil");
        System.out.println("Expected  : 202 ACCEPTED");
        System.out.println("Actual    : " + result.getHttpStatus());

        assertEquals(HttpStatus.ACCEPTED, result.getHttpStatus());
    }
}
