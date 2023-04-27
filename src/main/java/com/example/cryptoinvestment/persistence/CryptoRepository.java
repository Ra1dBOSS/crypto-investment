package com.example.cryptoinvestment.persistence;

import com.example.cryptoinvestment.persistence.entities.CryptoEntity;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CryptoRepository extends JpaRepository<CryptoEntity, String> {

    @Query("SELECT updatedAt FROM CryptoEntity WHERE name = :name")
    Optional<Timestamp> getUpdatedAt(@Param("name") String name);
}
