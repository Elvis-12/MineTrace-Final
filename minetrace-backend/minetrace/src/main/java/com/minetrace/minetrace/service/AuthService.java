package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.*;
import com.minetrace.minetrace.entity.Organization;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.OrganizationRepository;
import com.minetrace.minetrace.repository.UserRepository;
import com.minetrace.minetrace.security.JwtUtil;
import com.minetrace.minetrace.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final NotificationRepository notificationRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        String orgName = user.getOrganization() != null ? user.getOrganization().getName() : "";

        return new LoginResponse(
                token,
                String.valueOf(user.getId()),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                orgName,
                user.getStatus().name(),
                user.getCreatedAt().toString()
        );
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setStatus(User.Status.ACTIVE);

        if (request.getOrganizationId() != null && !request.getOrganizationId().isBlank()) {
            Organization org = organizationRepository.findById(Long.parseLong(request.getOrganizationId()))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            user.setOrganization(org);
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(User.Role.valueOf(request.getRole()));

        if (request.getOrganizationId() != null && !request.getOrganizationId().isBlank()) {
            Organization org = organizationRepository.findById(Long.parseLong(request.getOrganizationId()))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            user.setOrganization(org);
        } else {
            user.setOrganization(null);
        }

        return toUserResponse(userRepository.save(user));
    }

    public void changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        // Remove notifications owned by this user first
        notificationRepository.findByUserIdOrderByTimestampDesc(id)
                .forEach(n -> notificationRepository.deleteById(n.getId()));
        userRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                String.valueOf(user.getId()),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getOrganization() != null ? String.valueOf(user.getOrganization().getId()) : null,
                user.getOrganization() != null ? user.getOrganization().getName() : "",
                user.getStatus().name(),
                user.getCreatedAt().toString()
        );
    }
}
