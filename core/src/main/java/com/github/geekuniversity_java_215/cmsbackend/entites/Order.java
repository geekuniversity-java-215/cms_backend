package com.github.geekuniversity_java_215.cmsbackend.entites;

import com.github.geekuniversity_java_215.cmsbackend.entites.base.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_order")
public class Order extends AbstractEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "delivery_order_id_seq")
    protected Long id;


    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;


    @ManyToOne
    @JoinColumn(name="courier_id")
    private Courier courier;

    @NotNull
    @OneToMany(mappedBy= "order", orphanRemoval = true,
        cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @OrderBy("id ASC")
    private List<Item> itemList = new ArrayList<>();




    public Order (){}

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public List<Item> getItemList() {
        return itemList;
    }


    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", customer=" + customer +
               ", courier=" + courier +
               ", itemList=" + itemList +
               '}';
    }
}