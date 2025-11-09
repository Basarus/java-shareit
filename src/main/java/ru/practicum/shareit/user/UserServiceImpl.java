package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.ConflictException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public UserDto create(UserDto dto) {
        if (dto == null) throw new BadRequestException("body must not be null");
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new BadRequestException("email must not be blank");
        if (!dto.getEmail().contains("@")) throw new BadRequestException("email must be valid");
        requireEmailUnique(dto.getEmail(), null);

        Long id = seq.incrementAndGet();
        User u = UserMapper.fromDto(dto);
        u.setId(id);
        storage.put(id, u);
        return UserMapper.toDto(u);
    }

    @Override
    public UserDto update(Long userId, UserDto patch) {
        User u = storage.get(userId);
        if (u == null) throw new NotFoundException("User not found: " + userId);

        if (patch.getEmail() != null) {
            if (patch.getEmail().isBlank()) throw new BadRequestException("email must not be blank");
            if (!patch.getEmail().contains("@")) throw new BadRequestException("email must be valid");
            requireEmailUnique(patch.getEmail(), userId);
            u.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            if (patch.getName().isBlank()) throw new BadRequestException("name must not be blank");
            u.setName(patch.getName());
        }
        return UserMapper.toDto(u);
    }

    @Override
    public UserDto get(Long id) {
        User u = storage.get(id);
        if (u == null) throw new NotFoundException("User not found: " + id);
        return UserMapper.toDto(u);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.values().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean exists(Long id) {
        return storage.containsKey(id);
    }

    private void requireEmailUnique(String email, Long selfId) {
        boolean clash = storage.values().stream()
                .anyMatch(u -> u.getEmail()
                        .equalsIgnoreCase(email) && !Objects.equals(u.getId(), selfId));
        if (clash) throw new ConflictException("Email already exists: " + email);
    }
}
