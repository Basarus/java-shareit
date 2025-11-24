package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookingDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new BadRequestException("start и end не могут быть null");
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new BadRequestException("Дата окончания должна быть позже даты начала");
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена: " + dto.getItemId()));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toEntity(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Бронирование не относится к вещам пользователя " + ownerId);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotFoundException("Пользователь не имеет доступа к этому бронированию");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        BookingState bookingState = BookingState.from(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings;

        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findByBooker_Id(userId, sort);
            case CURRENT ->
                    bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case PAST -> bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED ->
                    bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sort);
            default -> throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + ownerId));

        BookingState bookingState = BookingState.from(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings;

        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findByItem_Owner_Id(ownerId, sort);
            case CURRENT ->
                    bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, sort);
            case PAST -> bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, now, sort);
            case FUTURE -> bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, now, sort);
            case WAITING ->
                    bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED ->
                    bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sort);
            default -> throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }
}
