package core.models.payload.account;

import core.models.account.Address;

public class Addresses {
    private Address currentAddress, previousAddress;

    public Addresses(Address currentAddress, Address previousAddress) {
        this.currentAddress = currentAddress;
        this.previousAddress = previousAddress;
    }

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public Address getPreviousAddress() {
        return previousAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setPreviousAddress(Address previousAddress) {
        this.previousAddress = previousAddress;
    }
}
