package com.denspark.utils.text_utils;

import java.util.ArrayList;

public class RegexTextUtils {

    public static ArrayList<String> getListOfSpaceAndCommaSeparatedWords(String s) {
        String[] array = splitMyWords(s,"(?is)((?<=[\\w\\p{Ll}])(\\s+)(?=[\\w\\p{Lu}]))|((?<=[\\w\\p{Ll}])\\s*,\\s*(?=[\\w\\p{Ll}]))|((?<=[\\w\\p{Ll}])\\s*,\\s*(?=[\\w\\p{Lu}]))|((?<=[\\w\\p{Lu}])\\s*,\\s*(?=[\\w\\p{Ll}]))|((?<=[\\w\\p{Lu}])\\s*,\\s*(?=[\\w\\p{Lu}]))|,");
        ArrayList<String> words = new ArrayList<>();
        for (String word : array) {
            words.add(word.trim());
        }
        return words;
    }
    public static ArrayList<String> getListOfCommaSeparatedWords(String s) {
        String[] array = splitMyWords(s,"((?<=[\\w\\p{Ll}])\\s*,\\s*(?=[\\w\\p{Ll}]))|((?<=[\\w\\p{Ll}])\\s*,\\s*(?=[\\w\\p{Lu}]))|((?<=[\\w\\p{Lu}])\\s*,\\s*(?=[\\w\\p{Ll}]))|((?<=[\\w\\p{Lu}])\\s*,\\s*(?=[\\w\\p{Lu}]))|((?<=[\\w\\p{Lu}])*,\\s*(?=[\\w\\p{Lu}]))|((?<=[\\w\\p{Ll}])\\s*,\\s*(?=«[\\w\\p{Lu}]))|((?<=»),\\s*(?=«[\\w\\p{Lu}]))|((?<=[\\w\\p{Lu}]\\))\\s*,\\s*(?=[\\w\\p{Ll}]))");
        ArrayList<String> words = new ArrayList<>();
        for (String word : array) {
            words.add(word.trim());
        }
        return words;
    }

    private static String[] splitMyWords(String s, String regex){
        String[] wordsArray = s.split(regex);

        return wordsArray;
    }

    public static ArrayList<String> extractFirstNlettersAndMakePattern(ArrayList<String> list, int n){
        ArrayList<String> outputList = new ArrayList<>();

        for (String s : list) {
            outputList.add("%"+s.substring(0,n)+"%");
        }
        return outputList;
    }


}
