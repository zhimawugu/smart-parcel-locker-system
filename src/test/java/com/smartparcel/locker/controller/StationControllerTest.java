package com.smartparcel.locker.controller;

import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.exception.GlobalExceptionHandler;
import com.smartparcel.locker.service.LockerService;
import com.smartparcel.locker.vo.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.smartparcel.locker.enums.LockerSize.SMALL;
import static com.smartparcel.locker.enums.LockerStatus.AVAILABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link StationController} (FR-02).
 */
@WebMvcTest(StationController.class)
@Import(GlobalExceptionHandler.class)
class StationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LockerService lockerService;
    @Test
    void listStationsReturnsData() throws Exception {
        when(lockerService.listStations())
                .thenReturn(List.of(new LockerStation("Building A", "ST-A", "addr")));

        mockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].code").value("ST-A"));
    }
    @Test
    void availableLockersReturnsData() throws Exception {
        when(lockerService.listAvailableLockers(eq(1L), any()))
                .thenReturn(List.of(new Locker(1L, "A-01", SMALL, AVAILABLE)));

        mockMvc.perform(get("/api/stations/1/lockers/available").param("size", "SMALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].code").value("A-01"));
    }
    @Test
    void availableLockersUnknownStationReturnsStationNotFoundCode() throws Exception {
        when(lockerService.listAvailableLockers(eq(99L), any()))
                .thenThrow(new BizException(ResultCode.STATION_NOT_FOUND));

        mockMvc.perform(get("/api/stations/99/lockers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40420));
    }
}
