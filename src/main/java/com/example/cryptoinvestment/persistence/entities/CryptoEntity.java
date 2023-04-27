package com.example.cryptoinvestment.persistence.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CryptoEntity {

    @Id
    private String name;
    private Double min;
    private Double max;
    private Double oldest;
    private Double newest;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "cryptoEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Exclude
    private Set<PriceEntity> priceValues;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        CryptoEntity cryptoEntity = (CryptoEntity) o;
        return name != null && Objects.equals(name, cryptoEntity.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}





