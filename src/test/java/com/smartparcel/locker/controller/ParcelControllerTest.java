package com.smartparcel.locker.controller;

import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.exception.GlobalExceptionHandler;
import com.smartparcel.locker.service.ParcelService;
import com.smartparcel.locker.vo.OpenLockerResponse;
import com.smartparcel.locker.vo.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link ParcelController} (FR-02 two-phase flow).
 */
@WebMvcTest(ParcelController.class)
@Import(GlobalExceptionHandler.class)
class ParcelControllerTest {
    private static final String OPEN_BODY =
            "{\"stationId\":1,\"recipientEmail\":\"alice@example.com\",\"size\":\"SMALL\"}";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ParcelService parcelService;
    @Test
    void openReturnsLockerInfoWithoutCode() throws Exception {
        when(parcelService.openLocker(any())).thenReturn(new OpenLockerResponse(1L, 5L, "S-01"));

        mockMvc.perform(post("/api/parcels/open").contentType(APPLICATION_JSON).content(OPEN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.parcelId").value(1))
                .andExpect(jsonPath("$.data.lockerCode").value("S-01"))
                .andExpect(jsonPath("$.data.collectionCode").doesNotExist());
    }
    @Test
    void openMissingStationIdReturnsParamError() throws Exception {
        String body = "{\"recipientEmail\":\"alice@example.com\",\"size\":\"SMALL\"}";

        mockMvc.perform(post("/api/parcels/open").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40000));
    }
    @Test
    void openUnknownRecipientReturnsRecipientNotFoundCode() throws Exception {
        when(parcelService.openLocker(any())).thenThrow(new BizException(ResultCode.RECIPIENT_NOT_FOUND));

        mockMvc.perform(post("/api/parcels/open").contentType(APPLICATION_JSON).content(OPEN_BODY))
                .andExpect(jsonPath("$.code").value(40410));
    }
    @Test
    void openNoLockerReturnsNoLockerAvailableCode() throws Exception {
        when(parcelService.openLocker(any())).thenThrow(new BizException(ResultCode.NO_LOCKER_AVAILABLE));

        mockMvc.perform(post("/api/parcels/open").contentType(APPLICATION_JSON).content(OPEN_BODY))
                .andExpect(jsonPath("$.code").value(42200));
    }
    @Test
    void closeReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/parcels/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
    @Test
    void closeNotOpenReturnsLockerNotOpenCode() throws Exception {
        doThrow(new BizException(ResultCode.LOCKER_NOT_OPEN)).when(parcelService).closeLocker(any());

        mockMvc.perform(post("/api/parcels/1/close"))
                .andExpect(jsonPath("$.code").value(40930));
    }
    @Test
    void cancelReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/parcels/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
