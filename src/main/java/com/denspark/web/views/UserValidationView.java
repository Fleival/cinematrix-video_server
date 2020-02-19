package com.denspark.web.views;

import com.denspark.model.user.User;
import io.dropwizard.views.View;

public class UserValidationView extends View {
    private final User user;

    public enum Template {
        MUSTACHE("user_validation.mustache");

        private String templateName;

        Template(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    public UserValidationView(UserValidationView.Template template, User user) {
        super(template.getTemplateName());
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}