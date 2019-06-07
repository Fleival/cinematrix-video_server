package com.denspark.core.video_parser.model;

public class Site {
    private String name;
    private String url;
    private String saveDir;
    private String fileExtension;
    private String separator;
    private String pageQuerySeparator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }


    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPageQuerySeparator() {
        return pageQuerySeparator;
    }

    public void setPageQuerySeparator(String pageQuerySeparator) {
        this.pageQuerySeparator = pageQuerySeparator;
    }

}
