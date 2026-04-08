package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.MineRequest;
import com.minetrace.minetrace.dto.MineResponse;
import com.minetrace.minetrace.entity.Mine;
import com.minetrace.minetrace.entity.Organization;
import com.minetrace.minetrace.repository.BatchRepository;
import com.minetrace.minetrace.repository.MineRepository;
import com.minetrace.minetrace.repository.MovementRepository;
import com.minetrace.minetrace.repository.OrganizationRepository;
import com.minetrace.minetrace.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MineService {

    private final MineRepository mineRepository;
    private final OrganizationRepository organizationRepository;
    private final BatchRepository batchRepository;
    private final MovementRepository movementRepository;
    private final VerificationRepository verificationRepository;

    public List<MineResponse> getAll() {
        return mineRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MineResponse create(MineRequest request) {
        Organization org = organizationRepository.findById(Long.parseLong(request.getOrganizationId()))
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Mine mine = new Mine();
        mine.setName(request.getName());
        mine.setLocation(request.getLocation());
        mine.setProvince(request.getProvince());
        mine.setDistrict(request.getDistrict());
        mine.setLicenseNumber(request.getLicenseNumber());
        mine.setOrganization(org);
        mine.setActive(true);

        return toResponse(mineRepository.save(mine));
    }

    public MineResponse update(Long id, MineRequest request) {
        Mine mine = mineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mine not found"));

        Organization org = organizationRepository.findById(Long.parseLong(request.getOrganizationId()))
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        mine.setName(request.getName());
        mine.setLocation(request.getLocation());
        mine.setProvince(request.getProvince());
        mine.setDistrict(request.getDistrict());
        mine.setLicenseNumber(request.getLicenseNumber());
        mine.setOrganization(org);

        return toResponse(mineRepository.save(mine));
    }

    public void toggleActive(Long id) {
        Mine mine = mineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mine not found"));
        mine.setActive(!mine.getActive());
        mineRepository.save(mine);
    }

    @Transactional
    public void delete(Long id) {
        if (!mineRepository.existsById(id)) {
            throw new RuntimeException("Mine not found");
        }
        // Cascade: delete all batches (and their movements/verifications) for this mine
        batchRepository.findByMineId(id).forEach(batch -> {
            verificationRepository.findByBatchIdOrderByTimestampDesc(batch.getId())
                    .forEach(v -> verificationRepository.deleteById(v.getId()));
            movementRepository.findByBatchId(batch.getId())
                    .forEach(m -> movementRepository.deleteById(m.getId()));
            batchRepository.deleteById(batch.getId());
        });
        mineRepository.deleteById(id);
    }

    public long countActive() {
        return mineRepository.findByActiveTrue().size();
    }

    private MineResponse toResponse(Mine mine) {
        return new MineResponse(
                String.valueOf(mine.getId()),
                mine.getName(),
                mine.getLocation(),
                mine.getProvince(),
                mine.getDistrict(),
                mine.getLicenseNumber(),
                mine.getOrganization() != null ? String.valueOf(mine.getOrganization().getId()) : null,
                mine.getOrganization() != null ? mine.getOrganization().getName() : "",
                mine.getActive(),
                mine.getCreatedAt().toString()
        );
    }
}
