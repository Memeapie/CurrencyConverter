package com.gre.assessment.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.gre.assessment.R;
import com.gre.assessment.Utils.EUCentralBankAPIConnectorUtil;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Historical extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.historical, container, false);
        final EUCentralBankAPIConnectorUtil connectorUtil = new EUCentralBankAPIConnectorUtil();
        final DecimalFormat decimalFormatter = new DecimalFormat("#.00");

        final Button refreshHistorical = view.findViewById(R.id.refreshButton);

        final Spinner fromCurrency = view.findViewById(R.id.spinnerFromHistorical);
        final Spinner toCurrency = view.findViewById(R.id.spinnerToHistorical);

        refreshHistorical.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                final LocalDateTime dateTimeNow = LocalDateTime.now();

                String convert = connectorUtil.getHistoricalExchangeRate(
                        dateTimeFormatter.format(dateTimeNow),
                        dateTimeFormatter.format(dateTimeNow.minusYears(1)),
                        fromCurrency.getSelectedItem().toString(),
                        toCurrency.getSelectedItem().toString()
                );
                System.out.println(convert);
            }
        });

        return view;
    }
}
