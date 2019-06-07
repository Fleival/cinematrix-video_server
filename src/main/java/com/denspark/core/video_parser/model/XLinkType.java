package com.denspark.core.video_parser.model;

public enum XLinkType {
    PERSON_LINKS,
    FILM_LINKS;

    public static XLinkType fromString(final String s) {
        return XLinkType.valueOf(s);
    }
}
