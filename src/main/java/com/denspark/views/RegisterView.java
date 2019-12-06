package com.denspark.views;

import io.dropwizard.views.View;

public class RegisterView extends View {
    private String callbackUrl;

    public RegisterView() {
        super("registration.mustache");
        this.callbackUrl = "/";
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }
}