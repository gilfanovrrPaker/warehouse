package ru.npf_paker.wms;

public class DataStore {
    private static String json;
    private static final DataStore ourInstance = new DataStore();

    public static DataStore getInstance() {
        return ourInstance;
    }

    private DataStore() {
    }

    public String GetData() {
        return json;
    }

    public void SetData(String value) {
        json = value;
    }
}
