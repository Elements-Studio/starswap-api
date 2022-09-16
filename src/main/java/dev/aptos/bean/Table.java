package dev.aptos.bean;

public class Table {
    ///// Type of tables
    //    struct Table<phantom K: copy + drop, phantom V> has store {
    //        handle: address,
    //    }
    private String handle;

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "Table{" +
                "handle='" + handle + '\'' +
                '}';
    }
}
