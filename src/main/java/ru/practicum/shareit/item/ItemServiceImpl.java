package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.util.Objects;

import java.time.LocalDateTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private final UserService users;

    public ItemServiceImpl(UserService users, ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.users = users;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        requireUserHeader(ownerId);
        if (!users.exists(ownerId)) throw new NotFoundException("Owner not found:" + ownerId);
        if (dto == null) throw new BadRequestException("body must not be null ");
        if (dto.getAvailable() == null) throw new BadRequestException("available must not be null");
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("name must not be blank");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new BadRequestException("description must not be blank");
        Long id = seq.incrementAndGet();
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + ownerId));

        Item i = ItemMapper.fromDto(dto, owner);
        i.setId(id);
        storage.put(id, i);
        return ItemMapper.toDto(i);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        requireUserHeader(ownerId);
        Item i = storage.get(itemId);
        if (i == null) throw new NotFoundException("Item not found: " + itemId);

        if (!Objects.equals(i.getOwner().getId(), ownerId)) {
            throw new NotFoundException("Item not found for this owner: " + itemId);
        }

        if (patch.getName() != null) {
            if (patch.getName().isBlank()) throw new BadRequestException("name must not be blank");
            i.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            if (patch.getDescription().isBlank()) throw new BadRequestException("description must not be blank");
            i.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            i.setAvailable(patch.getAvailable());
        }

        return ItemMapper.toDto(i);
    }

    @Override
    public ItemDto get(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId).stream().map(CommentMapper::toDto).collect(Collectors.toList());

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(requesterId)) {
            List<Booking> bookings = bookingRepository.findByItem_IdOrderByStartDesc(itemId);
            LocalDateTime now = LocalDateTime.now();

            lastBooking = bookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).filter(b -> !b.getStart().isAfter(now)) // start <= now
                    .max(Comparator.comparing(Booking::getStart)).map(BookingMapper::toShortDto).orElse(null);

            nextBooking = bookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).filter(b -> b.getStart().isAfter(now)).min(Comparator.comparing(Booking::getStart)).map(BookingMapper::toShortDto).orElse(null);
        }

        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwner_Id(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();

        List<Comment> allComments = commentRepository.findByItem_IdInOrderByCreatedDesc(itemIds);
        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream().collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.mapping(CommentMapper::toDto, Collectors.toList())));

        List<Booking> allBookings = bookingRepository.findByItem_Owner_Id(ownerId, Sort.by(Sort.Direction.DESC, "start"));

        Map<Long, List<Booking>> bookingsByItemId = allBookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream().map(item -> {
            List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), List.of());

            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

            BookingShortDto lastBooking = itemBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).filter(b -> !b.getStart().isAfter(now)).max(Comparator.comparing(Booking::getStart)).map(BookingMapper::toShortDto).orElse(null);

            BookingShortDto nextBooking = itemBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).filter(b -> b.getStart().isAfter(now)).min(Comparator.comparing(Booking::getStart)).map(BookingMapper::toShortDto).orElse(null);

            return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long requesterId, String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream().filter(Item::getAvailable).filter(i -> i.getName().toLowerCase().contains(q) || i.getDescription().toLowerCase().contains(q)).sorted(Comparator.comparing(Item::getId)).map(ItemMapper::toDto).toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("Комментарий не может быть пустым");
        }

        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        boolean hasPastApprovedBooking = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasPastApprovedBooking) {
            throw new BadRequestException("Пользователь не брал эту вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = Comment.builder().text(commentDto.getText()).item(item).author(author).created(LocalDateTime.now()).build();

        Comment saved = commentRepository.save(comment);

        return CommentMapper.toDto(saved);
    }

    private void requireUserHeader(Long userId) {
        if (userId == null) throw new BadRequestException("X-Sharer-User-Id header is required");
    }
}
