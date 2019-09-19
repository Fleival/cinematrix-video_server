package com.denspark.core.video_parser;

import com.denspark.config.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.article_parser.ArticleParser;
import com.denspark.core.video_parser.article_parser.impls.FilmixArticleParser;
import com.denspark.core.video_parser.link_parser.LinkParser;
import com.denspark.core.video_parser.link_parser.impls.FilmixLinkParser;
import com.denspark.core.video_parser.model.XLinkType;
import org.springframework.context.ApplicationContext;

public class ParserFactory {

    private static ParserFactory mInstance;
    private Parser parser;
    private LinkParser linkParser;
    private ArticleParser articleParser;

    private ParserFactory() {
    }

    public static ParserFactory getInstance() {
        if (mInstance == null) {
            mInstance = new ParserFactory();
        }
        return mInstance;
    }

    public Parser getFilmixParser(String siteName, XLinkType type, int THREAD_COUNT,ApplicationContext context, CinematrixVideoConfiguration configuration) {
        this.parser = FilmixLinkParser.getInstance(
                siteName,
                type,
                THREAD_COUNT,
                context,
                configuration);
        parser.setStartPage(parser.getSiteCss().getPersonsSectionUrl());
        return parser;
    }

    public LinkParser getFilmixLinkParser(String siteName, XLinkType type, int THREAD_COUNT,ApplicationContext context,  CinematrixVideoConfiguration configuration) {
        this.linkParser = FilmixLinkParser.getInstance(
                siteName,
                type,
                THREAD_COUNT,
                context,
                configuration);

        return linkParser;
    }

    public ArticleParser getFilmixArticleParser(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, int splitListSize, CinematrixVideoConfiguration configuration) {
        this.articleParser = FilmixArticleParser.getInstance(
                siteName,
                type,
                THREAD_COUNT,
                context,
                splitListSize,
                configuration);
//        parser.setStartPage(parser.getSiteCss().getPersonsSectionUrl());
        return articleParser;
    }

}
