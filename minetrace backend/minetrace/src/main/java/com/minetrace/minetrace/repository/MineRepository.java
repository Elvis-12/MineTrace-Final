package com.minetrace.minetrace.repository;

import com.minetrace.minetrace.entity.Mine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MineRepository extends JpaRepository<Mine, Long> {
    List<Mine> findByActiveTrue();
}
