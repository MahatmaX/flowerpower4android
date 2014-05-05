package de.fp4a.gui.plot;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import de.fp4a.timeseries.ITimeSeriesModel;


public class XYTimeSeriesPlot extends XYPlot
{

	public XYTimeSeriesPlot(Context context, String title)
	{
		super(context, title);
	}

	public XYTimeSeriesPlot(Context context, AttributeSet attributes)
	{
		super(context, attributes);
	}

	public XYTimeSeriesPlot(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void init(ITimeSeriesModel timeSeries, String seriesTitle, int lowerRangeBoundary, int upperRangeBoundary, 
			int gradientColorStart, int gradientColorEnd, int gradientEndCoordinateY,
			int lineAndPointColor)
	{
		getLegendWidget().setVisible(false);
        getBackgroundPaint().setColor(Color.WHITE);
        
        getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
        getGraphWidget().setRangeLabelWidth(30.0f); // in order to fully display the range label, e.g. 1013.2
        getGraphWidget().setDomainLabelWidth(20.0f); // in order to fully display the domain label, e.g. 21.Jan
        
        getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        getGraphWidget().getDomainGridLinePaint().setColor(Color.GRAY);
        getGraphWidget().getRangeGridLinePaint().setColor(Color.GRAY);
        getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        getGraphWidget().getRangeGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        getGraphWidget().getDomainOriginLinePaint().setColor(Color.GRAY);
        getGraphWidget().getRangeOriginLinePaint().setColor(Color.GRAY);
 
        setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        getBorderPaint().setStrokeWidth(1);
        getBorderPaint().setAntiAlias(true);
        getBorderPaint().setColor(Color.WHITE);
 
        Paint lineFill = new Paint();
        lineFill.setAlpha(80);
       	lineFill.setShader(new LinearGradient(0, 0, 0, gradientEndCoordinateY, gradientColorStart, gradientColorEnd, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(lineAndPointColor, lineAndPointColor, Color.WHITE, null); // line color, point color, fill color
        formatter.setFillPaint(lineFill);
        
        XYTimeSeries series = new XYTimeSeries(timeSeries, seriesTitle);
        addSeries(series, formatter);
        
        setRangeBoundaries(lowerRangeBoundary, upperRangeBoundary, BoundaryMode.FIXED); 
        setDomainStep(XYStepMode.SUBDIVIDE, 10);
        setRangeStep(XYStepMode.SUBDIVIDE, 11);
        
        getGraphWidget().getDomainLabelPaint().setTextSize(20); // 10 for Gio (320x480), 16 for Nexus Prime(720x1184)
        getGraphWidget().getRangeLabelPaint().setTextSize(20); // 10 for Gio(320x480), 16 for Nexus Prime(720x1184)
        
        NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		setRangeValueFormat(nf);
		
		setDomainValueFormat(new Format() {
        	
        	private SimpleDateFormat formatDay = new SimpleDateFormat("dd.MMM");
        	private SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm:ss");
        	
        	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
    		{
    			long timestamp = ((Number) obj).longValue();
    			Date date = new Date(timestamp);
    			return formatHour.format(date, toAppendTo, pos);
    		}

    		public Object parseObject(String source, ParsePosition pos) { return null; }
        });
	}
}
