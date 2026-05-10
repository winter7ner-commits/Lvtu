package com.bitzh.lvtu.dto;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
=======
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
<<<<<<< HEAD
}
=======
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
