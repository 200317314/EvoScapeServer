package core.models.account;

import core.database.MongoConnection;
import core.utils.Utils;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Account {
    private double credit;
    private String id, name, email, phone, password, passwordre;
    private Address currentAddress;
    private List<Address> previousAddresses;

    public Account(double credit, String name, String email, String phone, String password, String passwordre, Address currentAddress, List<Address> previousAddresses) {
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

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public List<Address> getPreviousAddresses() {
        return previousAddresses;
    }

    public void setPreviousAddresses(List<Address> previousAddresses) {
        this.previousAddresses = previousAddresses;
    }

    public void saveAccount() {
        Document account = MongoConnection.getDatabase().getCollection("accounts").find(eq("_id", this.getId())).first();
        Document updatedAccount = new Document("data", Utils.getGson().toJson(this));
        Document operation = new Document("$set", updatedAccount);

        MongoConnection.getDatabase().getCollection("accounts").updateOne(account, operation);
    }
}
