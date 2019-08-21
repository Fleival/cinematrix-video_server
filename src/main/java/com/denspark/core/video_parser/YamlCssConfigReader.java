package com.denspark.core.video_parser;

import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.utils.file_path_utils.FilePathUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YamlCssConfigReader {
    private static volatile YamlCssConfigReader instance;
    private File configFile;
    private List<SiteCss> siteCssList;
    private String configPath;
    private FilePathUtils filePathUtils;

    private static final Logger logger = LoggerFactory.getLogger(YamlCssConfigReader.class);

    private YamlCssConfigReader() {
        //no instance

    }

    private static YamlCssConfigReader getInstance() {
        YamlCssConfigReader localInstance = instance;
        if (localInstance == null) {
            synchronized (YamlCssConfigReader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new YamlCssConfigReader();
                }
            }
        }
        return localInstance;
    }

    public static YamlCssConfigReader getInstance(String configPath) {
        YamlCssConfigReader localInstanceWithPath = getInstance();
        localInstanceWithPath.configPath = configPath;
        try {
            localInstanceWithPath.initConfigFile();
            localInstanceWithPath.initSiteList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localInstanceWithPath;
    }

    private void initConfigFile() {
        filePathUtils = FilePathUtils.getInstance();
        configFile = new File(filePathUtils.getFullPath(configPath, true));
        logger.info("configFile: " + configFile);
    }


    private void initSiteList() throws Exception {
        List<String> yamlLineCollection = new ArrayList<>();
        if (configFile.exists() && !configFile.isDirectory()) {
//                System.out.println("File exist: " + configFile.getName() + " : " + configFile.getAbsolutePath());

            InputStream input = new FileInputStream(configFile);
            Yaml yaml = new Yaml(new SafeConstructor());

            for (Object data : yaml.loadAll(input)) {
//                    System.out.println(data);
                yamlLineCollection.add(yaml.dump(data));
            }
            input.close();
//                if (this.siteCssList == null) {
//                }
            this.siteCssList = new ArrayList<>();
            for (String s : yamlLineCollection) {
                try {
                    this.siteCssList.add(getSiteMappedToYaml(s));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private SiteCss getSiteMappedToYaml(Object yamlData) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String lineInYaml = (String) yamlData;
        return mapper.readValue(lineInYaml, SiteCss.class);
    }


    public SiteCss getSiteByInt(int i) {
        SiteCss siteCss;
        if (i >= 0 & i < siteCssList.size()) {
            siteCss = siteCssList.get(i);
        } else {
            System.err.println("WARNING , i is not correct, siteCss is null");
            siteCss = null;
        }
        return siteCss;
    }

    SiteCss getSiteByName(String name) {
        SiteCss target = null;
        for (SiteCss siteCss : siteCssList) {
            if (siteCss.getName().equalsIgnoreCase(name)) {
                target = siteCss;

            }
        }
        if (target == null) {
            System.out.println("WARNING \"SiteCss target = null\"");
        }
        return target;
    }

    public void showInfo() {
        for (SiteCss s : siteCssList) {
            System.out.println(s.getName());
        }
    }

    public void showInfo(SiteCss s) {
        System.out.println(s.getName());
    }

    public List<SiteCss> getSiteCssList() {
        return siteCssList;
    }

    public void updateConfigFile() {
        // TODO: 01.05.2018 YAML Update maxPageValue

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer();
        representer.addClassTag(SiteCss.class, Tag.MAP);
        Yaml yaml = new Yaml(representer, options);

//        File tempFile = new File(configFile.getParentFile().getAbsolutePath()+"\\tempFile.yml");
        try {
            FileWriter writer = new FileWriter(configFile);
            Iterator<SiteCss> siteCssIterator = siteCssList.iterator();
            yaml.dumpAll(siteCssIterator, writer);
        } catch (IOException e) {
            System.err.println("can't mergeAndClear config file" + configFile.getAbsolutePath() + "maxPageNumber = 0" + "\nStacktrace : ");
            e.printStackTrace();
        }
    }
}
