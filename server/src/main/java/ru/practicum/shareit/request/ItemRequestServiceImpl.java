package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        requireUser(userId);

        if (dto == null) {
            throw new BadRequestException("body must not be null");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BadRequestException("description must not be blank");
        }

        ItemRequest entity = ItemRequestMapper.toEntity(userId, dto);
        ItemRequest saved = requestRepository.save(entity);

        return ItemRequestMapper.toDto(saved, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        requireUser(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorOrderByCreatedDesc(userId);

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Item>> itemsByRequestId = loadItemsForRequests(requests);

        return requests.stream().map(r -> ItemRequestMapper.toDto(r, itemsByRequestId.get(r.getId()))).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        requireUser(userId);

        if (from < 0 || size <= 0) {
            throw new BadRequestException("Pagination parameters are invalid");
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorNot(userId, pageable).getContent();

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Item>> itemsByRequestId = loadItemsForRequests(requests);

        return requests.stream().map(r -> ItemRequestMapper.toDto(r, itemsByRequestId.get(r.getId()))).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        requireUser(userId);

        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден: " + requestId));

        List<Item> items = itemRepository.findAllByRequest(request.getId());

        return ItemRequestMapper.toDto(request, items);
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("X-Sharer-User-Id header is required");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    private Map<Long, List<Item>> loadItemsForRequests(List<ItemRequest> requests) {
        List<Long> ids = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIn(ids);

        return allItems.stream().collect(Collectors.groupingBy(Item::getRequest));
    }
}
