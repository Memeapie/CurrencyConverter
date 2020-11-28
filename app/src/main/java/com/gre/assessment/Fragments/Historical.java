package com.gre.assessment.Fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.gre.assessment.Models.CurrencyConvertListedItem;
import com.gre.assessment.Models.CurrencyHistory;
import com.gre.assessment.R;
import com.gre.assessment.Utils.EUCentralBankAPIConnectorUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Historical extends Fragment {

    private LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.historical, container, false);
        final EUCentralBankAPIConnectorUtil connectorUtil = new EUCentralBankAPIConnectorUtil();

        final Button refreshHistorical = view.findViewById(R.id.refreshButton);

        final Spinner fromCurrency = view.findViewById(R.id.spinnerFromHistorical);
        final Spinner toCurrency = view.findViewById(R.id.spinnerToHistorical);

        chart = view.findViewById(R.id.historicalDataChart);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        refreshHistorical.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (fromCurrency.getSelectedItem() == toCurrency.getSelectedItem()){
                    Snackbar.make(view, "Converting Currencies are the same!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    final LocalDateTime dateTimeNow = LocalDateTime.now();

                    CurrencyHistory currencyHistory = connectorUtil.getHistoricalExchangeRate(
                            dateTimeFormatter.format(dateTimeNow.minusYears(1)),
                            dateTimeFormatter.format(dateTimeNow),
                            fromCurrency.getSelectedItem().toString(),
                            toCurrency.getSelectedItem().toString()
                    );

                    Optional<String> errorOut = Optional.ofNullable(currencyHistory.getErrorOut());
                    if (errorOut.isPresent()){
                        Snackbar.make(view, errorOut.get(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        renderData(currencyHistory);
                    }
                }
            }
        });
        return view;
    }

    public void renderData(CurrencyHistory currencyHistory) {

        // Data Set
        ArrayList<Entry> values = new ArrayList<>();
        List<CurrencyConvertListedItem> data = currencyHistory.getCurrencyRates();

        for (int x = 0; x < data.size(); x++){
            values.add(new Entry(9, 190));
        }

        // Left Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(data.size());
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);

        // Top Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(2.5f);
        leftAxis.setAxisMinimum(0.5f);
        leftAxis.enableGridDashedLine(10f, 0.5f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        chart.getAxisRight().setEnabled(false);

        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.fade_blue);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);
        }
    }

}
