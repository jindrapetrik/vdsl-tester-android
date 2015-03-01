/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.jpexs.vdsltester.view;

import java.util.ArrayList;
import java.util.List;

import com.jpexs.vdsltester.R;
import com.jpexs.vdsltester.model.Band;
import com.jpexs.vdsltester.model.Main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Custom android.view.View that shows a pie chart and, optionally, a label.
 */
public class GraphView extends ViewGroup {
	Paint p;
	List<Double> values = new ArrayList<Double>();

	int curW;
	int curH;

	double valueMax;
	double valueMin;
	List<Band> USBandPlan;
	List<Band> DSBandPlan;
	int cursorX = -1;
	int cursorY = -1;

	final int DEF_GRAPH_TOP=30;
	int graphTop=30;
	final int DEF_GRAPH_RIGHT=10;
	int graphRight=10;
	int leftRulerWidth = 30;
	final int DEF_LEFT_RULER_WIDTH=80;
	int bottomRulerHeight = 40;
	final int DEF_BOTTOM_RULER_HEIGHT=40;
	int lineLength = 6;
	int lineSpace = 3;
	final int DEF_LINE_LENGTH = 10;
	final int DEF_LINE_SPACE = 5;
	String unit;
	String value;

	Paint unusedCarrierPaint = createSolidFillPaint(Color.DKGRAY);
	Paint USCarrierPaint = createSolidFillPaint(Color.RED);
	Paint DSCarrierPaint = createSolidFillPaint(Color.rgb(0, 0, 255));

	Paint rulerPaint = createStrokePaint(Color.BLACK);

	Paint backgroundPaint = createSolidFillPaint(Color.WHITE);
	Paint darkPaint = createSolidFillPaint(Color.DKGRAY);
	Paint rectPaint = createStrokePaint(Color.BLACK);
	
	Paint whiteRectPaint = createStrokePaint(Color.WHITE);

	Paint unusedCarrierBackgroundPaint = createSolidFillPaint(Color.rgb(0xf2,
			0xf2, 0xf2));
	Paint USCarrierBackgroundPaint = createSolidFillPaint(Color.rgb(0xff, 0xdd,
			0xdd));
	Paint DSCarrierBackgroundPaint = createSolidFillPaint(Color.rgb(0xdd, 0xdd,
			0xff));

	public void setMinMax(double valueMax, double valueMin) {
		this.valueMax = valueMax;
		this.valueMin = valueMin;
	}

	final int CAR_NONE = 0;
	final int CAR_US = 1;
	final int CAR_DS = 2;

	private int getCarrierMode(int carrier) {
		if ((USBandPlan == null) || (DSBandPlan == null)) {
			return CAR_NONE;
		}
		Band selectedBand = null;

		int mode = CAR_NONE;
		for (int u = 0; u < USBandPlan.size(); u++) {
			Band band = USBandPlan.get(u);
			if (band.contains(carrier)) {
				mode = CAR_US;
				if (selectedBand == null) {
					selectedBand = band;
				} else {
					if (band.from > selectedBand.from) {
						selectedBand = band;
					}
				}
			}
		}

		for (int d = 0; d < DSBandPlan.size(); d++) {
			Band band = DSBandPlan.get(d);
			if (band.contains(carrier)) {
				if (selectedBand == null) {
					selectedBand = band;
					mode = CAR_DS;
				} else {
					if (band.from > selectedBand.from) {
						selectedBand = band;
						mode = CAR_DS;
					}
				}
			}
		}
		return mode;
	}

	private Paint getCarrierColor(int carrier) {
		int mode = getCarrierMode(carrier);
		switch (mode) {
		case CAR_US:
			return USCarrierPaint;
		case CAR_DS:
			return DSCarrierPaint;
		default:
			return unusedCarrierPaint;
		}
	}

	private Paint getCarrierBackgroundPaintColor(int carrier) {
		int mode = getCarrierMode(carrier);
		switch (mode) {
		case CAR_US:
			return USCarrierBackgroundPaint;
		case CAR_DS:
			return DSCarrierBackgroundPaint;
		default:
			return unusedCarrierBackgroundPaint;
		}
	}

