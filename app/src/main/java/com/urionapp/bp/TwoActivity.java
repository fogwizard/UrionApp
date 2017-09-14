package com.urionapp.bp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.urionclass.First;
import com.example.uriondb.DBOpenHelper;
import com.urionapp.bp.R;

public class TwoActivity extends Activity implements OnClickListener {
	private ImageButton home, history, week, month, year;
	private Button user;
	private String ggname;
	private DBOpenHelper dbOpenHelper;
	private First first;
	private int tiao, tiaom, tiaoy;
	private double[] daysa, dayda, daypa, monthsa, monthda, monthpa, yeasa,
			yeada, yeapa, times;
	private XYMultipleSeriesDataset ds, dm, dy, dataset, datasetm, datasety;
	private XYMultipleSeriesRenderer render, renderer;
	private TimeSeries series;
	private XYSeries seriess;
	private CategorySeries barscat;
	private GraphicalView gv, dv;
	private boolean a = true, b = false, c = true;
	LinearLayout layout, dianchart;
	List<Date[]> datess = new ArrayList<Date[]>();
	List<Date[]> datesm = new ArrayList<Date[]>();
	List<Date[]> datesy = new ArrayList<Date[]>();
	Date[] de, dem, dey;
	private int panduan = 0;
	List<double[]> values = new ArrayList<double[]>();
	private XYSeriesRenderer xyRender;
	private String[] titles = new String[] { "SYS(mmHg)", "DIA(mmHg)",
			"PUL(/min)" };
	private int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE };

	int[] colorsb = new int[] { Color.argb(255, 231, 0, 18),
			Color.argb(255, 236, 98, 0), Color.argb(255, 243, 152, 1),
			Color.argb(255, 255, 241, 1), Color.argb(255, 143, 195, 32),
			Color.argb(255, 10, 124, 37) };
	String[] types = new String[] { BarChart.TYPE, BarChart.TYPE,
			BarChart.TYPE, BarChart.TYPE, BarChart.TYPE, BarChart.TYPE,
			ScatterChart.TYPE };
	double[] aaa = new double[200];
	double[] bbb = new double[200];
	double[] ccc = new double[200];
	double[] ddd = new double[200];
	double[] eee = new double[200];
	double[] fff = new double[200];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout2);
		home = (ImageButton) this.findViewById(R.id.home);
		user = (Button) this.findViewById(R.id.user);
		history = (ImageButton) this.findViewById(R.id.history1);
		week = (ImageButton) this.findViewById(R.id.week);
		month = (ImageButton) this.findViewById(R.id.month);
		year = (ImageButton) this.findViewById(R.id.year);
		ggname = getIntent().getExtras().getString("gname");
		layout = (LinearLayout) findViewById(R.id.chart);
		dianchart = (LinearLayout) findViewById(R.id.dian);
		user.setText(ggname);
		history.setOnClickListener(this);
		home.setOnClickListener(this);
		week.setOnClickListener(this);
		month.setOnClickListener(this);
		year.setOnClickListener(this);
		first = new First();
		threeselect();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.history1:
			Intent serverIntent = new Intent(TwoActivity.this,
					ThreadActivity.class);
			serverIntent.putExtra("gname", ggname);
			startActivity(serverIntent);
			break;
		case R.id.home:
			Intent mainone = new Intent(TwoActivity.this, MainActivity.class);
			mainone.putExtra("gname", ggname);
			startActivity(mainone);
			break;
		case R.id.week:

			if (a) {
				panduan = 1;
				week.setImageResource(R.drawable.week31);
				month.setImageResource(R.drawable.month31);
				year.setImageResource(R.drawable.year3);
				twoselect();
				a = false;
				b = true;
				c = true;

			} else {
				week.setImageResource(R.drawable.week3);
				month.setImageResource(R.drawable.month3);
				year.setImageResource(R.drawable.year31);
				a = true;
				b = false;
				c = false;

			}

			break;
		case R.id.month:
			if (b) {
				panduan = 0;
				week.setImageResource(R.drawable.week3);
				month.setImageResource(R.drawable.month3);
				year.setImageResource(R.drawable.year3);
				threeselect();
				b = false;
				a = true;
				c = true;
			} else {
				week.setImageResource(R.drawable.week31);
				month.setImageResource(R.drawable.month31);
				year.setImageResource(R.drawable.year31);
				b = true;
				a = false;
				c = false;
			}

			break;
		case R.id.year:
			if (c) {
				panduan = 2;
				week.setImageResource(R.drawable.week3);
				month.setImageResource(R.drawable.month31);
				year.setImageResource(R.drawable.year31);
				oneselect();
				c = false;
				b = true;
				a = true;

			} else {
				week.setImageResource(R.drawable.week31);
				month.setImageResource(R.drawable.month31);
				year.setImageResource(R.drawable.year3);
				c = true;
				b = false;
				a = false;
			}

			break;
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;

		}

		return super.dispatchKeyEvent(event);
	}

	// 判断相隔天数

	// 判断月份
	@SuppressWarnings("unchecked")
	public void twoselect() {

		dbOpenHelper = new DBOpenHelper(TwoActivity.this);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		ArrayList<Object> arrList = new ArrayList<Object>();
		ArrayList<Object> al = new ArrayList<Object>();
		ArrayList sysList = new ArrayList();
		ArrayList diaList = new ArrayList();
		ArrayList pulList = new ArrayList();
		Cursor cursor = db.query("sdp", new String[] { "time,sys,dia,pul" },
				"name=?", new String[] { ggname }, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			int sys = cursor.getInt(cursor.getColumnIndex("sys"));
			int dia = cursor.getInt(cursor.getColumnIndex("dia"));
			int pul = cursor.getInt(cursor.getColumnIndex("pul"));
			String add = time.substring(0, 10);
			arrList.add(add);
			sysList.add(sys);
			diaList.add(dia);
			pulList.add(pul);

		}
		cursor.close();
		db.close();
		tiao = quChong(arrList).size();
		if (tiao == 0) {
			tiao = 1;
			de = new Date[tiao];
			daypa = new double[1];
			daysa = new double[1];
			dayda = new double[1];
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = sDateFormat.format(new java.util.Date());
			arrList.add(date);
			dayda[0] = 1000;
			daysa[0] = 1000;
			daypa[0] = 1000;
			sysList.add(1000);
			diaList.add(1000);
			pulList.add(1000);
		}
		daypa = new double[tiao];
		daysa = new double[tiao];
		dayda = new double[tiao];
		System.out.println(quChong(arrList));

		Map<Object, Integer> map = new HashMap<Object, Integer>();
		for (Object o : arrList) {
			map.put(o, map.get(o) == null ? 1 : map.get(o) + 1);
			System.out.println(o + "oooooooooooo");
		}
		int index = 0;
		int s = 0;
		int arr[] = new int[tiao];
		Object[] tee;
		de = new Date[tiao];
		for (Object i : map.keySet()) {
			System.out.println(i + "----->" + map.get(i));
			index = map.get(i);
			tee = quChong(arrList).toArray();
			for (int j = 0; j < tiao; j++) {
				if (i.equals(tee[j])) {
					arr[j] = index;
				}
			}
			String te = tee[s].toString();
			String tes = te.replaceAll("-", "");
			String end = tes.substring(0, 8);
			double a = Double.parseDouble(end.substring(0, 4));
			int aa = (int) a;
			double b = Double.parseDouble(end.substring(4, 6));
			int bb = (int) b;
			double c = Double.parseDouble(end.substring(6, 8));
			int cc = (int) c;
			de[s] = new Date(aa - 1900, bb - 1, cc);
			s++;

		}
		datess.add(de);

		int sa = 0, da = 0, pa = 0;
		List sal, dal, pal;

		for (int i = 0; i < arr.length; i++) {
			int a = arr.length;
			if (a == 0) {
				sal = sysList.subList(0, 1);
				dal = diaList.subList(0, 1);
				pal = pulList.subList(0, 1);
				sa = first.getAverage(sal);
				da = first.getAverage(dal);
				pa = first.getAverage(pal);
				daysa[i] = sa;
				dayda[i] = da;
				daypa[i] = pa;
			} else {
				int le = 0;
				for (int k = 0; k < i; k++) {
					le += arr[k];
				}
				sal = sysList.subList(le, le + arr[i]);
				dal = diaList.subList(le, le + arr[i]);
				pal = pulList.subList(le, le + arr[i]);
				sa = first.getAverage(sal) / arr[i];
				da = first.getAverage(dal) / arr[i];
				pa = first.getAverage(pal) / arr[i];
				daysa[i] = sa;
				dayda[i] = da;
				daypa[i] = pa;
			}

		}
		if (ds == null)
			getDataset();
		if (render == null)
			getRenderer();

		if (gv == null) {
			gv = ChartFactory
					.getTimeChartView(this, ds, render, " dd-MMM-yyyy");
			layout.addView(gv);

		} else {
			layout.removeAllViews(); // 先remove再add可以实现统计图更新
			gv = ChartFactory
					.getTimeChartView(this, ds, render, " dd-MMM-yyyy");
			layout.addView(gv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
		if (dataset == null)
			getDemoDataset();
		if (renderer == null)
			getDemoRenderer();

		if (dv == null) {
			dv = ChartFactory.getCombinedXYChartView(this, dataset, renderer,
					types);
			dianchart.addView(dv);

		} else {
			getDemoDataset();
			// 绘制图形
			dianchart.removeAllViews(); // 先remove再add可以实现统计图更新
			dv = ChartFactory.getCombinedXYChartView(this, dataset, renderer,
					types);
			dianchart.addView(dv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
	}

	public static List quChong(List list) {
		List newlist = new ArrayList();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();// 定义一个Object变量去接收
			if (!newlist.contains(obj)) {// 判断是否包含
				newlist.add(obj);
			}
		}
		return newlist;
	}

	@SuppressWarnings("unchecked")
	public void threeselect() {
		dbOpenHelper = new DBOpenHelper(TwoActivity.this);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		ArrayList arrList = new ArrayList();
		ArrayList sysList = new ArrayList();
		ArrayList diaList = new ArrayList();
		ArrayList pulList = new ArrayList();
		Cursor cursor = db.query("sdp", new String[] { "time,sys,dia,pul" },
				"name=?", new String[] { ggname }, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			int sys = cursor.getInt(cursor.getColumnIndex("sys"));
			int dia = cursor.getInt(cursor.getColumnIndex("dia"));
			int pul = cursor.getInt(cursor.getColumnIndex("pul"));
			String add = time.substring(0, 7);
			arrList.add(add);
			sysList.add(sys);
			diaList.add(dia);
			pulList.add(pul);

		}
		cursor.close();
		db.close();
		tiaom = quChong(arrList).size();
		if (tiaom == 0) {
			tiaom = 1;
			dem = new Date[tiaom];
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = sDateFormat.format(new java.util.Date());
			arrList.add(date);
			monthpa = new double[tiaom];
			monthda = new double[tiaom];
			monthsa = new double[tiaom];
			monthpa[0] = 1000;
			monthda[0] = 1000;
			monthsa[0] = 1000;
			sysList.add(1000);
			diaList.add(1000);
			pulList.add(1000);
		}
		dem = new Date[tiaom];
		monthpa = new double[tiaom];
		monthda = new double[tiaom];
		monthsa = new double[tiaom];
		System.out.println(quChong(arrList));
		Map<Object, Integer> map = new HashMap<Object, Integer>();
		for (Object o : arrList) {
			map.put(o, map.get(o) == null ? 1 : map.get(o) + 1);
		}
		int index = 0;
		int s = 0;
		Object[] tee;
		int arr[] = new int[tiaom];
		for (Object i : map.keySet()) {
			System.out.println(i + "----->" + map.get(i));
			tee = quChong(arrList).toArray();
			index = map.get(i);
			for (int j = 0; j < tiaom; j++) {
				if (i.equals(tee[j])) {
					arr[j] = index;
				}
			}

			String te = tee[s].toString();
			String tes = te.replaceAll("-", "");
			String end = tes.substring(0);
			double a = Double.parseDouble(end.substring(0, 4));
			int aa = (int) a;
			double b = Double.parseDouble(end.substring(5, 6));
			int bb = (int) b;
			dem[s] = new Date(aa - 1900, bb, 0);
			s++;
		}
		datesm.add(dem);

		int sa = 0, da = 0, pa = 0;
		List<Integer> sal, dal, pal;

		for (int i = 0; i < arr.length; i++) {
			int a = arr.length;

			if (a == 0) {

				sal = sysList.subList(0, 1);
				dal = diaList.subList(0, 1);
				pal = pulList.subList(0, 1);
				sa = first.getAverage(sal);
				da = first.getAverage(dal);
				pa = first.getAverage(pal);
				monthsa[i] = sa;
				monthda[i] = da;
				monthpa[i] = pa;
			} else {
				int le = 0;
				for (int k = 0; k < i; k++) {
					le += arr[k];
				}
				sal = sysList.subList(le, le + arr[i]);
				dal = diaList.subList(le, le + arr[i]);
				pal = pulList.subList(le, le + arr[i]);
				sa = first.getAverage(sal) / arr[i];
				da = first.getAverage(dal) / arr[i];
				pa = first.getAverage(pal) / arr[i];
				System.out.println("aa........." + sa);
				System.out.println("bb........." + da);
				System.out.println("cc........." + pa);
				monthsa[i] = sa;
				monthda[i] = da;
				monthpa[i] = pa;
			}

		}
		if (dm == null)
			getDatasetm();
		if (render == null)
			getRenderer();

		if (gv == null) {
			gv = ChartFactory.getTimeChartView(this, dm, render, " MMM-yyyy");
			layout.addView(gv);

		} else {

			// 绘制图形
			layout.removeAllViews(); // 先remove再add可以实现统计图更新
			gv = ChartFactory.getTimeChartView(this, dm, render, "MMM-yyyy");
			layout.addView(gv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}

		if (datasetm == null)
			getDemoDatasetm();
		if (renderer == null)
			getDemoRenderer();

		if (dv == null) {
			dv = ChartFactory.getCombinedXYChartView(this, datasetm, renderer,
					types);
			dianchart.addView(dv);

		} else {
			getDemoDatasetm();
			// 绘制图形
			dianchart.removeAllViews(); // 先remove再add可以实现统计图更新
			dv = ChartFactory.getCombinedXYChartView(this, datasetm, renderer,
					types);
			dianchart.addView(dv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
	}

	public void oneselect() {

		dbOpenHelper = new DBOpenHelper(TwoActivity.this);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		ArrayList arrList = new ArrayList();
		ArrayList sysList = new ArrayList();
		ArrayList diaList = new ArrayList();
		ArrayList pulList = new ArrayList();
		Cursor cursor = db.query("sdp", new String[] { "time,sys,dia,pul" },
				"name=?", new String[] { ggname }, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			int sys = cursor.getInt(cursor.getColumnIndex("sys"));
			int dia = cursor.getInt(cursor.getColumnIndex("dia"));
			int pul = cursor.getInt(cursor.getColumnIndex("pul"));
			String add = time.substring(0, 4);
			arrList.add(add);
			sysList.add(sys);
			diaList.add(dia);
			pulList.add(pul);

		}
		cursor.close();
		db.close();
		tiaoy = quChong(arrList).size();
		if (tiaoy == 0) {
			tiaoy = 1;
			dey = new Date[tiaoy];
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = sDateFormat.format(new java.util.Date());
			arrList.add(date);
			yeapa = new double[tiaoy];
			yeasa = new double[tiaoy];
			yeada = new double[tiaoy];
			yeapa[0] = 1000;
			yeasa[0] = 1000;
			yeada[0] = 1000;
			sysList.add(1000);
			diaList.add(1000);
			pulList.add(1000);
		}
		dey = new Date[tiaoy];
		yeapa = new double[tiaoy];
		yeasa = new double[tiaoy];
		yeada = new double[tiaoy];
		System.out.println(quChong(arrList));
		Map<Object, Integer> map = new HashMap<Object, Integer>();
		for (Object o : arrList) {
			map.put(o, map.get(o) == null ? 1 : map.get(o) + 1);
		}
		int index = 0;
		int s = 0;
		Object[] tee;
		int arr[] = new int[tiaoy];
		for (Object i : map.keySet()) {
			System.out.println(i + "----->" + map.get(i));
			tee = quChong(arrList).toArray();
			index = map.get(i);
			for (int j = 0; j < tiaoy; j++) {
				if (i.equals(tee[j])) {
					arr[j] = index;
				}
			}

			String te = tee[s].toString();
			String tes = te.replace("-", "");
			String end = tes.substring(0, 4);
			double a = Double.parseDouble(end);
			int aa = (int) a;

			dey[s] = new Date(aa - 1899, 0, 0);
			s++;
		}
		datesy.add(dey);

		int sa = 0, da = 0, pa = 0;
		List sal, dal, pal;

		for (int i = 0; i < arr.length; i++) {
			int a = arr.length;

			if (a == 0) {

				sal = sysList.subList(0, 1);
				dal = diaList.subList(0, 1);
				pal = pulList.subList(0, 1);
				sa = first.getAverage(sal);
				da = first.getAverage(dal);
				pa = first.getAverage(pal);
				yeasa[i] = sa;
				yeada[i] = da;
				yeapa[i] = pa;
				System.out.println("aa........." + sa);
				System.out.println("bb........." + da);
				System.out.println("cc........." + pa);
			} else {
				int le = 0;
				for (int k = 0; k < i; k++) {
					le += arr[k];
				}
				sal = sysList.subList(le, le + arr[i]);
				dal = diaList.subList(le, le + arr[i]);
				pal = pulList.subList(le, le + arr[i]);
				System.out.println(arr[i] + "////////");
				System.out.println("arr[i]........."
						+ sysList.subList(le, le + arr[i]));
				sa = first.getAverage(sal) / arr[i];
				da = first.getAverage(dal) / arr[i];
				pa = first.getAverage(pal) / arr[i];
				yeasa[i] = sa;
				yeada[i] = da;
				yeapa[i] = pa;
				System.out.println("aa........." + sa);
				System.out.println("bb........." + da);
				System.out.println("cc........." + pa);
			}

		}
		if (dy == null)
			getDatasety();
		if (render == null)
			getRenderer();
		if (gv == null) {
			gv = ChartFactory.getTimeChartView(this, dy, render, "yyyy");
			layout.addView(gv);
		} else {
			// 绘制图形
			layout.removeAllViews(); // 先remove再add可以实现统计图更新
			gv = ChartFactory.getTimeChartView(this, dy, render, "yyyy");
			layout.addView(gv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}
		if (datasety == null)
			getDemoDatasety();
		if (renderer == null)
			getDemoRenderer();

		if (dv == null) {
			dv = ChartFactory.getCombinedXYChartView(this, datasety, renderer,
					types);
			dianchart.addView(dv);

		} else {
			getDemoDatasety();
			// 绘制图形
			dianchart.removeAllViews(); // 先remove再add可以实现统计图更新
			dv = ChartFactory.getCombinedXYChartView(this, datasety, renderer,
					types);
			dianchart.addView(dv, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}

	}

	public XYMultipleSeriesDataset beijing() {
		datasetm = new XYMultipleSeriesDataset();
		datasety = new XYMultipleSeriesDataset();
		dataset = new XYMultipleSeriesDataset();
		int t = 0;
		if (t >= 0 && t < 120) {
			for (t = 0; t < 120; t++) {
				aaa[t] = 120;
				bbb[t] = 110;
				ccc[t] = 100;
				ddd[t] = 90;
				eee[t] = 85;
				fff[t] = 80;
			}
		}
		t = 120;
		if (t >= 120 && t < 130) {
			for (t = 120; t < 130; t++) {
				aaa[t] = 120;
				bbb[t] = 110;
				ccc[t] = 100;
				ddd[t] = 90;
				eee[t] = 85;
				fff[t] = 0;
			}
		}
		t = 130;
		if (t >= 130 && t < 140) {
			for (t = 130; t < 140; t++) {
				aaa[t] = 120;
				bbb[t] = 110;
				ccc[t] = 100;
				ddd[t] = 90;
				eee[t] = 0;
				fff[t] = 0;
			}
		}
		t = 140;
		if (t >= 140 && t < 160) {
			for (t = 140; t < 160; t++) {
				aaa[t] = 120;
				bbb[t] = 110;
				ccc[t] = 100;
				ddd[t] = 0;
				eee[t] = 0;
				fff[t] = 0;
			}
		}
		t = 160;
		if (t >= 160 && t < 180) {
			for (t = 160; t < 180; t++) {
				aaa[t] = 120;
				bbb[t] = 110;
				ccc[t] = 0;
				ddd[t] = 0;
				eee[t] = 0;
				fff[t] = 0;
			}
		}
		t = 180;
		if (t >= 180 && t < 200) {
			for (t = 180; t < 200; t++) {
				aaa[t] = 120;
				bbb[t] = 0;
				ccc[t] = 0;
				ddd[t] = 0;
				eee[t] = 0;
				fff[t] = 0;
			}
		}

		String[] titles = new String[] { "Severe hypertension",
				"Moderate hypertension", " Mild hypertension",
				"High normal value", "Normal blood pressure",
				"Optimal blood pressure" };
		List<double[]> values = new ArrayList<double[]>();
		values.add(aaa);
		values.add(bbb);
		values.add(ccc);
		values.add(ddd);
		values.add(eee);
		values.add(fff);

		int length = titles.length;
		for (int i = 0; i < length; i++) {
			barscat = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				barscat.add(v[k]);
			}
			datasety.addSeries(barscat.toXYSeries());
			dataset.addSeries(barscat.toXYSeries());
			datasetm.addSeries(barscat.toXYSeries());
		}
		if (panduan == 0) {
			return datasetm;
		} else if (panduan == 1) {
			return dataset;
		} else {
			return datasety;
		}

	}

	protected void onRestoreInstanceState(Bundle savedState) {
		Log.i("onRestoreInstanceState", "onRestoreInstanceState");

		super.onRestoreInstanceState(savedState);

		ds = (XYMultipleSeriesDataset) savedState.getParcelable("dataset");
		dm = (XYMultipleSeriesDataset) savedState.getSerializable("datasetm");
		dy = (XYMultipleSeriesDataset) savedState.getSerializable("datasety");
		dataset = (XYMultipleSeriesDataset) savedState
				.getSerializable("datasett");
		datasetm = (XYMultipleSeriesDataset) savedState
				.getSerializable("datasettm");
		datasety = (XYMultipleSeriesDataset) savedState
				.getSerializable("datasetty");
		render = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderer");
		renderer = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderert");
		series = (TimeSeries) savedState.getSerializable("current_series");
		seriess = (XYSeries) savedState.getSerializable("current_seriess");
		xyRender = (XYSeriesRenderer) savedState
				.getSerializable("current_renderer");
	}

	// 在onResume方法中，我们调用getDataset方法构造了折线图的点数据，用getRenderer方法构造了折线图的Renderer，然后用ChartFactory.getLineChartView
	// 构造了一个GraphicalView，最后把这个GraphicalView 加到id为chart的LinearLayout中。于是在
	// Activity 上显示了折线图。
	@Override
	protected void onResume() {

		Log.i("onResume", "onResume");

		super.onResume();

	}

	// onSaveInstanceState
	// 方法在挂起时被调用，我们在这里使用序列化方法（putSerializable方法）把Activity的成员变量储存到Bundle。
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("onSaveInstanceState", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		// outState.putParcelable("dataset", ds);
		outState.putSerializable("dataset", ds);
		outState.putSerializable("datasetm", dm);
		outState.putSerializable("datasety", dy);
		outState.putSerializable("datasett", dataset);
		outState.putSerializable("datasettm", datasetm);
		outState.putSerializable("datasetty", datasety);

		outState.putSerializable("renderer", render);
		outState.putSerializable("renderert", renderer);
		outState.putSerializable("current_series", series);
		outState.putSerializable("current_seriess", seriess);
		outState.putSerializable("current_renderer", xyRender);
	}

	private XYMultipleSeriesDataset getDatasetm() {
		dm = new XYMultipleSeriesDataset();
		Date[] xV = datesm.get(0);
		for (int i = 0; i < titles.length; i++) {
			// 新建一个系列（线条）
			series = new TimeSeries(titles[i]);
			int seriesLength = xV.length;
			int a = seriesLength - 4;
			if (seriesLength > 3) {
				render.setXAxisMax(xV[seriesLength - a].getTime());
			}
			switch (i) {
			case 0:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), monthsa[k]);
					System.out.println(monthsa[k] + "sasasasas");
				}
				dm.addSeries(series);
				break;
			case 1:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), monthda[k]);

				}
				dm.addSeries(series);
				break;
			case 2:
				for (int k = 0; k < seriesLength; k++) {
					series.add(xV[k].getTime(), monthpa[k]);

				}
				dm.addSeries(series);
				break;

			default:
				series.add(0, 0);
				dm.addSeries(series);
				break;
			}

		}

		return dm;

	}

	private XYMultipleSeriesDataset getDatasety() {
		dy = new XYMultipleSeriesDataset();
		Date[] xV = datesy.get(0);
		for (int i = 0; i < titles.length; i++) {
			// 新建一个系列（线条）
			series = new TimeSeries(titles[i]);
			int seriesLength = xV.length;
			int a = seriesLength - 4;
			if (seriesLength > 3) {
				render.setXAxisMax(xV[seriesLength - a].getTime());
			}
			switch (i) {
			case 0:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), yeasa[k]);

				}
				dy.addSeries(series);
				break;
			case 1:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), yeada[k]);
				}
				dy.addSeries(series);
				break;
			case 2:
				for (int k = 0; k < seriesLength; k++) {
					series.add(xV[k].getTime(), yeapa[k]);

				}
				dy.addSeries(series);
				break;

			default:
				series.add(0, 0);
				dy.addSeries(series);
				break;
			}

		}

		return dy;

	}

	private XYMultipleSeriesDataset getDataset() {
		ds = new XYMultipleSeriesDataset();
		Date[] xV = datess.get(0);
		for (int i = 0; i < titles.length; i++) {
			// 新建一个系列（线条）
			series = new TimeSeries(titles[i]);
			int seriesLength = xV.length;
			int a = seriesLength - 4;
			if (seriesLength > 3) {
				render.setXAxisMax(xV[seriesLength - a].getTime());
			}
			switch (i) {
			case 0:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), daysa[k]);
					System.out.println(xV[k] + "getTime().....");
					System.out.println(daysa[k] + "sasasasas");
				}
				ds.addSeries(series);
				break;
			case 1:
				for (int k = 0; k < seriesLength; k++) {

					series.add(xV[k].getTime(), dayda[k]);
					System.out.println(xV[k] + "FFFFFFFF" + dayda[k]);
				}
				ds.addSeries(series);
				break;
			case 2:
				for (int k = 0; k < seriesLength; k++) {
					series.add(xV[k].getTime(), daypa[k]);

				}
				ds.addSeries(series);
				break;

			default:
				series.add(0, 0);
				ds.addSeries(series);
				break;
			}

		}
		return ds;

	}

	public XYMultipleSeriesRenderer getRenderer() {
		render = new XYMultipleSeriesRenderer();
		render.setPanEnabled(true, false);
		render.setAxisTitleTextSize(20); // 设置坐标轴标题文本大小
		render.setChartTitleTextSize(20); // 设置图表标题文本大小
		// render.setChartTitle("Measurement of blood pressure data values");
		render.setLabelsTextSize(15); // 设置轴标签文本大小
		render.setLegendTextSize(15); // 设置图例文本大小
		render.setMargins(new int[] { 0, 30, 5, 30 }); // 设置4边留白
		render.setPanEnabled(true, false); // 设置x,y坐标轴不会因用户划动屏幕而移动
		render.setMarginsColor(Color.argb(0, 0xff, 0, 0));// 设置4边留白透明
		render.setBackgroundColor(Color.TRANSPARENT); // 设置背景色透明
		render.setApplyBackgroundColor(true); // 使背景色生效
		render.setXLabels(6);// 设置X轴显示12个点，根据setChartSettings的最大值和最小值自动计算点的间隔
		render.setYLabels(12);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		render.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
		render.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之间的相对位置关系
		// render.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
		render.setShowGrid(true);// 是否显示网格
		render.setGridColor(R.color.black);// 设置网格颜色
		render.setAxesColor(R.color.black);// 设置X.y轴颜色
		render.setXLabelsColor(Color.BLACK);
		render.setYLabelsColor(0, Color.BLACK);
		render.setLabelsColor(Color.BLACK);
		// render.setYLabelsColor(scale, color)(R.color.black);
		render.setFitLegend(true);// 设置自动按比例缩放
		render.setYAxisMax(200.0); // 设置Y轴最大值
		render.setYAxisMin(40.0); // 设置Y轴最小值

		// 设置x,y轴上的刻度的颜色
		render.setLabelsColor(R.color.black);
		for (int i = 0; i < titles.length; i++) {
			xyRender = new XYSeriesRenderer();
			xyRender.setPointStyle(PointStyle.CIRCLE);
			xyRender.setColor(colors[i]);// 设置线图颜色
			xyRender.setFillPoints(true);// 设置为实心点
			render.addSeriesRenderer(xyRender);// 添加到render中
		}
		return render;

	}

	private XYMultipleSeriesRenderer getDemoRenderer() {
		renderer = new XYMultipleSeriesRenderer();
		int length = colorsb.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer a = new XYSeriesRenderer();
			a.setColor(colorsb[i]);
			renderer.addSeriesRenderer(a);
		}
		XYSeriesRenderer r = new XYSeriesRenderer();
		// XYSeriesRenderer renderer = new XYSeriesRenderer();
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.BLUE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setPanEnabled(false, false);
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);

		renderer.setYAxisMax(120.0); // 设置Y轴最大值
		renderer.setYAxisMin(0.0); // 设置Y轴最小值
		renderer.setXAxisMax(200.0); // 设置Y轴最大值
		renderer.setXAxisMin(0.0); // 设置Y轴最小值
		renderer.setAxisTitleTextSize(15);
		renderer.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之间的相对位置关系
		renderer.setBackgroundColor(Color.TRANSPARENT); // 设置背景色透明
		renderer.setPointSize(5f);
		// render.setPanEnabled(false, true); // 设置x,y坐标轴不会因用户划动屏幕而移动
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));// 设置4边留白透明
		renderer.setMargins(new int[] { 12, 40, 10, 30 });
		renderer.setAxesColor(Color.BLACK);// 设置X.y轴颜色
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setFitLegend(true);// 设置自动按比例缩
		// renderer.setZoomEnabled(false);
		renderer.setZoomRate(1.1f);
		renderer.setXLabels(10);
		renderer.setYLabels(10);
		renderer.setXTitle("Systolic blood pressure(mmHg)");

		renderer.setYTitle("               Diastolic blood pressure(mmHg)");

		renderer.setAxesColor(Color.BLACK);

		return renderer;
	}

	private XYMultipleSeriesDataset getDemoDataset() {

		beijing();
		seriess = new XYSeries("DIA/SYS");
		for (int i = 0; i < tiao; i++) {
			seriess.add(daysa[i], daypa[i]);
			System.out.println(daysa[i] + "hhhhhhhhhhhhh" + dayda[i]);
		}
		dataset.addSeries(seriess);

		return dataset;
	}

	private XYMultipleSeriesDataset getDemoDatasety() {
		System.out.println("bbbbbbbbbbbbbb");
		beijing();
		seriess = new XYSeries("DIA/SYS");
		for (int i = 0; i < tiaoy; i++) {
			seriess.add(yeasa[i], yeada[i]);
		}
		datasety.addSeries(seriess);
		System.out.println(datasety.getSeriesCount()
				+ "datasetdatasetdatasetdataset");

		return datasety;
	}

	private XYMultipleSeriesDataset getDemoDatasetm() {
		beijing();
		seriess = new XYSeries("DIA/SYS");
		for (int i = 0; i < tiaom; i++) {
			seriess.add(monthsa[i], monthda[i]);
			System.out.println(monthsa[i] + "hhhhhhhhhhmmmhhh" + monthda[i]);
		}
		datasetm.addSeries(seriess);
		return datasetm;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
