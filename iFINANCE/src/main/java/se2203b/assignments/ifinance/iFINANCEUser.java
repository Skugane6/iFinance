package se2203b.assignments.ifinance;

import javafx.beans.property.*;

public abstract class iFINANCEUser {

    private IntegerProperty id;
    private StringProperty fn;

    private ObjectProperty<UserAccount> uAccount
            = new SimpleObjectProperty(new UserAccount());

    //set and get methods
    // id property
    public void setID(int id) {
        this.id = new SimpleIntegerProperty(id);
    }
    public IntegerProperty idProperty() {
        return this.id;
    }
    public int getID() {
        return this.id.get();
    }

    // name property
    public void setFn(String name) {
        this.fn = new SimpleStringProperty(name);
    }
    public StringProperty fnProperty() {
        return this.fn;
    }
    public String getFn() {
        return this.fn.get();
    }

    // userAccount Property
    public void setuAccount(UserAccount uAccount) {

        this.uAccount.set(uAccount);
    }
    public ObjectProperty<UserAccount> uAccountProperty() {
        return this.uAccount;
    }
    public UserAccount getuAccount() {
        return this.uAccount.get();
    }

}
