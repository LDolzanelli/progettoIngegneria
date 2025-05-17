package it.unibs.ingsw.destinazioni.domain.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class User {
    private Integer id;
    private String nickname;
    private String password;
    private String role;
    private boolean firstLogin;

    public User(Integer id, String nickname, String password, String role, boolean firstLogin) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.firstLogin = firstLogin;
    }

    /**
     * Costruttore per la creazione di un nuovo utente, la generazione dell'ID è gestita dal database.
     */
    public User(String nickname, String password, String role) {
        this(null, nickname, password, role, true);
    }
}
