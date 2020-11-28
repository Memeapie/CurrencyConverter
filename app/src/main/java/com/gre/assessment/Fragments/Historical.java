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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
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
import com.gre.assessment.Utils.DateComparator;
import com.gre.assessment.Utils.EUCentralBankAPIConnectorUtil;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Historical extends Fragment {

    private static final DateComparator dateComparator = new DateComparator();
    private static final DecimalFormat decimalFormatter = new DecimalFormat("0.00");
    private static final EUCentralBankAPIConnectorUtil connectorUtil = new EUCentralBankAPIConnectorUtil();
    private static final LocalDateTime dateTimeNow = LocalDateTime.now();

    private LineChart chart;
    private Spinner fromCurrency;
    private Spinner toCurrency;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.historical, container, false);

        fromCurrency = view.findViewById(R.id.spinnerFromHistorical);
        toCurrency = view.findViewById(R.id.spinnerToHistorical);

        Button twoYearsHistory = view.findViewById(R.id.timePeriodButton2yr);
        Button oneYearHistory = view.findViewById(R.id.timePeriodButton1yr);
        Button sixMonthsHistory = view.findViewById(R.id.timePeriodButton6mth);
        Button oneMonthHistory = view.findViewById(R.id.timePeriodButton1mnth);

        chart = view.findViewById(R.id.historicalDataChart);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        twoYearsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(dateTimeNow.minusYears(2));
            }
        });

        oneYearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(dateTimeNow.minusYears(1));
            }
        });

        sixMonthsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(dateTimeNow.minusMonths(6));
            }
        });

        oneMonthHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(dateTimeNow.minusMonths(1));
            }
        });

        return view;
    }


    public void updateData(LocalDateTime endDate) {
        if (fromCurrency.getSelectedItem() == toCurrency.getSelectedItem()){
            Snackbar.make(view, "Converting Currencies are the same!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            CurrencyHistory currencyHistory = connectorUtil.getHistoricalExchangeRate(
                    dateTimeFormatter.format(endDate),
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

    public void renderData(CurrencyHistory currencyHistory) {

        // Data Values
        ArrayList<Entry> values = new ArrayList<>();
        List<CurrencyConvertListedItem> sortedCurrency = currencyHistory.getCurrencyRates();
        Collections.sort(sortedCurrency, dateComparator);
        float minChart = sortedCurrency.get(0).getToRate().floatValue();
        float maxChart = sortedCurrency.get(0).getToRate().floatValue();
        for (int x = 0; x < sortedCurrency.size(); x++){
            values.add(new Entry(x, sortedCurrency.get(x).getToRate().floatValue()));
            if (sortedCurrency.get(x).getToRate().floatValue() < minChart){
                minChart = sortedCurrency.get(x).getToRate().floatValue();
            } else if (sortedCurrency.get(x).getToRate().floatValue() > maxChart){
                maxChart = sortedCurrency.get(x).getToRate().floatValue();
            }
        }

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(sortedCurrency.size());
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLabels(false);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine limitLineMax = new LimitLine(maxChart, "Maximum: " + decimalFormatter.format(maxChart));
        limitLineMax.setLineWidth(2f);
        limitLineMax.enableDashedLine(10f, 10f, 0f);
        limitLineMax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLineMax.setTextSize(10f);

        LimitLine limitLineMin = new LimitLine(minChart, "Minimum: " + decimalFormatter.format(minChart));
        limitLineMin.setLineWidth(2f);
        limitLineMin.enableDashedLine(10f, 10f, 0f);
        limitLineMin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limitLineMin.setTextSize(10f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limitLineMax);
        leftAxis.addLimitLine(limitLineMin);
        leftAxis.setAxisMaximum(maxChart +0.1f);
        leftAxis.setAxisMinimum(minChart -0.1f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        chart.getAxisRight().setEnabled(false);

        LineDataSet currencyDataSet;
        currencyDataSet = new LineDataSet(values, "Conversion Rate for " + currencyHistory.getFromCurrencyCode() + " to " + currencyHistory.getToCurrencyCode());
        currencyDataSet.setDrawIcons(false);
        currencyDataSet.enableDashedLine(10f, 5f, 0f);
        currencyDataSet.enableDashedHighlightLine(10f, 5f, 0f);
        currencyDataSet.setColor(Color.DKGRAY);
        currencyDataSet.setCircleColor(Color.DKGRAY);
        currencyDataSet.setLineWidth(1f);
        currencyDataSet.setDrawCircles(false);
        currencyDataSet.setValueTextSize(9f);
        currencyDataSet.setDrawFilled(true);
        currencyDataSet.setFormLineWidth(2f);
        currencyDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        currencyDataSet.setFormSize(15.f);

        Description des = chart.getDescription();
        des.setEnabled(false);

        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.fade_blue);
            currencyDataSet.setFillDrawable(drawable);
        } else {
            currencyDataSet.setFillColor(Color.DKGRAY);
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(currencyDataSet);
        LineData data = new LineData(dataSets);
        chart.setData(data);
    }
}
