package com.github.geekuniversity_java_215.cmsbackend.jrpc_protocol.dto._base;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

import java.io.Serializable;
import java.time.Instant;

@Data
public abstract class AbstractDto implements Serializable {


    protected Long id;

    @Setter(AccessLevel.NONE)
    protected Boolean enabled;

    @Setter(AccessLevel.NONE)
    protected Instant created;

    @Setter(AccessLevel.NONE)
    protected Instant updated;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Instant getCreated() {
//        return created;
//    }
//
//    public void setCreated(Instant created) {
//        this.created = created;
//    }
//
//    public Instant getUpdated() {
//        return updated;
//    }
//
//    public void setUpdated(Instant updated) {
//        this.updated = updated;
//    }

    @Override
    public String toString() {
        return "AbstractDto{" +
            "id=" + id +
            ", created=" + created +
            ", updated=" + updated +
            '}';
    }
}
