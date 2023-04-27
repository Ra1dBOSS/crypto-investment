package com.example.cryptoinvestment.persistence.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PriceEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Double price;
    private Long timestamp;
    @ManyToOne
    @JoinColumn(name = "crypto_id")
    private CryptoEntity cryptoEntity;


    public PriceEntity(String name, Double price, Long timestamp) {
        this.name = name;
        this.price = price;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PriceEntity that = (PriceEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}





