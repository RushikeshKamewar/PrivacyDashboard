package rk.android.app.privacydashboard.util;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

import rk.android.app.privacydashboard.R;

public class PieCharts {

    public static PieData getData(Context context, ArrayList<PieEntry> entries,ArrayList<Integer> colors){
        PieDataSet dataSet = new PieDataSet(entries,context.getString(R.string.chart_title));
        dataSet.setDrawIcons(true);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLineColor(Color.TRANSPARENT);
        dataSet.setValueLinePart1Length(0.1f);
        dataSet.setValueLinePart2Length(0.1f);
        dataSet.setValueLineVariableLength(false);
        dataSet.setSliceSpace(3f);
        dataSet.setFormLineWidth(10f);
        dataSet.setDrawValues(false);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Utils.getAttrColor(context,R.attr.colorPrimaryText));
        data.setValueTypeface(ResourcesCompat.getFont(context, R.font.medium));

        return data;
    }

}
