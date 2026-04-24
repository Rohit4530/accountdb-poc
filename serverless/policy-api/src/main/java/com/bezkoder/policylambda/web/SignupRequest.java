package com.bezkoder.policylambda.web;

import java.util.Set;

public record SignupRequest(String username, String email, String password, Set<String> role) {
}
