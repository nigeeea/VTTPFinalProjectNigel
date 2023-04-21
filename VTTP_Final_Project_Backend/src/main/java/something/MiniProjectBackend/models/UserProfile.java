package something.MiniProjectBackend.models;

public class UserProfile {

    String email;
    String full_name;
    Integer contact_number;
    String address;
    Integer postal_code;


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Integer getContact_number() {
        return this.contact_number;
    }

    public void setContact_number(Integer contact_number) {
        this.contact_number = contact_number;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPostal_code() {
        return this.postal_code;
    }

    public void setPostal_code(Integer postal_code) {
        this.postal_code = postal_code;
    }

    
}
