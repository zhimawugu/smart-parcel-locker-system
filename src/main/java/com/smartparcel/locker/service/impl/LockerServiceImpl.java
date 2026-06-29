package com.smartparcel.locker.service.impl;

import com.smartparcel.locker.dao.LockerDao;
import com.smartparcel.locker.dao.LockerStationDao;
import com.smartparcel.locker.dto.CreateLockerRequest;
import com.smartparcel.locker.dto.CreateStationRequest;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.LockerStatus;
import com.smartparcel.locker.exception.StationNotFoundException;
import com.smartparcel.locker.service.LockerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link LockerService} implementation.
 */
@Service
public class LockerServiceImpl implements LockerService {
    @Resource
    private LockerStationDao stationDao;
    @Resource
    private LockerDao lockerDao;

    @Override
    @Transactional
    public LockerStation createStation(CreateStationRequest request) {
        return stationDao.save(
                new LockerStation(request.getName(), request.getCode(), request.getAddress()));
    }

    @Override
    @Transactional
    public Locker createLocker(Long stationId, CreateLockerRequest request) {
        stationDao.findById(stationId).orElseThrow(StationNotFoundException::new);
        LockerStatus status = request.getStatus() != null ? request.getStatus() : LockerStatus.AVAILABLE;
        return lockerDao.save(new Locker(stationId, request.getCode(), request.getSize(), status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LockerStation> listStations() {
        return stationDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Locker> listAvailableLockers(Long stationId, LockerSize size) {
        stationDao.findById(stationId).orElseThrow(StationNotFoundException::new);

        List<Locker> available = lockerDao.findAvailableByStation(stationId);
        if (size == null) {
            return available;
        }
        return available.stream().filter(l -> l.getSize() == size).toList();
    }
}
