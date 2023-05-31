package com.mokke.componentbuilder.utils;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.FileSystemResource;

public class htmlParser {
    private String absolutePath = new FileSystemResource("src/main/resources/templates").getFile().getAbsolutePath();
    private String fileName;
    private Document doc;
    
    public htmlParser (String fileName) throws IOException {
        this.fileName = fileName;
        File file = new File(absolutePath, this.fileName + ".html");

        doc = Jsoup.parse(file);
    }

    public String getDocAsString() {
        String htmlAsString = doc.html();
        return htmlAsString;
    }
    
}
