package com.denspark.utils.xml;

import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.XLink;
import com.ximpleware.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VTDXMLreader {
    File inputXml;
    FileInputStream xmlFileInputStream;
    byte[] byteBuffer;
    Set<XLink> xLinks = new LinkedHashSet<>();
    Set<Link> links = new LinkedHashSet<>();
    ConcurrentHashMap<String, XLink> xLinkUrlMap = new ConcurrentHashMap<>();
    Map<String, Integer> xPersonCache = new LinkedHashMap<>();

    private void parseXmlXlinkData() {
        try {
            //instantiate VTDGen
            //and call parse
            VTDGen vtdGen = new VTDGen();
            vtdGen.setDoc(this.byteBuffer);
            vtdGen.parse(false);

            VTDNav vtdNav = vtdGen.getNav();
            AutoPilot ap = new AutoPilot(vtdNav);

            ap.selectXPath("//xlink");
            while (ap.evalXPath() != -1) {
//                System.out.println("Inside xlink tag");
                XLink xLink = new XLink();

                //now find the children
                if (vtdNav.toElement(VTDNav.FIRST_CHILD, "id")) {
                    int val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        if (value.equals("")) {
                            xLink.setId(0);
                        } else {
                            xLink.setId(Integer.parseInt(value));
                        }
//                        System.out.println("\tid  " + value);
                    }
                    vtdNav.toElement(VTDNav.NEXT_SIBLING, "type");
                    val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        xLink.setType(value);
//                        System.out.println("\turl  " + value);
                    }
                    vtdNav.toElement(VTDNav.NEXT_SIBLING, "url");
                    val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        xLink.setUrl(value);
//                        System.out.println("\turl  " + value);
                    }
                    vtdNav.toElement(VTDNav.NEXT_SIBLING, "in_database");
                    val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        xLink.setExistsInDb(Boolean.parseBoolean(value));
//                        System.out.println("\tin_database  " + value);
                    }

                    xLinks.add(xLink);
                }
                //move back to parent
                vtdNav.toElement(VTDNav.PARENT);
            }
        } catch (ParseException e) {
            System.out.println(" XML file parsing error \n" + e);
        } catch (NavException e) {
            System.out.println(" Exception during navigation " + e);
        } catch (XPathParseException e) {

        } catch (XPathEvalException e) {

        }
    }

    private void parseXmlXpersonCacheData() {
        try {
            //instantiate VTDGen
            //and call parse
            VTDGen vtdGen = new VTDGen();
            vtdGen.setDoc(this.byteBuffer);
            vtdGen.parse(false);

            VTDNav vtdNav = vtdGen.getNav();
            AutoPilot ap = new AutoPilot(vtdNav);


            ap.selectXPath("//xperson");

            while (ap.evalXPath() != -1) {
//                System.out.println("Inside xlink tag");
//                XLink xLink = new XLink();
                Integer id = null;
                String name = null;

                //now find the children
                if (vtdNav.toElement(VTDNav.FIRST_CHILD, "id")) {
                    int val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        if (value.equals("")) {
                            id = 0;
                        } else {
                            id = Integer.parseInt(value);
                        }
//                        System.out.println("\tid  " + value);
                    }
                    vtdNav.toElement(VTDNav.NEXT_SIBLING, "name");
                    val = vtdNav.getText();
                    if (val != -1) {
                        String value = vtdNav.toNormalizedString(val);
                        name = value;
//                        System.out.println("\turl  " + value);
                    }
                    xPersonCache.put(name, id);
                }
                //move back to parent
                vtdNav.toElement(VTDNav.PARENT);
            }
        } catch (ParseException e) {
            System.out.println(" XML file parsing error \n" + e);
        } catch (NavException e) {
            System.out.println(" Exception during navigation " + e);
        } catch (XPathParseException e) {

        } catch (XPathEvalException e) {

        }
    }

    private String toNormalizedString(String name, int val, VTDNav vn) throws NavException {
        String strValue = null;
        if (val != -1) {
            strValue = vn.toNormalizedString(val);
            System.out.println(name + ":: " + strValue);
        }
        return strValue;
    }

    private String toNormalizedStringText(String tagName, VTDNav vn) throws NavException {
        return toNormalizedString(tagName, vn.getText(), vn);
    }

    public void printXlinkSetResult() {
        System.out.println("Parsed items count in SET is: " + xLinks.size());
    }

    public Set<XLink> getXlinkSetResult(String xmlFile) {
        setInputXml(xmlFile);
        if (xLinks.isEmpty()) {
            parseXmlXlinkData();
        }
        return xLinks;
    }

    public Set<Link> getLinkSet(String xmlFile) {
        setInputXml(xmlFile);
        if (links.isEmpty()) {

            try {
                //instantiate VTDGen
                //and call parse
                VTDGen vtdGen = new VTDGen();
                vtdGen.setDoc(this.byteBuffer);
                vtdGen.parse(false);

                VTDNav vtdNav = vtdGen.getNav();
                AutoPilot ap = new AutoPilot(vtdNav);

                ap.selectXPath("//link");
                while (ap.evalXPath() != -1) {
//                System.out.println("Inside xlink tag");
                    Link link = new Link();

                    //now find the children
                    if (vtdNav.toElement(VTDNav.FIRST_CHILD, "url")) {
                        int val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            link.setUrl(value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "processed");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            link.setProcessed(Boolean.parseBoolean(value));
                        }

                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "error");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            link.setError(Boolean.parseBoolean(value));
                        }

                        links.add(link);
                    }
                    //move back to parent
                    vtdNav.toElement(VTDNav.PARENT);
                }
            } catch (ParseException e) {
                System.out.println(" XML file parsing error \n" + e);
            } catch (NavException e) {
                System.out.println(" Exception during navigation " + e);
            } catch (XPathParseException e) {

            } catch (XPathEvalException e) {

            }

        }
        return links;
    }


    public Set<XLink> getxLinkSet(String xmlFile) {
        setInputXml(xmlFile);

        if (xLinks.isEmpty()) {

            try {
                //instantiate VTDGen
                //and call parse
                VTDGen vtdGen = new VTDGen();
                vtdGen.setDoc(this.byteBuffer);
                vtdGen.parse(false);

                VTDNav vtdNav = vtdGen.getNav();
                AutoPilot ap = new AutoPilot(vtdNav);

                ap.selectXPath("//xlink");
                while (ap.evalXPath() != -1) {
//                System.out.println("Inside xlink tag");
                    XLink xLink = new XLink();

                    //now find the children
                    if (vtdNav.toElement(VTDNav.FIRST_CHILD, "id")) {
                        int val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            if (value.equals("")) {
                                xLink.setId(0);
                            } else {
                                xLink.setId(Integer.parseInt(value));
                            }
//                        System.out.println("\tid  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "type");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setType(value);
//                        System.out.println("\turl  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "url");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setUrl(value);
//                        System.out.println("\turl  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "update_date");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setUpdateDate(value);
//                        System.out.println("\tin_database  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "in_database");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setExistsInDb(Boolean.parseBoolean(value));
//                        System.out.println("\tin_database  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "need_to_update_record");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setNeedToUpdateRecord(Boolean.parseBoolean(value));
//                        System.out.println("\tin_database  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "pos_rating");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setPosRating(Integer.parseInt(value));
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "neg_rating");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setNegRating(Integer.parseInt(value));
                        }

                        xLinks.add(xLink);
                    }
                    //move back to parent
                    vtdNav.toElement(VTDNav.PARENT);
                }
            } catch (ParseException e) {
                System.out.println(" XML file parsing error \n" + e);
            } catch (NavException e) {
                System.out.println(" Exception during navigation " + e);
            } catch (XPathParseException e) {

            } catch (XPathEvalException e) {

            }

        }
        return xLinks;
    }

    public ConcurrentHashMap<String, XLink> getXlinksToUrlMap(String xmlFile) {
        setInputXml(xmlFile);

        if (xLinkUrlMap.isEmpty()) {

            try {
                //instantiate VTDGen
                //and call parse
                VTDGen vtdGen = new VTDGen();
                vtdGen.setDoc(this.byteBuffer);
                vtdGen.parse(false);

                VTDNav vtdNav = vtdGen.getNav();
                AutoPilot ap = new AutoPilot(vtdNav);

                ap.selectXPath("//xlink");
                while (ap.evalXPath() != -1) {
//                System.out.println("Inside xlink tag");
                    XLink xLink = new XLink();

                    //now find the children
                    if (vtdNav.toElement(VTDNav.FIRST_CHILD, "id")) {
                        int val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            if (value.equals("")) {
                                xLink.setId(0);
                            } else {
                                xLink.setId(Integer.parseInt(value));
                            }
//                        System.out.println("\tid  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "type");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setType(value);
//                        System.out.println("\turl  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "url");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setUrl(value);
//                        System.out.println("\turl  " + value);
                        }
                        vtdNav.toElement(VTDNav.NEXT_SIBLING, "in_database");
                        val = vtdNav.getText();
                        if (val != -1) {
                            String value = vtdNav.toNormalizedString(val);
                            xLink.setExistsInDb(Boolean.parseBoolean(value));
//                        System.out.println("\tin_database  " + value);
                        }

                        xLinkUrlMap.put(xLink.getUrl(), xLink);
                    }
                    //move back to parent
                    vtdNav.toElement(VTDNav.PARENT);
                }
            } catch (ParseException e) {
                System.out.println(" XML file parsing error \n" + e);
            } catch (NavException e) {
                System.out.println(" Exception during navigation " + e);
            } catch (XPathParseException e) {

            } catch (XPathEvalException e) {

            }

        }
        return xLinkUrlMap;
    }

    public Map<String, Integer> getXPersonCache(String xmlFile) {
        setInputXml(xmlFile);
        if (xPersonCache.isEmpty()) {
            parseXmlXpersonCacheData();
        }
        return xPersonCache;
    }

    public String getInputXml() {
        return inputXml.getAbsolutePath();
    }

    private void setInputXml(String path) {
        inputXml = new File(path);
        try {
            xmlFileInputStream = new FileInputStream(inputXml);
            byteBuffer = new byte[(int) inputXml.length()];
            xmlFileInputStream.read(byteBuffer);
            xmlFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

