package com.bezkoder.policylambda.model;

import java.util.List;

public record UserRecord(
        long id,
        String username,
        String email,
        String passwordHash,
        List<String> roles) {
}
