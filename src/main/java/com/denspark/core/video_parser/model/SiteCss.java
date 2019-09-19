package com.denspark.core.video_parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames=true)
public class SiteCss extends Site {

    @Getter @Setter private String filmListLastPageSelector;
    @Getter @Setter private String filmCategorySelector;
    @Getter @Setter private String filmListUrl;
    @Getter @Setter private int filmListLastPageIndex;
    @Getter @Setter private int articleListLastPageIndex;
    @Getter @Setter private String articleLastPageSelector;
    @Getter @Setter private String articleItemURLSelector;
    @Getter @Setter private String articleItemSelector;
    @Getter @Setter private String serialListLastPageSelector;
    @Getter @Setter private String serialCategorySelector;
    @Getter @Setter private String serialListUrl;
    @Getter @Setter private int serialListLastPageIndex;
    @Getter @Setter private String cartoonListLastPageSelector;
    @Getter @Setter private String cartoonCategorySelector;
    @Getter @Setter private String cartoonListUrl;
    @Getter @Setter private int cartoonListLastPageIndex;
    @Getter @Setter private String categoryListElementSelector;
    @Getter @Setter private String categoryListElementUrlAttr;
    @Getter @Setter private String videoTitleKey;
    @Getter @Setter private String videoTitleRegex;
    @Getter @Setter private String videoAbsUrlKey;
    @Getter @Setter private String thumbnailUrlKey;
    @Getter @Setter private String thumbnailUrlRegex;
    @Getter @Setter private String thumbnailAbsUrlKey;
    @Getter @Setter private String durationElementKey;
    @Getter @Setter private String durationElementRegex;
    @Getter @Setter private String ratingElementKey;
    @Getter @Setter private String ratingElementRegex;
    @Getter @Setter private String videoSrcElementsKey;
    @Getter @Setter private String videoSrcElementsRegex;
    @Getter @Setter private String videoSrcElementAttrKey;
    @Getter @Setter private String personsSectionUrl;
    @Getter @Setter private int personListLastPageIndex;
    @Getter @Setter private String personListLastPageIndexSelector;
    @Getter @Setter private String personItemURLSelector;

}