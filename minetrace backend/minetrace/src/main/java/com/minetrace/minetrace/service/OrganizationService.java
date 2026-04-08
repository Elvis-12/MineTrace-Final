package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.OrganizationRequest;
import com.minetrace.minetrace.dto.OrganizationResponse;
import com.minetrace.minetrace.entity.Organization;
import com.minetrace.minetrace.repository.MineRepository;
import com.minetrace.minetrace.repository.OrganizationRepository;
import com.minetrace.minetrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final MineRepository mineRepository;
    private final MineService mineService;

    public List<OrganizationResponse> getAll() {
        return organizationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrganizationResponse create(OrganizationRequest request) {
        Organization org = new Organization();
        org.setName(request.getName());
        org.setAddress(request.getAddress());
        org.setPhone(request.getPhone());
        org.setEmail(request.getEmail());
        Organization saved = organizationRepository.save(org);
        return toResponse(saved);
    }

    public OrganizationResponse update(Long id, OrganizationRequest request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.setName(request.getName());
        org.setAddress(request.getAddress());
        org.setPhone(request.getPhone());
        org.setEmail(request.getEmail());
        return toResponse(organizationRepository.save(org));
    }

    @Transactional
    public void delete(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found");
        }
        // Cascade: delete all mines (and their batches) belonging to this org
        mineRepository.findAll().stream()
                .filter(m -> m.getOrganization() != null && m.getOrganization().getId().equals(id))
                .forEach(m -> mineService.delete(m.getId()));
        // Disassociate users from this org before deleting
        userRepository.findAll().stream()
                .filter(u -> u.getOrganization() != null && u.getOrganization().getId().equals(id))
                .forEach(u -> { u.setOrganization(null); userRepository.save(u); });
        organizationRepository.deleteById(id);
    }

    private OrganizationResponse toResponse(Organization org) {
        long usersCount = userRepository.findAll().stream()
                .filter(u -> u.getOrganization() != null && u.getOrganization().getId().equals(org.getId()))
                .count();
        return new OrganizationResponse(
                String.valueOf(org.getId()),
                org.getName(),
                org.getAddress(),
                org.getPhone(),
                org.getEmail(),
                usersCount,
                org.getCreatedAt().toString()
        );
    }
}
