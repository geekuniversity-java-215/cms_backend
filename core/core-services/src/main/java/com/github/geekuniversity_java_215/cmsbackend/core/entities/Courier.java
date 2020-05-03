package com.github.geekuniversity_java_215.cmsbackend.core.entities;

import com.github.geekuniversity_java_215.cmsbackend.core.entities.base.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courier")
@Data
@EqualsAndHashCode(callSuper=true)
public class Courier extends AbstractEntity {

    @NotNull
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy= "courier", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    protected List<Order> orderList = new ArrayList<>();

    // stub
    private String courierSpecificData;


    protected Courier() {}

    public Courier(@NotNull User user, String courierSpecificData) {
        this.user = user;
        this.courierSpecificData = courierSpecificData;
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}

// TODO: 09.04.2020 Добавить сущность автомобиль и подвязать к курьеру.