package com.github.geekuniversity_java_215.cmsbackend.authserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.User;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.UserRole;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.base.AbstractEntity;
import com.github.geekuniversity_java_215.cmsbackend.protocol.token.TokenType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

//@MappedSuperclass
@Entity
//@AttributeOverride(name="id", column = )
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Table(
    name = "unconfirmed_uzer",
    indexes = {@Index(name = "unconfirmed_uzer_first_name_last_name_unq",
            columnList = "last_name, first_name",unique = true)
    })
@Data
@EqualsAndHashCode(callSuper=true)
public class UnconfirmedUser extends AbstractEntity {

    //public static final Duration TTL = Duration.ofDays(1);

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "user_id_seq")
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull
    @Column(name = "user_name")
    private String username;  // username is approved by dictionary.com //  use email as username ???

    @NotNull
    @Column(name = "password") // bcrypt hash
    private String password;

//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Column(name = "role_id")
    private Set<UserRole> roles = new HashSet<>();

//    // registration confirmation JWT
//    @NotNull
//    private String token;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "email")
    private String email;      // use email as username ??

    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "expired_at", updatable = false)
    @Getter
    protected Instant expiredAt;

    protected UnconfirmedUser() {
        expiredAt = Instant.now().plus(Duration.ofSeconds(TokenType.CONFIRM.getTtl()));
    }

    public UnconfirmedUser(@NotNull String firstName,
                           @NotNull String lastName,
                           @NotNull String email,
                           @NotNull String phoneNumber,
                           @NotNull String username,
                           @NotNull String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public User toUser() {

        return new User(
                username,
                password,
                firstName,
                lastName,
                email,
                phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               '}';
    }

    @JsonIgnore
    public String getFullName() {
        return lastName + firstName;
    }
}
