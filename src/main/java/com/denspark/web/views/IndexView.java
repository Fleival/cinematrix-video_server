package com.denspark.web.views;

import io.dropwizard.views.View;
import org.springframework.ui.Model;

public class IndexView extends View {
    private Model model;
    private long totalFilmsCount;
    private long yesterdayFilmsCount;
    private long lastUpdFilmsCount;
    private boolean isUpdating;
    private String statusGreen;
    private String statusYellow;
    private String elapsedAfterPreviousUpd;
    private String userName;
    private boolean isUserAuthenticated;

    public IndexView(long totalFilmsCount, long yesterdayFilmsCount, long lastUpdFilmsCount, boolean isUpdating, String elapsedAfterPreviousUpd, Model model) {
        super("index.mustache");
        this.totalFilmsCount = totalFilmsCount;
        this.yesterdayFilmsCount = yesterdayFilmsCount;
        this.lastUpdFilmsCount = lastUpdFilmsCount;
        this.isUpdating = isUpdating;
        this.elapsedAfterPreviousUpd = elapsedAfterPreviousUpd;
        this.model = model;

    }

    public long getTotalFilmsCount() {
        return totalFilmsCount;
    }

    public long getYesterdayFilmsCount() {
        return yesterdayFilmsCount;
    }

    public long getLastUpdFilmsCount() {
        return lastUpdFilmsCount;
    }

    public String getStatusGreen() {
        if (isUpdating) {
            statusGreen = "";
        } else {
            statusGreen = "-on";
        }
        return statusGreen;
    }

    public String getStatusYellow() {
        if (isUpdating) {
            statusYellow = "-on";
        } else {
            statusYellow = "";
        }
        return statusYellow;
    }

    public String getElapsedAfterPreviousUpd() {
        return elapsedAfterPreviousUpd;
    }

    public boolean isUserAuthenticated() {
        isUserAuthenticated = (boolean) model.getAttribute("isUserAuthenticated");
        return isUserAuthenticated;
    }

    public String getUserName() {
        userName = (String) model.getAttribute("userName");
        return userName;
    }
}
