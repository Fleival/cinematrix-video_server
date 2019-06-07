package com.denspark.core.video_parser.link_parser.impls;

import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.link_parser.LinkGrabber;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FilmixLinkGrabber extends LinkGrabber {

    public FilmixLinkGrabber(Link link, SiteCss siteCss, XLinkType type) {
        super(link, siteCss, type);
    }

    @Override
    protected void findPersonLinks(Document document) {
        String cssQuery = siteCss.getArticleItemURLSelector();
        Elements elements = document.select(cssQuery);

        elements.forEach(
                this::createXLink
        );
    }

    @Override
    protected void findFilmLinks(Document document) {
        String cssQuery = siteCss.getPersonItemURLSelector();
        Elements elements = document.select(cssQuery);

        elements.forEach(
                this::createXLink
        );
    }
    private void createXLink(Element element) {
        xLinkSet_ItemLinks.add(createXlink(element));
    }

}
