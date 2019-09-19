package com.denspark.core.video_parser.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class XLink {

    private int id;
    private String type;
    private String url;
    private Boolean isExistsInDb;
    private Boolean isNeedToUpdateRecord;
    private String updateDate;
    private int posRating;
    private int negRating;

    public XLink() {
    }

    public XLink(int id, String type, String url, String updateDate, Boolean isExistsInDb, Boolean isNeedToUpdateRecord) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.isExistsInDb = isExistsInDb;
        this.isNeedToUpdateRecord = isNeedToUpdateRecord;
        this.updateDate = updateDate;
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

    public Boolean isNeedToUpdateRecord() {
        return isNeedToUpdateRecord;
    }

    public void setNeedToUpdateRecord(Boolean needToUpdateRecord) {
        isNeedToUpdateRecord = needToUpdateRecord;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getPosRating() {
        return posRating;
    }

    public void setPosRating(int posRating) {
        this.posRating = posRating;
    }

    public int getNegRating() {
        return negRating;
    }

    public void setNegRating(int negRating) {
        this.negRating = negRating;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public URL getURL() {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public Date getUpdateDateObj() {
        Date date;
        if (updateDate != null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(updateDate);
            } catch (ParseException e) {
                date = null;
            }
        }else{
            date = null;
        }
        return date;
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

    public StringBuilder getXmlStringBuilder() {

        String xlinkTag = "<xlink>";
        String xlinkTagClose = "</xlink>";
        String idTag = "<id>";
        String idTagClose = "</id>";
        String typeTag = "<type>";
        String typeTagClose = "</type>";
        String urlTag = "<url>";
        String urlTagClose = "</url>";
        String updateDateTag = "<update_date>";
        String updateDateTagClose = "</update_date>";
        String inDatabaseTag = "<in_database>";
        String inDatabaseTagClose = "</in_database>";
        String needToUpdateRecordTag = "<need_to_update_record>";
        String needToUpdateRecordTagClose = "</need_to_update_record>";
        String posRatingTag = "<pos_rating>";
        String posRatingTagClose = "</pos_rating>";
        String negRatingTag = "<neg_rating>";
        String negRatingTagClose = "</neg_rating>";

        StringBuilder sb = new StringBuilder("\n");

        sb
                .append("\t").append(xlinkTag).append("\n") // <xlink>
                .append("\t\t").append(idTag).append(getId()).append(idTagClose).append("\n")//  <id>1</id>
                .append("\t\t").append(typeTag).append(getType()).append(typeTagClose).append("\n")//  <type></type>
                .append("\t\t").append(urlTag).append(getUrl()).append(urlTagClose).append("\n")//  <url></url>
                .append("\t\t").append(updateDateTag).append(getUpdateDate()).append(updateDateTagClose).append("\n")//  <update_date></update_date>
                .append("\t\t").append(inDatabaseTag).append(isExistsInDb()).append(inDatabaseTagClose).append("\n")//  <in_database></in_database>
                .append("\t\t").append(needToUpdateRecordTag).append(isNeedToUpdateRecord()).append(needToUpdateRecordTagClose).append("\n")//  <need_to_update_record></need_to_update_record>
                .append("\t\t").append(posRatingTag).append(getPosRating()).append(posRatingTagClose).append("\n")//  <pos_rating></pos_rating>
                .append("\t\t").append(negRatingTag).append(getNegRating()).append(negRatingTagClose).append("\n")//  <neg_rating></neg_rating>
                .append("\t").append(xlinkTagClose);//  </xlink>


        return sb;
    }

    public String getXml() {
        return getXmlStringBuilder().toString();
    }


}
