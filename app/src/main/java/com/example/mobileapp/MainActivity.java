package com.example.mobileapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

    private EditText amountEditText;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private TextView resultTextView;

    private HashMap<String, Double> currencyRates = new HashMap<>();
    private String[] currencies = {"AUD", "AZN", "GBP", "AMD", "BYN", "BGN", "BRL", "HUF", "VND", "HKD", "GEL", "DKK", "AED", "USD", "EUR", "RUB"}; // Добавил "RUB"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEditText = findViewById(R.id.amountEditText);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        resultTextView = findViewById(R.id.resultTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        new FetchCurrencyRatesTask().execute();

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        String toCurrency = toCurrencySpinner.getSelectedItem().toString();

        double fromRate = currencyRates.get(fromCurrency);
        double toRate = currencyRates.get(toCurrency);

        double result = amount * (toRate / fromRate);
        resultTextView.setText(String.format("%.2f %s", result, toCurrency));
    }

    private class FetchCurrencyRatesTask extends AsyncTask<Void, Void, HashMap<String, Double>> {
        @Override
        protected HashMap<String, Double> doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.cbr.ru/scripts/XML_daily.asp");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                NodeList nodeList = document.getElementsByTagName("Valute");
                HashMap<String, Double> rates = new HashMap<>();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
                    String value = element.getElementsByTagName("Value").item(0).getTextContent().replace(',', '.');
                    rates.put(charCode, Double.parseDouble(value));
                }

                // Добавляем курс рубля как 1, поскольку мы используем его в качестве базовой валюты
                rates.put("RUB", 1.0);

                return rates;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Double> rates) {
            if (rates != null) {
                currencyRates.putAll(rates);
            }
        }
    }
}
