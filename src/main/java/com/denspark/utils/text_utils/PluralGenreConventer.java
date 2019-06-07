package com.denspark.utils.text_utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluralGenreConventer {
    String word;
//    а, о, и, е, ё, э, ы, у, ю, я
    List<String> vowels = Arrays.asList("а","о","и","е","ё","э","ы","у","ю","я");
//    б, в, г, д, ж, з, й, к, л, м, н, п, р, с, т, ф, х, ц, ч, ш, щ
    List<String> consonants = Arrays.asList("б","в","г","д","ж","з","й","к","л","м","н","п","р","с","т","ф","х","ц","ч","ш","щ");
    WordGender wordGender;
    WordPlural wordPlural;

    enum WordGender{
        MALE,
        FEMALE,
        MIDDLE;
    }
    enum WordPlural{
        ONE,
        MANY;
    }

    public void annalyze(){


    }

//    документальный
//    документальный
}

