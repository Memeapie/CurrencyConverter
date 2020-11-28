package com.gre.assessment.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.gre.assessment.Models.CurrencyConvert;
import com.gre.assessment.R;
import com.gre.assessment.Utils.EUCentralBankAPIConnectorUtil;

import java.text.DecimalFormat;
import java.util.Optional;

public class Convert extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.convert, container, false);
        final EUCentralBankAPIConnectorUtil connectorUtil = new EUCentralBankAPIConnectorUtil();
        final DecimalFormat decimalFormatter = new DecimalFormat("#.00");

        final Button convert = view.findViewById(R.id.convertButton);

        final Spinner fromCurrency = view.findViewById(R.id.spinnerFrom);
        final Spinner toCurrency = view.findViewById(R.id.spinnerTo);

        final TextView amountDirty = view.findViewById(R.id.textNumberDecimal);
        final TextView outputConversionRate = view.findViewById(R.id.textOuputConversionRate);
        final TextView outputConversionAmount = view.findViewById(R.id.textOutputConversionAmount);

        convert.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (fromCurrency.getSelectedItem() == toCurrency.getSelectedItem()){
                    Snackbar.make(view, "Converting Currencies are the same!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    final Optional<Double> amountClean = Optional.of(Double.valueOf(amountDirty.getText().toString()));
                    final CurrencyConvert convert = connectorUtil.getLatestExchangeRate(
                            fromCurrency.getSelectedItem().toString(),
                            toCurrency.getSelectedItem().toString()
                    );
                    Optional<String> errorOut = Optional.ofNullable(convert.getErrorOut());
                    if (errorOut.isPresent()){
                        Snackbar.make(view, errorOut.get(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        final String convertedAmount = decimalFormatter.format(amountClean.get() * convert.getToRate());
                        outputConversionRate.setText("Today's Conversion Rate is " + decimalFormatter.format(convert.getToRate()));
                        outputConversionAmount.setText(amountClean.get() + " " + convert.getFromCurrencyCode() + " currently converts to " + convertedAmount + " " + convert.getToCurrencyCode());
                    }
                }
            }
        });

        return view;
    }
}
