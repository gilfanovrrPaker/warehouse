package ru.npf_paker.wms;

import android.arch.core.util.Function;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

public class DataStore {
    private static String json;
    private static final DataStore ourInstance = new DataStore();
    public static final HashMap<Long, Single> promisesMap = new HashMap<>();
    public static final HashMap<Long, Function> functionHashMap = new HashMap<>();

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
