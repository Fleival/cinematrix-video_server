package com.denspark.utils.text_utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GenreComparator {
    static List<String> genreList = Arrays.asList(
            "фильм-нуар",
            "игра",
            "для взрослых",
            "концерт",
            "ток-шоу",
            "новости",
            "спорт",
            "вестерн",
            "биография",
            "аниме",
            "музыка",
            "документальный",
            "фантастика",
            "детский",
            "мультфильм",
            "фэнтези",
            "история",
            "военный",
            "короткометражка",
            "приключения",
            "детектив",
            "боевик",
            "семейный",
            "триллер",
            "ужасы",
            "мюзикл",
            "мелодрама",
            "криминал",
            "комедия",
            "драма");

    public static ArrayList<String> genreCorrector(List<String> inputList) {
        ArrayList<String> outStringList = new ArrayList<>();
        inputList.forEach(
                name -> {
                    outStringList.add(analyze(genreList, name));

                    outStringList.add(name);

                    if (name.equalsIgnoreCase("передачи с")) {
                        outStringList.remove(name);
                        outStringList.add("передачи c тв");

                    }
                    if (name.equalsIgnoreCase("Реальное")) {
                        outStringList.remove(name);
                        outStringList.add("реальное тв");
                    }
                    if ((name.equalsIgnoreCase("ТВ"))) {
                        outStringList.remove(name);
                    }
                }
        );
        return outStringList;

    }

    public static String analyze(List<String> stringList, String second) {
        return stringList.stream().filter(
                word -> {
                    int i = StringUtils.indexOfDifference(word, second);
                    int fL = word.length() - 1;
                    return i >= fL;
                }
        ).findFirst()
                .orElse(second);
    }

    public static ArrayList<String> prepareList(ConcurrentHashMap<String, Integer> map, List<String> stringList) {
        ArrayList<String> outStringList = new ArrayList<>();
        List<String> mapKeyList = new ArrayList<>(map.keySet());
        stringList.forEach(
                name -> outStringList.add(analyze(mapKeyList, name))
        );
        return outStringList;
    }

    //For testing purpose:
    public static void main(String[] args) {
        List<String> genreList = Arrays.asList(
                "фильм-нуар",
                "игра",
                "для взрослых",
                "концерт",
                "ток-шоу",
                "новости",
                "спорт",
                "вестерн",
                "биография",
                "аниме",
                "музыка",
                "документальный",
                "фантастика",
                "детский",
                "мультфильм",
                "фэнтези",
                "история",
                "военный",
                "короткометражка",
                "приключения",
                "детектив",
                "боевик",
                "семейный",
                "триллер",
                "ужасы",
                "мюзикл",
                "мелодрама",
                "криминал",
                "комедия",
                "драма");

        ConcurrentHashMap<String, Integer> testMap = new ConcurrentHashMap<>();
        testMap.put("игра", 1);
        testMap.put("для взрослых", 2);
        testMap.put("концерт", 3);
        testMap.put("ток-шоу", 4);
        testMap.put("новости", 5);
        testMap.put("спорт", 6);
        testMap.put("вестерн", 7);
        testMap.put("биография", 8);
        testMap.put("аниме", 9);
        testMap.put("музыка", 10);
        testMap.put("документальный", 11);
        testMap.put("фантастика", 12);
        testMap.put("детский", 13);
        testMap.put("мультфильм", 14);
        testMap.put("фэнтези", 15);
        testMap.put("история", 16);
        testMap.put("военный", 17);
        testMap.put("ТВ", 18);
        testMap.put("реальное", 19);
        testMap.put("короткометражка", 20);
        testMap.put("приключения", 21);
        testMap.put("детектив", 22);
        testMap.put("боевик", 23);
        testMap.put("семейный", 24);
        testMap.put("триллер", 25);
        testMap.put("ужасы", 26);
        testMap.put("мюзикл", 27);
        testMap.put("мелодрама", 28);
        testMap.put("криминал", 29);
        testMap.put("комедия", 30);
        testMap.put("драма", 31);
        List<String> testList = Arrays.asList(
                "музыка",
                "комедия",
                "комедии",
                "видосики",
                "драмы",
                "триллеры"
        );

        String testStr = "helloolohello";
        String s1 = "документальный";
        String s2 = "драмы";
//        String util = StringUtils.chop(testStr);
//        System.out.println(s1.length());
//
//        System.out.println(StringUtils.getCommonPrefix(s1, s2));
//        System.out.println(StringUtils.indexOfDifference(s1, s2));

//        System.out.println(analyze(genreList, s2));
        System.out.println(testList);
        testList = prepareList(testMap, testList);
//        System.out.println(prepareList(testMap, testList));
        System.out.println(testList);


    }
}
