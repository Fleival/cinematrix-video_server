package com.denspark.utils.xml;

import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.ximpleware.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.StringEscapeUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VTD_XMLcreator {
    byte[] emptyDoc;
    VTDGen vg = new VTDGen();
    VTDNav vn;
    XMLModifier xm;
    FileOutputStream fos;

    public VTD_XMLcreator(String file) {
        try {
            emptyDoc = "<?xml version='1.0' encoding='UTF-8'?>\n<root>\n</root>".getBytes("UTF-8");
            vg.setDoc(emptyDoc);
            vg.parse(false);

            vn = vg.getNav();
            xm = new XMLModifier(vn);
            fos = new FileOutputStream(file);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ModifyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void createXml(StringBuilder sb, String rootElement) {
        try {
            xm.updateElementName(rootElement);
            xm.insertAfterHead(sb.toString());
            vn = xm.outputAndReparse();
            fos.write(vn.getXML().getBytes());
            fos.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ModifyException e) {
            e.printStackTrace();
        } catch (NavException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TranscodeException e) {
            e.printStackTrace();
        }
    }

    public void createLinkXmlDoc(Set<Link> linkSet, String info) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StringBuilder sb = new StringBuilder();
        linkSet.forEach(
                link -> sb.append(link.getXmlStringBuilder())
        );
//        System.out.println(sb.toString());
        createXml(sb, "urls");
        stopWatch.stop();
        System.out.println("Wrote " + linkSet.size() + " items to file: " + info);
        System.out.println("in " + stopWatch.getTime() / 100 + " santiseconds");
    }


    public void createXlinkXmlDoc(Set<XLink> xLinkSet, String info) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StringBuilder sb = new StringBuilder();
        xLinkSet.forEach(
                xLink -> sb.append(xLink.getXmlStringBuilder())
        );
//        System.out.println(sb.toString());
        createXml(sb, "urls");
        stopWatch.stop();
        System.out.println("Wrote " + xLinkSet.size() + " items to file: " + info);
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    public void createXlinkXmlDoc(List<XLink> xLinkSet, String info) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StringBuilder sb = new StringBuilder();
        xLinkSet.forEach(
                xLink -> sb.append(xLink.getXmlStringBuilder())
        );
//        System.out.println(sb.toString());
        createXml(sb, "urls");
        stopWatch.stop();
        System.out.println("Wrote " + xLinkSet.size() + info);
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    public void createXpesonXmlDoc(Set<Person> xPersonSet, String info) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StringBuilder sb = new StringBuilder();
        xPersonSet.forEach(
                person -> sb.append(person.getXmlStringBuilder())
        );
        createXml(sb, "person_cache");
        stopWatch.stop();
        System.out.println("Wrote " + xPersonSet.size() + info);
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    public void createXpesonXmlDoc(Map<String, Integer> xCache, String info) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StringBuilder sb = new StringBuilder();
        xCache.forEach(
                (k, v) -> sb.append(
                        getXmlStringBuilder(k, v))
        );
        createXml(sb, "person_cache");
        stopWatch.stop();
        System.out.println("Wrote " + xCache.size() + info);
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    private StringBuilder getXmlStringBuilder(String name, Integer id) {
        name = sanitizeXmlChars(name);
        String xTag = "<xperson>";
        String xTagClose = "</xperson>";
        String idTag = "<id>";
        String idTagClose = "</id>";
        String nameTag = "<name>";
        String nameTagClose = "</name>";
        StringBuilder sb = new StringBuilder("\n");
        sb
                .append("\t").append(xTag).append("\n")
                .append("\t\t").append(idTag).append(id).append(idTagClose).append("\n")
                .append("\t\t").append(nameTag).append(name).append(nameTagClose).append("\n")
                .append("\t").append(xTagClose);
        return sb;
    }

    public static String sanitizeXmlChars(String xml) {
        return StringEscapeUtils.escapeXml10(xml);
    }
}
