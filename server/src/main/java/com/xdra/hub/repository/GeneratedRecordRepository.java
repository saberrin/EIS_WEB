package com.xdra.hub.repository;

import com.xdra.hub.entity.GeneratedRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedRecordRepository extends JpaRepository<GeneratedRecordEntity, Long> {
    @Query(value = "select * from generated_record where pack_id = :packId and creation_time = " +
            "(select creation_time from generated_record where pack_id = :packId order by creation_time desc limit 1) ", nativeQuery = true)
    List<GeneratedRecordEntity> getPackLatestRecords(@Param("packId") long packId);
}
