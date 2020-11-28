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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Historical extends Fragment {

    private final static DateComparator dateComparator = new DateComparator();

    private LineChart chart;
    private float minChart;
    private float maxChart;

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

        // Data Values
        ArrayList<Entry> values = new ArrayList<>();
        List<CurrencyConvertListedItem> sortedCurrency = currencyHistory.getCurrencyRates();
        Collections.sort(sortedCurrency, dateComparator);
        minChart = sortedCurrency.get(0).getToRate().floatValue();
        maxChart = sortedCurrency.get(0).getToRate().floatValue();
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
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(maxChart, "Maximum Limit");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(minChart, "Minimum Limit");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(maxChart+0.5f);
        leftAxis.setAxisMinimum(minChart-0.5f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
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
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }
}
