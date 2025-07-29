package com.regisx001.validationsystem.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.regisx001.validationsystem.domain.entities.AnalyseHistory;

public interface AnalyseHistoryRepository extends JpaRepository<AnalyseHistory, UUID> {

}
