package com.smartparcel.locker.controller;

import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.service.ParcelService;
import com.smartparcel.locker.vo.ApiResponse;
import com.smartparcel.locker.vo.OpenLockerResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {
    @Resource
    private ParcelService parcelService;

    @PostMapping("/open")
    public ApiResponse<OpenLockerResponse> open(@Valid @RequestBody StoreParcelRequest request) {
        return ApiResponse.success(parcelService.openLocker(request));
    }

    @PostMapping("/{parcelId}/close")
    public ApiResponse<Void> close(@PathVariable Long parcelId) {
        parcelService.closeLocker(parcelId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{parcelId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long parcelId) {
        parcelService.cancel(parcelId);
        return ApiResponse.success(null);
    }
}
