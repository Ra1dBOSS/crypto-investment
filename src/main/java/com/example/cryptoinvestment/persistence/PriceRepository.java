package com.example.cryptoinvestment.persistence;

import com.example.cryptoinvestment.persistence.entities.PriceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {

    @Modifying
    @Query("DELETE FROM PriceEntity WHERE name = :name")
    void deleteByName(@Param("name") String name);

    @Query("SELECT name FROM PriceEntity "
            + "WHERE (timestamp >= :startTime AND timestamp <= :finishTime) "
            + "GROUP BY name ORDER BY ((MAX(price)-MIN(price))/MIN(price)) DESC LIMIT 1")
    String getNameWithHighestNormalizedRange(@Param("startTime") long startTime, @Param("finishTime") long finishTime);

    @Query("SELECT name FROM PriceEntity GROUP BY name ORDER BY ((MAX(price)-MIN(price))/MIN(price)) DESC")
    List<String> getNamesOrderedByNormalizedRange();
}
