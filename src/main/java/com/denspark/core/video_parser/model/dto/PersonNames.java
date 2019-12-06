package com.denspark.core.video_parser.model.dto;

public class PersonNames implements java.io.Serializable {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonNames(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public PersonNames() {
        super();
    }
}
