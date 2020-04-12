package core.models.account;

import java.util.List;

public class Account {
    private double credit;
    private String id, name, email, phone, password, passwordre, currentAddress;
    private List<String> previousAddresses;

    public Account(double credit, String name, String email, String phone, String password, String passwordre, String currentAddress, List<String> previousAddresses) {
        this.credit = credit;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordre = passwordre;
        this.currentAddress = currentAddress;
        this.previousAddresses = previousAddresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordre() {
        return passwordre;
    }

    public void setPasswordre(String passwordre) {
        this.passwordre = passwordre;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public List<String> getPreviousAddresses() {
        return previousAddresses;
    }

    public void setPreviousAddresses(List<String> previousAddresses) {
        this.previousAddresses = previousAddresses;
    }
}
