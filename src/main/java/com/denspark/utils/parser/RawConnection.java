package com.denspark.utils.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class RawConnection {
    public BufferedReader reader;
    public StringBuilder sb = new StringBuilder();

    public BufferedReader setConnection(String urlString) throws IOException {
        URL testSite = new URL(urlString);
        HttpURLConnection apiConnection = (HttpURLConnection) testSite.openConnection();

        apiConnection.setRequestMethod("GET");
        apiConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        apiConnection.setRequestProperty("Accept-Encoding", "gzip");

        apiConnection.connect();


        InputStream gzippedResponse = apiConnection.getInputStream();
        InputStream decompressedResponse = new GZIPInputStream(gzippedResponse);
        InputStreamReader reader = new InputStreamReader(decompressedResponse, "UTF-8");
        BufferedReader in = new BufferedReader(reader);
        return in;


    }

    public void read(BufferedReader in, StringBuilder sb) {
        String readed = "";
        try {
            while ((readed = in.readLine()) != null) {
//                System.out.println(readed);
                sb.append(readed);
            }
        } catch (IOException e) {
            sb.append(readed);
        }
//        catch (NullPointerException e){
//            e.printStackTrace();
//        }
    }

}
