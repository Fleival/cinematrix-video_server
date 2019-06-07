package com.denspark.core.video_parser.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class XLink {

    private int id;
    private String type;
    private String url;
    private Boolean isExistsInDb;

    public XLink(){}

    public XLink(int id, String type, String url, Boolean isExistsInDb) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.isExistsInDb = isExistsInDb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isExistsInDb() {
        return isExistsInDb;
    }

    public void setExistsInDb(Boolean isExistsInDb) {
        this.isExistsInDb = isExistsInDb;
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
        if (!(o instanceof XLink)) return false;
        XLink xLink = (XLink) o;
        return getUrl().equals(xLink.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }

    public StringBuilder getXmlStringBuilder(){

        String xlinkTag = "<xlink>";
        String xlinkTagClose = "</xlink>";
        String idTag = "<id>";
        String idTagClose = "</id>";
        String typeTag = "<type>";
        String typeTagClose = "</type>";
        String urlTag = "<url>";
        String urlTagClose = "</url>";
        String inDatabaseTag = "<in_database>";
        String inDatabaseTagClose = "</in_database>";
        StringBuilder sb = new StringBuilder("\n");

        sb
                .append("\t").append(xlinkTag).append("\n") // <xlink>
                .append("\t\t").append(idTag).append(getId()).append(idTagClose).append("\n")//  <id>1</id>
                .append("\t\t").append(typeTag).append(getType()).append(typeTagClose).append("\n")//  <type></type>
                .append("\t\t").append(urlTag).append(getUrl()).append(urlTagClose).append("\n")//  <url></url>
                .append("\t\t").append(inDatabaseTag).append(isExistsInDb()).append(inDatabaseTagClose).append("\n")//  <in_database></in_database>
                .append("\t").append(xlinkTagClose);//  </xlink>


        return sb;
    }

    public String getXml(){
        return getXmlStringBuilder().toString();
    }
}
