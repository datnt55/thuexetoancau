package grab.com.thuexetoancau.model;

import java.io.Serializable;

/**
 * Created by DatNT on 7/21/2017.
 */

public class User implements Serializable{
    private int id;
    private String name;
    private String phone;
    private String email;
    private String url;
    private String license;
    public User() {
    }

    public User(int id, String name, String phone, String email, String url) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.url = url;
    }

    public User(String name, String phone, String license) {
        this.name = name;
        this.phone = phone;
        this.license = license;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
