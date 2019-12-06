package com.denspark.views;

import io.dropwizard.views.View;

public class IndexView extends View {
    private long totalFilmsCount;
    private long yesterdayFilmsCount;
    private long lastUpdFilmsCount;
    private boolean isUpdating;
    private String statusGreen;
    private String statusYellow;
    private String elapsedAfterPreviousUpd;

    public IndexView(long totalFilmsCount, long yesterdayFilmsCount, long lastUpdFilmsCount, boolean isUpdating, String elapsedAfterPreviousUpd) {
        super("index.mustache");
        this.totalFilmsCount = totalFilmsCount;
        this.yesterdayFilmsCount = yesterdayFilmsCount;
        this.lastUpdFilmsCount = lastUpdFilmsCount;
        this.isUpdating = isUpdating;
        this.elapsedAfterPreviousUpd = elapsedAfterPreviousUpd;

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
}
