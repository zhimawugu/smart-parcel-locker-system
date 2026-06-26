package com.smartparcel.locker.controller;

import com.smartparcel.locker.dto.CreateLockerRequest;
import com.smartparcel.locker.dto.CreateStationRequest;
import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.service.LockerService;
import com.smartparcel.locker.vo.ApiResponse;
import com.smartparcel.locker.vo.LockerResponse;
import com.smartparcel.locker.vo.LockerStationResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    @Resource
    private LockerService lockerService;

    @PostMapping
    public ApiResponse<LockerStationResponse> createStation(@Valid @RequestBody CreateStationRequest request) {
        return ApiResponse.success(LockerStationResponse.from(lockerService.createStation(request)));
    }

    @PostMapping("/{stationId}/lockers")
    public ApiResponse<LockerResponse> createLocker(
            @PathVariable Long stationId,
            @Valid @RequestBody CreateLockerRequest request) {
        return ApiResponse.success(LockerResponse.from(lockerService.createLocker(stationId, request)));
    }

    @GetMapping
    public ApiResponse<List<LockerStationResponse>> listStations() {
        List<LockerStationResponse> stations = lockerService.listStations().stream()
                .map(LockerStationResponse::from)
                .toList();
        return ApiResponse.success(stations);
    }

    @GetMapping("/{stationId}/lockers/available")
    public ApiResponse<List<LockerResponse>> availableLockers(
            @PathVariable Long stationId,
            @RequestParam(required = false) LockerSize size) {
        List<LockerResponse> lockers = lockerService.listAvailableLockers(stationId, size).stream()
                .map(LockerResponse::from)
                .toList();
        return ApiResponse.success(lockers);
    }
}
