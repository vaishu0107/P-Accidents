package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginCreds {
    private String username;
    private String password;

    public LoginCreds(){
        super();
        username = "";
        password = "";
    }
    public LoginCreds(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
