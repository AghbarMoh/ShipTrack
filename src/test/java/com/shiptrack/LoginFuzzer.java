package com.shiptrack;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.shiptrack.services.AuthService;
import com.shiptrack.database.DatabaseManager;

public class LoginFuzzer {

    public static void fuzzerInitialize() {
        DatabaseManager.initializeDatabase();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {

        AuthService authService = new AuthService();

        String username = data.consumeString(100);
        String password = data.consumeString(100);

        try {
            authService.login(username, password);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Application crashed with username = " + username +
                    " and password = " + password,
                    e);
        }
    }
}