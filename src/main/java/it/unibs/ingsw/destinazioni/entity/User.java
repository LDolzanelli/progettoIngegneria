package it.unibs.ingsw.destinazioni.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "destinazioni")
public class User {
    @Id
    @Size(max = 50)
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 20)
    @NotNull
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @ColumnDefault("0")
    @Column(name = "first_login")
    private Boolean firstLogin;

}