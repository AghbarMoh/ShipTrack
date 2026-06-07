package com.shiptrack;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.shiptrack.services.AuthService;

public class PasswordFuzzer {

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {

        AuthService authService = new AuthService();

        String password = data.consumeString(100);

        try {
            authService.isPasswordValid(password);
        } catch (Exception e) {
            throw new RuntimeException(
                "Application crashed with password = " + password,
                e
            );
        }
    }
}