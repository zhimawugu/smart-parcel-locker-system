package com.smartparcel.locker.service.impl;

import com.smartparcel.locker.dao.LockerDao;
import com.smartparcel.locker.dao.LockerStationDao;
import com.smartparcel.locker.dao.ParcelDao;
import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.Parcel;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.LockerStatus;
import com.smartparcel.locker.enums.ParcelStatus;
import com.smartparcel.locker.exception.*;
import com.smartparcel.locker.service.utils.EmailSender;
import com.smartparcel.locker.service.ParcelService;
import com.smartparcel.locker.vo.OpenLockerResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Default {@link ParcelService} implementation. Each method owns its transaction
 * so that locker-status changes and parcel writes commit atomically.
 */
@Service
public class ParcelServiceImpl implements ParcelService {
    private static final int CODE_GENERATION_ATTEMPTS = 10;
    private static final Duration DOOR_OPEN_TIMEOUT = Duration.ofMinutes(3);
    private final SecureRandom random = new SecureRandom();
    @Resource
    private UserDao userDao;
    @Resource
    private LockerStationDao stationDao;
    @Resource
    private LockerDao lockerDao;
    @Resource
    private ParcelDao parcelDao;

    @Override
    @Transactional
    public OpenLockerResponse openLocker(StoreParcelRequest request) {
        releaseExpiredDoorOpenLockers();

        User recipient = userDao.findByEmail(request.getRecipientEmail())
                .orElseThrow(RecipientNotFoundException::new);
        stationDao.findById(request.getStationId())
                .orElseThrow(StationNotFoundException::new);

        Locker locker = selectLocker(request.getStationId(), request.getSize());
        locker.setStatus(LockerStatus.DOOR_OPEN);
        locker.setDoorOpenedAt(Instant.now());
        lockerDao.save(locker);

        Parcel parcel = new Parcel(
                recipient.getId(),
                locker.getId(),
                request.getSize(),
                generateUniqueCollectionCode(),
                ParcelStatus.WAITING_FOR_COLLECTION,
                request.getDescription());
        parcel = parcelDao.save(parcel);

        return OpenLockerResponse.of(parcel, locker);
    }

    @Override
    @Transactional
    public void closeLocker(Long parcelId) {
        Parcel parcel = parcelDao.findById(parcelId).orElseThrow(ParcelNotFoundException::new);
        Locker locker = requireOpenLocker(parcel.getLockerId());

        locker.setStatus(LockerStatus.OCCUPIED);
        locker.setDoorOpenedAt(null);
        lockerDao.save(locker);

        User recipient = userDao.findById(parcel.getRecipientId())
                .orElseThrow(RecipientNotFoundException::new);
        EmailSender.send(recipient.getEmail(), "Your parcel collection code",
                "Your parcel is ready. Collection code: " + parcel.getCollectionCode());
    }

    @Override
    @Transactional
    public void cancel(Long parcelId) {
        Parcel parcel = parcelDao.findById(parcelId).orElseThrow(ParcelNotFoundException::new);
        Locker locker = requireOpenLocker(parcel.getLockerId());

        locker.setStatus(LockerStatus.AVAILABLE);
        locker.setDoorOpenedAt(null);
        lockerDao.save(locker);
        parcelDao.delete(parcel);
    }

    /**
     * Frees lockers left in DOOR_OPEN past the timeout, deleting their pending parcels.
     */
    private void releaseExpiredDoorOpenLockers() {
        Instant threshold = Instant.now().minus(DOOR_OPEN_TIMEOUT);
        for (Locker locker : lockerDao.findExpiredDoorOpen(threshold)) {
            parcelDao.findByLockerIdAndStatus(locker.getId(), ParcelStatus.WAITING_FOR_COLLECTION)
                    .ifPresent(parcelDao::delete);
            locker.setStatus(LockerStatus.AVAILABLE);
            locker.setDoorOpenedAt(null);
            lockerDao.save(locker);
        }
    }

    private Locker requireOpenLocker(Long lockerId) {
        Locker locker = lockerDao.findById(lockerId).orElseThrow(LockerNotOpenException::new);
        if (locker.getStatus() != LockerStatus.DOOR_OPEN) {
            throw new LockerNotOpenException();
        }

        return locker;
    }

    private Locker selectLocker(Long stationId, LockerSize requested) {
        List<Locker> available = lockerDao.findAvailableByStation(stationId);
        Locker larger = null;

        for (Locker locker : available) {
            if (locker.getSize() == requested) {
                return locker;
            }
            if (locker.getSize().ordinal() > requested.ordinal()
                    && (larger == null || locker.getSize().ordinal() < larger.getSize().ordinal())) {
                larger = locker;
            }
        }

        if (larger == null) {
            throw new NoLockerAvailableException();
        }

        return larger;
    }

    private String generateUniqueCollectionCode() {
        for (int i = 0; i < CODE_GENERATION_ATTEMPTS; i++) {
            String code = String.format("%06d", random.nextInt(1_000_000));
            if (!parcelDao.existsByCollectionCode(code)) {
                return code;
            }
        }

        throw new IllegalStateException("Unable to generate a unique collection code");
    }
}
