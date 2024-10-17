package com.example.mobileapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Объявление переменных для интерфейса
    private EditText amountEditText; // Поле ввода суммы для конвертации
    private Spinner fromCurrencySpinner; // Спиннер для выбора исходной валюты
    private Spinner toCurrencySpinner; // Спиннер для выбора целевой валюты
    private Button convertButton; // Кнопка для запуска конвертации
    private TextView resultTextView; // Текстовое поле для отображения результата

    // Хранение курсов валют в виде пары "валюта - курс"
    private HashMap<String, Double> currencyRates = new HashMap<>();
    // Список доступных валют
    private String[] currencies = {"AUD", "AZN", "GBP", "AMD", "BYN", "BGN", "BRL", "HUF", "VND", "HKD", "GEL", "DKK", "AED", "USD", "EUR", "RUB"}; // Добавил "RUB"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Установка макета

        // Привязка элементов интерфейса к переменным
        amountEditText = findViewById(R.id.amountEditText);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Создание адаптера для спиннеров с валютами
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Установка стиля для выпадающего списка
        fromCurrencySpinner.setAdapter(adapter); // Применение адаптера к спиннеру исходной валюты
        toCurrencySpinner.setAdapter(adapter); // Применение адаптера к спиннеру целевой валюты

        new FetchCurrencyRatesTask().execute(); // Запуск задачи для получения курсов валют

        // Установка слушателя на кнопку конвертации
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency(); // Вызов метода конвертации при нажатии на кнопку
            }
        });
    }

    private void convertCurrency() {
        // Получение введенной суммы и выбранных валют
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        String toCurrency = toCurrencySpinner.getSelectedItem().toString();

        // Получение курсов для выбранных валют
        double fromRate = currencyRates.get(fromCurrency);
        double toRate = currencyRates.get(toCurrency);

        // Конвертация суммы и отображение результата
        double result = amount * (toRate / fromRate);
        resultTextView.setText(String.format("%.2f %s", result, toCurrency));
    }

    // Асинхронный класс для получения курсов валют
    private class FetchCurrencyRatesTask extends AsyncTask<Void, Void, HashMap<String, Double>> {
        @Override
        protected HashMap<String, Double> doInBackground(Void... voids) {
            try {
                // Установка URL для получения XML с курсами валют
                URL url = new URL("https://www.cbr.ru/scripts/XML_daily.asp");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET"); // Установка метода запроса
                connection.connect(); // Подключение к ресурсу

                // Получение входного потока данных
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream); // Парсинг XML-документа

                NodeList nodeList = document.getElementsByTagName("Valute"); // Получение списка всех валют
                HashMap<String, Double> rates = new HashMap<>(); // Хранилище для курсов валют

                // Извлечение курсов валют из XML
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent(); // Получение кода валюты
                    String value = element.getElementsByTagName("Value").item(0).getTextContent().replace(',', '.'); // Получение значения курса
                    rates.put(charCode, Double.parseDouble(value)); // Добавление в хранилище курсов
                }

                // Добавляем курс рубля как 1, поскольку мы используем его в качестве базовой валюты
                rates.put("RUB", 1.0);

                return rates; // Возвращение курсов валют
            } catch (Exception e) {
                e.printStackTrace(); // Вывод ошибки, если возникла проблема
                return null; // Возвращение null в случае ошибки
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> rates) {
            if (rates != null) {
                currencyRates.putAll(rates); // Добавление полученных курсов в хранилище
            }
        }
    }
}
