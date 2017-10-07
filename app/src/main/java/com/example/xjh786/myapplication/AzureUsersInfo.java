package com.example.xjh786.myapplication;


import java.util.HashMap;
import java.util.Map;

class MotoUserInfo {

    private String name;

    MotoUserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

class AzureUsersInfo {

    static final String SUBSCRIPTION_KEY = "e230ce5fd5484f8080a49cf7a1e12abd";
    private Map<String, ZelloUserInfo> ZELLO_USERS_LIST = new HashMap<>();
    private Map<String, MotoUserInfo> MOTO_USERS_LIST = new HashMap<>();

    AzureUsersInfo() {
        ZELLO_USERS_LIST.put("042fbc8d-7081-4ee9-aae2-90a691bf1cb6", new ZelloUserInfo("", "")); // TihHuang
        MOTO_USERS_LIST.put("042fbc8d-7081-4ee9-aae2-90a691bf1cb6", new MotoUserInfo("Tih Huang"));

        ZELLO_USERS_LIST.put("3209c42f-545c-474e-8d3f-1022d52ac765", new ZelloUserInfo("", "")); // CheeYien
        MOTO_USERS_LIST.put("3209c42f-545c-474e-8d3f-1022d52ac765", new MotoUserInfo("CheeYien"));

        ZELLO_USERS_LIST.put("016ab45a-dc53-4ccb-a97d-7e848266ad4a", new ZelloUserInfo("", "")); // Marilyn
        MOTO_USERS_LIST.put("016ab45a-dc53-4ccb-a97d-7e848266ad4a", new MotoUserInfo("Marilyn"));

        ZELLO_USERS_LIST.put("78540070-d192-474b-b089-a2190bd57347", new ZelloUserInfo("", "")); // Amir
        MOTO_USERS_LIST.put("78540070-d192-474b-b089-a2190bd57347", new MotoUserInfo("Amir"));

        ZELLO_USERS_LIST.put("76fd3c3a-fab3-4692-8473-e9d19c48875d", new ZelloUserInfo("", "")); // AdrianKang
        MOTO_USERS_LIST.put("76fd3c3a-fab3-4692-8473-e9d19c48875d", new MotoUserInfo("Adrian Kang"));

        ZELLO_USERS_LIST.put("4fef5be3-a534-4788-b521-7d899d5cf96a", new ZelloUserInfo("", "")); // SoonLengYap
        MOTO_USERS_LIST.put("4fef5be3-a534-4788-b521-7d899d5cf96a", new MotoUserInfo("Soon Leng"));

        ZELLO_USERS_LIST.put("ab7bb31c-1c00-4caa-a975-863999e6bd5b", new ZelloUserInfo("", "")); // Eugene Chin
        MOTO_USERS_LIST.put("ab7bb31c-1c00-4caa-a975-863999e6bd5b", new MotoUserInfo("Eugene Chin"));
    }

    public ZelloUserInfo getZelloInfo(String azureProfileId) {
        return ZELLO_USERS_LIST.get(azureProfileId);
    }

    public MotoUserInfo getMotoInfo(String azureProfileId) {
        return MOTO_USERS_LIST.get(azureProfileId);
    }
}

class ZelloUserInfo {

    private String username;
    private String password;

    ZelloUserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