	public void setValues(List<Double> values, List<Band> USBandPlan,
			List<Band> DSBandPlan) {
		this.USBandPlan = USBandPlan;
		this.DSBandPlan = DSBandPlan;
		this.values = values;
		valueMin=Integer.MAX_VALUE;
		valueMax=Integer.MIN_VALUE;
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				double val = (double) values.get(i);
				if (val < valueMin) {
					valueMin = val;
				}
				if (val > valueMax) {
					valueMax = val;
				}
			}
		}
		updateImage();
		invalidate();
	}

	Bitmap img;
	Paint whitePaint;

	private void updateImage() {
		initSize();
		int MAXIMAGEWIDTH = 999999999;//getWidth() - leftRulerWidth;
		if (values != null) {
			Color lineColors[] = null;
			List<Color[]> lines = new ArrayList<Color[]>();
			int imgHeight = getHeight() - bottomRulerHeight - graphTop; // getHeight();
			int imgWidth = getWidth() - leftRulerWidth - graphRight; // getWidth();
			if (imgHeight < 1) {
				imgHeight = 100;
			}
			if (imgWidth < 1) {
				imgWidth = 100;
			}
			img = Bitmap.createBitmap(imgWidth, imgHeight,
					Bitmap.Config.ARGB_8888);
			Canvas grphcs = new Canvas(img);
			int count = values.size();
			double delta = valueMax - valueMin;
			double multiplier = ((double) imgHeight) / delta;
			int xcount = 0;
			int lastX = -1;
			int deltax = 0;
			for (int i = 0; i < count; i++) {
				int x = (int) Math.round(i * imgWidth / (double) count);
				if (lastX != x) {
					deltax += x - lastX;
				}
				if (((lastX != x) && (xcount >= MAXIMAGEWIDTH))
						|| (i == count - 1)) {
					if (i == count - 1) {
						i++;
						deltax++;
					}
					if (lastX > -1) {
						Bitmap subImg = Bitmap.createBitmap(xcount, imgHeight,
								Bitmap.Config.ARGB_8888);
						Canvas subGr = new Canvas(subImg);
						subGr.drawRect(0, 0, xcount, imgHeight, backgroundPaint);
						for (int p = 0; p < xcount; p++) {
							int tone = i - xcount + p;
							double val = 0;
							val = values.get(tone);
							int ival = (int) (multiplier * (val - valueMin));
							subGr.drawLine(p, imgHeight, p, 0,
									getCarrierBackgroundPaintColor(tone));
							subGr.drawLine(p, imgHeight, p, imgHeight - ival,
									getCarrierColor(tone));
						}
						Paint fillPaint = new Paint();
						fillPaint.setStyle(Paint.Style.FILL);
						grphcs.drawBitmap(subImg, new Rect(0, 0, xcount,
								imgHeight), new Rect(1 + x - deltax, 0, deltax,
								imgHeight), null);
						deltax = 1;
					}
					xcount = 0;
				}
				lastX = x;
				xcount++;
			}
		}
	}

	public void diplayPos(int x, int y) {
		cursorX = x;
		cursorY = y;
		invalidate();
	}

	/**
	 * Interface definition for a callback to be invoked when the current item
	 * changes.
	 */
	public interface OnCurrentItemChangedListener {
		void OnCurrentItemChanged(GraphView source, int currentItem);
	}

	/**
	 * Class constructor taking only a context. Use this constructor to create
	 * {@link PieChart} objects from your own code.
	 * 
	 * @param context
	 */
	public GraphView(Context context) {
		super(context);
		init();
	}

	public Paint createSolidFillPaint(int color) {
		Paint ret = new Paint();
		ret.setColor(color);
		ret.setStyle(Paint.Style.FILL);
		return ret;
	}

	public Paint createStrokePaint(int color) {
		Paint ret = new Paint();
		ret.setColor(color);
		ret.setStyle(Paint.Style.STROKE);
		return ret;
	}

	public void init() {
		p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.RED);
		List<Double> v = new ArrayList<Double>();
		v.add(0.0);
		
		for (int i = 0; i < 512; i++) {
			//v.add(0.0);
			//v.add((Double) (double)(int)(Math.random()*9));
		}				
		setValues(v, new ArrayList<Band>(), new ArrayList<Band>());
	}

	/**
	 * Class constructor taking a context and an attribute set. This constructor
	 * is used by the layout engine to construct a {@link PieChart} from a set
	 * of XML attributes.
	 * 
	 * @param context
	 * @param attrs
	 *            An attribute set which can contain attributes from
	 *            {@link com.example.android.customandroid.view.Views.R.styleable.PieChart}
	 *            as well as attributes inherited from
	 *            {@link android.android.view.View.android.view.View}.
	 */
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// attrs contains the raw values for the XML attributes
		// that were specified in the layout, which don't include
		// attributes set by styles or themes, and which may have
		// unresolved references. Call obtainStyledAttributes()
		// to get the final values for each attribute.
		//
		// This call uses R.styleable.PieChart, which is an array of
		// the custom attributes that were declared in attrs.xml.
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.GraphView, 0, 0);

		try {
			// Retrieve the values from the TypedArray and store into
			// fields of this class.
			//
			// The R.styleable.PieChart_* constants represent the index for
			// each custom attribute in the R.styleable.PieChart array.
			setMinMax(a.getFloat(R.styleable.GraphView_max, 0),
					a.getFloat(R.styleable.GraphView_min, 100));
			unit=a.getString(R.styleable.GraphView_unit);
			value=a.getString(R.styleable.GraphView_value);
			//throw new RuntimeException("max:"+a.getFloat(R.styleable.GraphView_min, 100)+", unit:"+unit);
		} finally {
			// release the TypedArray so that it can be reused.
			a.recycle();
		}
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Do nothing. Do not call the superclass method--that would start a
		// layout pass
		// on this android.view.View's children. PieChart lays out its children
		// in onSizeChanged().
	}

	public static int MODE_HORIZONTAL = 0;
	public static int MODE_VERTICAL = 1;

	private int getTextHeight(Paint p, String text) {
		Rect bounds = new Rect();
		p.getTextBounds(text, 0, text.length(), bounds);
		return bounds.height();
	}

	private int getTextWidth(Paint p, String text) {
		Rect bounds = new Rect();
		p.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width();
	}

	private void drawRuler(Canvas grphcs, int x, int y, int mode, int min,
			int max, int rulerWidth, int rulerHeight, int stepCount) {
		// grphcs.setColor(Color.Black);
		int delta = max - min;
		if(delta==0){
			return;
		}
		if(stepCount>delta){
			stepCount=delta;
		}
		double step = (double) delta / (double) (stepCount);
		int fontHeight = getTextHeight(rulerPaint, "A");
		double val = min;

		
		int lineAndSpace = lineLength + lineSpace;

		if (mode == MODE_HORIZONTAL) {
			grphcs.drawLine(x + 0, y + 0, x + 0, y + lineLength, rulerPaint);
			grphcs.drawText("" + min, x + 0, y + lineAndSpace+ fontHeight, rulerPaint);

			grphcs.drawLine(x + rulerWidth - 1, y + 0, x + rulerWidth - 1, y
					+ lineLength, rulerPaint);
			grphcs.drawText("" + max,
					x + rulerWidth - getTextWidth(rulerPaint, "" + max), y
							+ lineAndSpace+ fontHeight, rulerPaint);
		}

		if (mode == MODE_VERTICAL) {
			grphcs.drawLine(x + rulerWidth - lineLength, y + rulerHeight - 1, x
					+ rulerWidth, y + rulerHeight - 1, rulerPaint);
			grphcs.drawText("" + min, x + rulerWidth - lineAndSpace
					- (getTextWidth(rulerPaint, ""+(int) min)), y
					+ rulerHeight- fontHeight + fontHeight, rulerPaint);

			grphcs.drawLine(x + rulerWidth - lineLength, y + 0, x + rulerWidth,
					y + 0, rulerPaint);
			grphcs.drawText("" + max, x + rulerWidth - lineAndSpace
					- (getTextWidth(rulerPaint, "" + (int) max)), y+ 0+ fontHeight/2,
					rulerPaint);
		}

		if (mode == MODE_HORIZONTAL) {
			val = min;
			for (int i = 1; val <= max - step; i++) {
				val += step;
				double left = val;
				left = left * rulerWidth;
				left = Math.ceil(left / (max - min));
				grphcs.drawLine(x + (int) left, y + 0, x + (int) left, y
						+ lineLength, rulerPaint);
				if(left>rulerWidth- (getTextWidth(rulerPaint, "" + (int) val) / 2)){
					continue;
				}
				grphcs.drawText(
						"" + (int) val,
						x
								+ (int) left
								- (getTextWidth(rulerPaint, "" + (int) val) / 2),
						y + lineAndSpace+ fontHeight, rulerPaint);
			}
		}
		if (mode == MODE_VERTICAL) {
			val = 0;
			double top = 0;
			while (true) {
				val += step;
				top = val;
				top = top * rulerHeight;
				top = Math.abs(top / delta);
				if (top >= rulerHeight - 20) {
					break;
				}
				grphcs.drawLine(x + rulerWidth - lineLength, y + (int) top, x
						+ rulerWidth, y + (int) top, rulerPaint);
				String text = "" + (int) (max - val);
				grphcs.drawText(text, x + rulerWidth - lineAndSpace
						- (getTextWidth(rulerPaint, text)), y + (int) top +fontHeight/4, rulerPaint);
			}
		}
	}

	private void initSize(){
		leftRulerWidth=DEF_LEFT_RULER_WIDTH*getHeight()/640;
		bottomRulerHeight=DEF_BOTTOM_RULER_HEIGHT*getHeight()/640;
		graphRight=DEF_GRAPH_RIGHT*getHeight()/640;
		graphTop=DEF_GRAPH_TOP*getHeight()/640;
		lineLength=DEF_LINE_LENGTH*getHeight()/640;
		lineSpace=DEF_LINE_SPACE*getHeight()/640;
	}
	
	@Override
	protected void onDraw(Canvas grphcs) {
		super.onDraw(grphcs);
		initSize();
		grphcs.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

		rulerPaint.setTextSize(30*getHeight()/640);
		
		
		if (img != null) {
			Paint fillPaint = new Paint();
			fillPaint.setStyle(Paint.Style.FILL);
			grphcs.drawBitmap(img, leftRulerWidth, graphTop, fillPaint);
		}
		drawRuler(grphcs,0,graphTop,MODE_VERTICAL,(int)valueMin,(int)valueMax,leftRulerWidth,getHeight()-bottomRulerHeight-graphTop,10);
		
		drawRuler(grphcs,leftRulerWidth,getHeight()-bottomRulerHeight,MODE_HORIZONTAL,(int)0,(int)this.values.size(),getWidth()-leftRulerWidth-graphRight,bottomRulerHeight,8);
		
		grphcs.drawText(unit, leftRulerWidth,30*getHeight()/640, rulerPaint);
		
		if (cursorX == -1) {
			return;
		}
		if ((cursorY < 10) && (cursorX > getWidth() - 10))
			return;

		int displayCarrier = cursorX * values.size() / getWidth();
		if (displayCarrier < 0) {
			displayCarrier = 0;
		}
		if (displayCarrier > values.size() - 1) {
			displayCarrier = values.size() - 1;
		}
		int displayY = 0;
		int maxDelta = (int) Math.abs(valueMax - valueMin);
		double valY = values.get(displayCarrier);
		valY = valY - valueMin;

		displayY = getHeight()
				- (int) Math.round(Math.abs(valY) * getHeight() / maxDelta);
		if (displayY == getHeight()) {
			displayY = getHeight() - 1;
		}
		grphcs.drawLine(cursorX, 0, cursorX, getHeight(), darkPaint);
		grphcs.drawLine(0, displayY, 10, displayY, darkPaint);

		double displayValue = values.get(displayCarrier);
		double displayFrequency = Main.carrierToFrequency(displayCarrier);
		displayFrequency = Math.round(displayFrequency * 100) / 100;
		// grphcs.setColor(Color.White);

		int dispRectX = 20;
		int dispRectW = 120;
		int dispRectH = 60;
		int dispRectY = 20;
		if (cursorX < getWidth() / 2) {
			dispRectX = getWidth() - dispRectW - 20;
		}

		grphcs.drawRect(dispRectX, dispRectY, dispRectW, dispRectH,
				whiteRectPaint);
		// grphcs.setColor(Color.Black);
		grphcs.drawRect(dispRectX, dispRectY, dispRectW, dispRectH, rectPaint);

		// grphcs.setFont(new Font("Sans serif",0,14));
		// displayLabel.setText(Main.view.language.carrier+":"+displayCarrier+" "+Main.view.language.frequency+":"+displayFrequency+"kHz "+Main.view.language.value+":"+displayValue);

		grphcs.drawText(getContext().getString(R.string.carrier) + ":"
				+ displayCarrier, dispRectX + 5, dispRectY + 5, rectPaint);
		String displayFrequencyStr = "" + displayFrequency;
		if (displayFrequencyStr.indexOf(".0") == displayFrequencyStr.length() - 2) {
			displayFrequencyStr = displayFrequencyStr.substring(0,
					displayFrequencyStr.length() - 2);
		}

		grphcs.drawText(getContext().getString(R.string.frequency) + ":"
				+ displayFrequency + "kHz", dispRectX + 5, dispRectY + 5 + 15,
				rectPaint);
		grphcs.drawText(getContext().getString(R.string.value) + ":"
				+ displayValue, dispRectX + 5, dispRectY + 5 + 30, rectPaint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		curW = w;
		curH = h;
		updateImage();
		invalidate();
	}

	private void setLayerToSW(android.view.View v) {
		if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
			setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	private void setLayerToHW(android.view.View v) {
		if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
			setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);
		}
	}
}