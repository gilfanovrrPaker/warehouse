package data;

import java.util.ArrayList;

public class Act {
    public long id = System.currentTimeMillis();
    public boolean income;
    public ArrayList<ActItem> items = new ArrayList<>();

    public Act(boolean income) {
        this.income = income;
    }
}
