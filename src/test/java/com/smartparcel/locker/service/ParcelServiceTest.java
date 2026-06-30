package com.smartparcel.locker.service;

import com.smartparcel.locker.dao.LockerDao;
import com.smartparcel.locker.dao.LockerStationDao;
import com.smartparcel.locker.dao.ParcelDao;
import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.CollectParcelRequest;
import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import com.smartparcel.locker.entity.Parcel;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.service.impl.ParcelServiceImpl;
import com.smartparcel.locker.service.utils.EmailSender;
import com.smartparcel.locker.vo.CollectInfoResponse;
import com.smartparcel.locker.vo.OpenLockerResponse;
import com.smartparcel.locker.vo.ResultCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.smartparcel.locker.enums.LockerSize.*;
import static com.smartparcel.locker.enums.LockerStatus.*;
import static com.smartparcel.locker.enums.ParcelStatus.*;
import static com.smartparcel.locker.enums.Role.RESIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {
    private final User recipient = new User("alice@example.com", "HASH", "Alice", RESIDENT);
    private final LockerStation station = new LockerStation("Building A", "ST-A", "addr");
    @Mock
    private UserDao userDao;
    @Mock
    private LockerStationDao stationDao;
    @Mock
    private LockerDao lockerDao;
    @Mock
    private ParcelDao parcelDao;
    @InjectMocks
    private ParcelServiceImpl parcelService;

    private StoreParcelRequest request(LockerSize size) {
        StoreParcelRequest request = new StoreParcelRequest();
        request.setStationId(1L);
        request.setRecipientEmail("alice@example.com");
        request.setSize(size);
        return request;
    }

    private void givenRecipientAndStationExist() {
        when(userDao.findByEmail("alice@example.com")).thenReturn(Optional.of(recipient));
        when(stationDao.findById(1L)).thenReturn(Optional.of(station));
    }

    private void givenAvailableLockers(Locker... lockers) {
        when(lockerDao.findAvailableByStation(1L)).thenReturn(List.of(lockers));
    }

    private void givenCodeUniqueAndSaveEchoesBack() {
        when(parcelDao.existsByCollectionCode(anyString())).thenReturn(false);
        when(parcelDao.save(any(Parcel.class))).then(returnsFirstArg());
    }

    @Test
    void openExactSizeOpensDoorAndCreatesParcelWithCode() {
        Locker small = new Locker(1L, "S-01", SMALL, AVAILABLE);
        givenRecipientAndStationExist();
        givenAvailableLockers(small);
        givenCodeUniqueAndSaveEchoesBack();

        OpenLockerResponse response = parcelService.openLocker(request(SMALL));

        assertThat(response.lockerCode()).isEqualTo("S-01");
        assertThat(small.getStatus()).isEqualTo(DOOR_OPEN);
        assertThat(small.getDoorOpenedAt()).isNotNull();

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(parcelDao).save(captor.capture());
        Parcel saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(WAITING_FOR_COLLECTION);
        assertThat(saved.getCollectionCode()).matches("\\d{6}");
    }

    @Test
    void openFallsBackToNextLargerLocker() {
        Locker medium = new Locker(1L, "M-01", MEDIUM, AVAILABLE);
        givenRecipientAndStationExist();
        givenAvailableLockers(medium);
        givenCodeUniqueAndSaveEchoesBack();

        OpenLockerResponse response = parcelService.openLocker(request(SMALL));

        assertThat(response.lockerCode()).isEqualTo("M-01");
        assertThat(medium.getStatus()).isEqualTo(DOOR_OPEN);
    }

    @Test
    void openRejectsWhenNoSuitableLocker() {
        givenRecipientAndStationExist();
        givenAvailableLockers(new Locker(1L, "S-01", SMALL, AVAILABLE));

        assertThatThrownBy(() -> parcelService.openLocker(request(LARGE)))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.NO_LOCKER_AVAILABLE);
    }

    @Test
    void openRejectsUnknownRecipient() {
        when(userDao.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.openLocker(request(SMALL)))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.RECIPIENT_NOT_FOUND);
    }

    @Test
    void openRejectsUnknownStation() {
        when(userDao.findByEmail("alice@example.com")).thenReturn(Optional.of(recipient));
        when(stationDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.openLocker(request(SMALL)))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.STATION_NOT_FOUND);
    }

    @Test
    void openReleasesExpiredDoorOpenLockers() {
        Locker expired = new Locker(1L, "S-99", SMALL, DOOR_OPEN);
        Parcel orphan = new Parcel(7L, 99L, SMALL, "111111", WAITING_FOR_COLLECTION, null);
        when(lockerDao.findExpiredDoorOpen(any())).thenReturn(List.of(expired));
        when(parcelDao.findByLockerIdAndStatus(any(), eq(WAITING_FOR_COLLECTION)))
                .thenReturn(Optional.of(orphan));
        givenRecipientAndStationExist();
        givenAvailableLockers(new Locker(1L, "S-01", SMALL, AVAILABLE));
        givenCodeUniqueAndSaveEchoesBack();

        parcelService.openLocker(request(SMALL));

        assertThat(expired.getStatus()).isEqualTo(AVAILABLE);
        assertThat(expired.getDoorOpenedAt()).isNull();
        verify(parcelDao).delete(orphan);
    }

    @Test
    void closeMarksLockerOccupied() {
        Parcel parcel = new Parcel(7L, 5L, SMALL, "482910", WAITING_FOR_COLLECTION, null);
        Locker locker = new Locker(1L, "S-01", SMALL, DOOR_OPEN);
        when(parcelDao.findById(1L)).thenReturn(Optional.of(parcel));
        when(lockerDao.findById(5L)).thenReturn(Optional.of(locker));
        when(userDao.findById(7L)).thenReturn(Optional.of(recipient));

        try (MockedStatic<EmailSender> mailer = mockStatic(EmailSender.class)) {
            parcelService.closeLocker(1L);
            mailer.verify(() -> EmailSender.send(eq("alice@example.com"), any(), any()));
        }

        assertThat(locker.getStatus()).isEqualTo(OCCUPIED);
        assertThat(locker.getDoorOpenedAt()).isNull();
    }

    @Test
    void closeRejectsWhenLockerNotOpen() {
        Parcel parcel = new Parcel(7L, 5L, SMALL, "482910", WAITING_FOR_COLLECTION, null);
        Locker locker = new Locker(1L, "S-01", SMALL, OCCUPIED);
        when(parcelDao.findById(1L)).thenReturn(Optional.of(parcel));
        when(lockerDao.findById(5L)).thenReturn(Optional.of(locker));

        assertThatThrownBy(() -> parcelService.closeLocker(1L))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.LOCKER_NOT_OPEN);
    }

    @Test
    void closeRejectsUnknownParcel() {
        when(parcelDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.closeLocker(1L))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.PARCEL_NOT_FOUND);
    }

    @Test
    void cancelFreesLockerAndDeletesParcel() {
        Parcel parcel = new Parcel(7L, 5L, SMALL, "482910", WAITING_FOR_COLLECTION, null);
        Locker locker = new Locker(1L, "S-01", SMALL, DOOR_OPEN);
        when(parcelDao.findById(1L)).thenReturn(Optional.of(parcel));
        when(lockerDao.findById(5L)).thenReturn(Optional.of(locker));

        parcelService.cancel(1L);

        assertThat(locker.getStatus()).isEqualTo(AVAILABLE);
        verify(parcelDao).delete(parcel);
    }

    private CollectParcelRequest collectRequest(String code, Long stationId) {
        CollectParcelRequest request = new CollectParcelRequest();
        request.setCollectionCode(code);
        request.setStationId(stationId);
        return request;
    }

    @Test
    void collectReturnsLockerInfoForCodeAtStation() {
        Parcel parcel = new Parcel(7L, 5L, SMALL, "472915", WAITING_FOR_COLLECTION, null);
        Locker locker = new Locker(1L, "B-08", SMALL, OCCUPIED);
        when(parcelDao.findByCollectionCode("472915")).thenReturn(Optional.of(parcel));
        when(lockerDao.findById(5L)).thenReturn(Optional.of(locker));
        when(stationDao.findById(1L)).thenReturn(Optional.of(station));

        CollectInfoResponse info = parcelService.collect(collectRequest("472915", 1L));

        assertThat(info.lockerCode()).isEqualTo("B-08");
        assertThat(info.stationName()).isEqualTo("Building A");
    }

    @Test
    void collectRejectsUnknownCode() {
        when(parcelDao.findByCollectionCode("000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.collect(collectRequest("000000", 1L)))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.INVALID_COLLECTION_CODE);
    }

    @Test
    void collectDoneMarksCollectedAndFreesLocker() {
        Parcel parcel = new Parcel(7L, 5L, SMALL, "472915", WAITING_FOR_COLLECTION, null);
        Locker locker = new Locker(1L, "B-08", SMALL, OCCUPIED);
        when(parcelDao.findById(9L)).thenReturn(Optional.of(parcel));
        when(lockerDao.findById(5L)).thenReturn(Optional.of(locker));

        parcelService.collectDone(9L);

        assertThat(parcel.getStatus()).isEqualTo(COLLECTED);
        assertThat(locker.getStatus()).isEqualTo(AVAILABLE);
    }
}
