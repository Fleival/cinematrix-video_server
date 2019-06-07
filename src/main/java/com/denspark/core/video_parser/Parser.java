package com.denspark.core.video_parser;

import com.denspark.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.utils.parser.MultiParserUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Parser {
//    private final CinematrixVideoConfiguration configuration;
    //Parser configuration
    protected long PAUSE_TIME = 100;
//    private final String CONFIG_PATH = "parser_config/sites_config_css_nav.yml";
    protected ExecutorService executorService;
    protected final int THREAD_COUNT;
    protected String xLinkXmlFilename;
    protected String linksXmlFilename;
    protected final SiteCss siteCss;
    private final YamlCssConfigReader configReader;
    protected final MultiParserUtils multiParserUtils;
    protected XLinkType type;
    protected String startPage;

    protected String urlBase;
    protected CompletableFuture<Void> allFutures;
    protected AtomicBoolean doParse = new AtomicBoolean(true);

    protected Parser(String siteName, XLinkType type, int THREAD_COUNT, CinematrixVideoConfiguration configuration) {
//        this.configuration = configuration;
        configReader = YamlCssConfigReader
                .getInstance(configuration.getConfig_PATH());
        siteCss = configReader.getSiteByName(siteName);

        multiParserUtils = MultiParserUtils.getInstance(false);

        this.THREAD_COUNT = THREAD_COUNT;

        this.type = type;
        this.xLinkXmlFilename =configuration.getXlink_WORKING_FOLDER()+ type + "_" + siteName + "_" + "ItemLinks.xml";
        this.linksXmlFilename =configuration.getXlink_WORKING_FOLDER()+ type + "_" + siteName + "_" + "LinksToParse.xml";

    }

    public void startParser() {
        parserStarter();
    }

    public void stopParser() {
        try {
            doParse.set(false);
            if (allFutures != null) {
                if (allFutures.isDone()) {
                    executorService.shutdown();
                    executorService.awaitTermination(10, TimeUnit.SECONDS);
                }
            }
            stopInstance();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public abstract void setStartPage(String startPage);

    protected abstract void parserStarter();

    protected abstract void stopInstance();

    public SiteCss getSiteCss() {
        return siteCss;
    }

    public void updateConfig() {
        MaintenanceParser maintenanceParser = new MaintenanceParser();
        maintenanceParser.updateConfig();
    }

    public class MaintenanceParser {

        private void setLastPageIndexes() {
            int articleListLastPageIndex = 0;
            int filmListLastPageIndex = 0;
            int serialListLastPageIndex = 0;
            int cartoonListLastPageIndex = 0;
            int personListLastPageIndex = 0;
            try {
                Document mainPage = Jsoup.connect(siteCss.getUrl()).get();
                Document filmPage = Jsoup.connect(siteCss.getFilmListUrl()).get();
                Document serialPage = Jsoup.connect(siteCss.getSerialListUrl()).get();
                Document cartoonPage = Jsoup.connect(siteCss.getCartoonListUrl()).get();
                Document personsPage = Jsoup.connect(siteCss.getPersonsSectionUrl()).get();

                filmListLastPageIndex = Integer.parseInt(filmPage.select(siteCss.getFilmListLastPageSelector()).first().text());
                serialListLastPageIndex = Integer.parseInt(serialPage.select(siteCss.getSerialListLastPageSelector()).first().text());
                cartoonListLastPageIndex = Integer.parseInt(cartoonPage.select(siteCss.getCartoonListLastPageSelector()).first().text());
                personListLastPageIndex = Integer.parseInt(personsPage.selectFirst("#dle-content > div > div > div > div > a:nth-child(6)").text());
                articleListLastPageIndex = Integer.parseInt(mainPage.select(siteCss.getArticleLastPageSelector()).first().text());

                siteCss.setArticleListLastPageIndex(articleListLastPageIndex);
                siteCss.setFilmListLastPageIndex(filmListLastPageIndex);
                siteCss.setSerialListLastPageIndex(serialListLastPageIndex);
                siteCss.setCartoonListLastPageIndex(cartoonListLastPageIndex);
                siteCss.setPersonListLastPageIndex(personListLastPageIndex);

            } catch (IOException err_1) {
                System.err.println("unable connect the host: "
                        + siteCss.getFilmListUrl()
                        + " or "
                        + siteCss.getSerialListUrl()
                        + " or "
                        + siteCss.getCartoonListUrl()
                        + "Last page indexes = 0"
                        + "\nStacktrace : ");
                err_1.printStackTrace();
            } catch (Selector.SelectorParseException err_2) {
                System.err.println("incorrect css selector: "
                        + siteCss.getFilmListLastPageSelector()
                        + " or "
                        + siteCss.getSerialListLastPageSelector()
                        + " or "
                        + siteCss.getCartoonListLastPageSelector()
                        + "Last page indexes = 0"
                        + "\nStacktrace : ");
                err_2.printStackTrace();
            } catch (NullPointerException err_3) {
                System.err.println("Integer.parseInt() argument is null, check css selectors"
                        + " filmListLastPageIndex = "
                        + filmListLastPageIndex
                        + " serialListLastPageIndex = "
                        + serialListLastPageIndex
                        + " cartoonListLastPageIndex = "
                        + cartoonListLastPageIndex
                        + "\nStacktrace : ");
                err_3.printStackTrace();
            } catch (NumberFormatException err_4) {
                System.err.println("Integer.parseInt() argument is null, check css selectors"
                        + " filmListLastPageIndex = "
                        + filmListLastPageIndex
                        + " serialListLastPageIndex = "
                        + serialListLastPageIndex
                        + " cartoonListLastPageIndex = "
                        + cartoonListLastPageIndex
                        + "\nStacktrace : ");
                err_4.printStackTrace();
            } finally {
                siteCss.setArticleListLastPageIndex(articleListLastPageIndex);
                siteCss.setFilmListLastPageIndex(filmListLastPageIndex);
                siteCss.setSerialListLastPageIndex(serialListLastPageIndex);
                siteCss.setCartoonListLastPageIndex(cartoonListLastPageIndex);
                siteCss.setPersonListLastPageIndex(personListLastPageIndex);
            }
        }

        public void updateConfig() {
            setLastPageIndexes();

            configReader.updateConfigFile();
            System.out.println(siteCss);
        }
    }
}
