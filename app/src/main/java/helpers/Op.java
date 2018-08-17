package helpers;

import java.util.ArrayList;

public class Op {
    public enum whereOp {
        ANY, EQ, LT, LE, GT, GE, RANGE, SET, ALLSET, EMPTY
    }
    public String Method;
    public ArrayList<Object> Params = new ArrayList<>();

    public Op(String method) {
        Method = method;
    }
}
