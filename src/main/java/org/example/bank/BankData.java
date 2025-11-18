package org.example.bank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BankData implements Serializable {
    public Map<String, String> users = new HashMap<>();
    public Map<String, Double> balances = new HashMap<>();
}
