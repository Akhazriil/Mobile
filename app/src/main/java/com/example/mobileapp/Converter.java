package com.example.mobileapp;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Converter {
    public Document getXmlFile(){
        try {
            // Установление подключения к сайту cbr
            URL url = new URL("https://www.cbr.ru/scripts/XML_daily.asp");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Получение данных в виде InputStream
            InputStream inputStream = connection.getInputStream();

            // Парсинг XML с помощью DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public double getCurrencyRate(Document document, String currencyCode) {
        // Получаем список элементов "Valute"
        NodeList nodeList = document.getElementsByTagName("Valute");
        for (int i = 0; i < nodeList.getLength(); i++) {
            String charCode = nodeList.item(i).getChildNodes().item(1).getTextContent(); // Код валюты
            if (charCode.equals(currencyCode)) {
                // Получаем курс валюты (значение с запятой, нужно заменить на точку для правильного формата)
                String value = nodeList.item(i).getChildNodes().item(4).getTextContent().replace(",", ".");
                return Double.parseDouble(value);
            }
        }
        return 1.0; // По умолчанию, если это рубль, возвращаем 1.0
    }
}