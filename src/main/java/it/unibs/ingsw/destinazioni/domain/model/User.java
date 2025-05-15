package it.unibs.ingsw.destinazioni.domain.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class User {
    private int id;
    private String nickname;
    private String password;
    private String role;
    private boolean firstLogin;

    public User(int id, String nickname, String password, String role, boolean firstLogin) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.firstLogin = firstLogin;
    }
}
