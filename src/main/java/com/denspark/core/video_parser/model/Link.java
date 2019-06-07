package com.denspark.core.video_parser.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Link {
    private String url;
    private boolean processed;
    private boolean error;

    public Link(){}

    public Link(String url, Boolean processed,Boolean error) {
        this.error = error;
        this.url = url;
        this.processed = processed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public URL getURL(){
        URL u = null;
        try {
            u = new URL(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link = (Link) o;
        return getUrl().equals(link.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }

    public StringBuilder getXmlStringBuilder(){

        String linkTag = "<link>";
        String linkTagClose = "</link>";
        String urlTag = "<url>";
        String urlTagClose = "</url>";
        String processedTag = "<processed>";
        String processedTagClose = "</processed>";
        String errorTag = "<error>";
        String errorTagClose = "</error>";


        StringBuilder sb = new StringBuilder("\n");

        sb
                .append("\t").append(linkTag).append("\n") // <xlink>
                .append("\t\t").append(urlTag).append(getUrl()).append(urlTagClose).append("\n")//  <url></url>
                .append("\t\t").append(processedTag).append(isProcessed()).append(processedTagClose).append("\n")//  <in_database></in_database>
                .append("\t\t").append(errorTag).append(isError()).append(errorTagClose).append("\n")//  <in_database></in_database>
                .append("\t").append(linkTagClose);//  </xlink>


        return sb;
    }

    public String getXml(){
        return getXmlStringBuilder().toString();
    }

}
