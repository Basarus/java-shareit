package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.BadRequestException;
import ru.practicum.shareit.common.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto dto) {
        if (dto == null) {
            throw new BadRequestException("body must not be null");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BadRequestException("email must not be blank");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name must not be blank");
        }

        User user = UserMapper.fromDto(dto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto patch) {
        if (id == null) {
            throw new BadRequestException("User id must not be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));

        if (patch == null) {
            return UserMapper.toDto(user);
        }

        if (patch.getName() != null) {
            if (patch.getName().isBlank()) {
                throw new BadRequestException("name must not be blank");
            }
            user.setName(patch.getName());
        }

        if (patch.getEmail() != null) {
            if (patch.getEmail().isBlank()) {
                throw new BadRequestException("email must not be blank");
            }
            user.setEmail(patch.getEmail());
        }

        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto get(Long id) {
        if (id == null) {
            throw new BadRequestException("User id must not be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BadRequestException("User id must not be null");
        }

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return id != null && userRepository.existsById(id);
    }
}
