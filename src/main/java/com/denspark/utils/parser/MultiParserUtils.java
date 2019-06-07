package com.denspark.utils.parser;

import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.utils.file_path_utils.FilePathUtils;
import com.denspark.utils.xml.VTDXMLreader;
import com.denspark.utils.xml.VTD_XMLcreator;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiParserUtils {
    private static MultiParserUtils mInstance;
    private static boolean FILE_PATH_UTILS_DEBUG_MODE;
    private FilePathUtils filePathUtils= FilePathUtils.getInstance();

    private MultiParserUtils() {
    }

    public static MultiParserUtils getInstance(boolean filePathDebugMode) {
        if (mInstance == null) {
            mInstance = new MultiParserUtils();
            FILE_PATH_UTILS_DEBUG_MODE = filePathDebugMode;
        }
        return mInstance;
    }

    public void writeLinks(String linksXmlFilename, Set<Link> linkSet) {
        VTD_XMLcreator xmlWriter = new VTD_XMLcreator(filePathUtils.getFullPath(linksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
        xmlWriter.createLinkXmlDoc(linkSet, linksXmlFilename);
    }

    public boolean fileExist(String filename) {
        File f = new File(filePathUtils.getFullPath(filename,FILE_PATH_UTILS_DEBUG_MODE));
        return f.exists() && !f.isDirectory();
    }

    public Set<Link> readLinks(String linksXmlFilename) {
        VTDXMLreader xmlReader = new VTDXMLreader();
        return xmlReader.getLinkSet(filePathUtils.getFullPath(linksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
    }

    public Set<XLink> readXlinks(String itemLinksXmlFilename) {
        VTDXMLreader xmlReader = new VTDXMLreader();
        return xmlReader.getxLinkSet(filePathUtils.getFullPath(itemLinksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
    }
    public ConcurrentHashMap<String, XLink> readXlinksToUrlMap(String itemLinksXmlFilename) {
        VTDXMLreader xmlReader = new VTDXMLreader();
        return xmlReader.getXlinksToUrlMap(filePathUtils.getFullPath(itemLinksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
    }

    public void write(String itemLinksXmlFilename, Set<XLink> xLinkSet) {
        VTD_XMLcreator xmlWriter = new VTD_XMLcreator(filePathUtils.getFullPath(itemLinksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
        xmlWriter.createXlinkXmlDoc(xLinkSet, itemLinksXmlFilename);
    }

    public void recountXlinksId(String itemLinksXmlFilename) {
        VTDXMLreader xmlReader = new VTDXMLreader();
        Set<XLink> xLinks = xmlReader.getXlinkSetResult(filePathUtils.getFullPath(itemLinksXmlFilename,FILE_PATH_UTILS_DEBUG_MODE));
        int i = 1;
        for (XLink xLink : xLinks) {
            xLink.setId(i);
            i++;
        }
        VTD_XMLcreator xmLcreator = new VTD_XMLcreator(xmlReader.getInputXml());
        xmLcreator.createXlinkXmlDoc(xLinks, itemLinksXmlFilename);
    }

    public String getXlinkXMLfilename(XLinkType type, String siteName){
        return type + "_" + siteName + "_" + "ItemLinks.xml";
    }
}
