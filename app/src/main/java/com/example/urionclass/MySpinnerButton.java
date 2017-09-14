package com.example.urionclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.urionbean.User;
import com.example.uriondb.DBOpenHelper;
import com.urionapp.bp.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MySpinnerButton extends android.widget.Button {

	private Context context;
	private DBOpenHelper dbOpenHelper;

	public MySpinnerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		// 设置监听事件
		setOnClickListener(new MySpinnerButtonOnClickListener());

	}

	public MySpinnerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// 设置监听事件
		setOnClickListener(new MySpinnerButtonOnClickListener());
	}

	public MySpinnerButton(Context context) {
		super(context);
		this.context = context;

		// 设置监听事件
		setOnClickListener(new MySpinnerButtonOnClickListener());
	}

	/**
	 * MySpinnerButton的点击事件
	 * 
	 * @author haozi
	 * 
	 */
	class MySpinnerButtonOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			final MySpinnerDropDownItems mSpinnerDropDrownItems = new MySpinnerDropDownItems(
					context);
			if (!mSpinnerDropDrownItems.isShowing()) {
				mSpinnerDropDrownItems.showAsDropDown(MySpinnerButton.this);
			}
		}
	}

	/**
	 * MySpinnerButton的下拉列表
	 * 
	 * @author haozi
	 * 
	 */

	class MySpinnerDropDownItems extends PopupWindow {

		private Context context;
		private LinearLayout mLayout; // 下拉列表的布局
		private ListView mListView; // 下拉列表控件
		private ArrayList<HashMap<String, String>> mData;

		public MySpinnerDropDownItems(Context context) {
			super(context);
			this.context = context;
			// 下拉列表的布局
			mLayout = new LinearLayout(context);
			mLayout.setOrientation(LinearLayout.VERTICAL);
			// mLayout.setBackgroundColor(R.color.red);
			// 下拉列表控件
			mListView = new ListView(context);
			dbOpenHelper = new DBOpenHelper(context);
			mListView.setLayoutParams(new LayoutParams(200, 300));
			mData = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> mHashmap1 = new HashMap<String, String>();
			mHashmap1.put("spinner_dropdown_item_textview", "User");
			mData.add(mHashmap1);
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query("user", new String[] { "name" }, null,
					null, null, null, null);
			while (cursor.moveToNext()) {
				HashMap<String, String> mHashmap = new HashMap<String, String>();
				String uname = cursor.getString(cursor.getColumnIndex("name"));

				mHashmap.put("spinner_dropdown_item_textview", uname);
				mData.add(mHashmap);
			}
			cursor.close(); // 游标使用完成后记得关闭
			db.close();

			// 为listView设置适配器
			mListView.setAdapter(new MyAdapter(context, mData,
					R.layout.spinner_dropdown_item,
					new String[] { "spinner_dropdown_item_textview" },
					new int[] { R.id.spinner_dropdown_item_textview }));
			// 设置listView的点击事件
			mListView
					.setOnItemClickListener(new MyListViewOnItemClickedListener());
			// 把下拉列表添加到layout中。
			mLayout.addView(mListView);

			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setContentView(mLayout);
			setFocusable(true);
			mLayout.setBackgroundColor(R.color.black);
			mLayout.setFocusableInTouchMode(true);
		}

		/**
		 * 我的适配器
		 * 
		 * @author haozi
		 * 
		 */
		public class MyAdapter extends BaseAdapter {

			private Context context;
			private List<? extends Map<String, ?>> mData;
			private int mResource;
			private String[] mFrom;
			private int[] mTo;
			private LayoutInflater mLayoutInflater;

			/**
			 * 我的适配器的构造方法
			 * 
			 * @param context
			 *            调用方的上下文
			 * @param data
			 *            数据
			 * @param resource
			 * @param from
			 * @param to
			 */
			public MyAdapter(Context context,
					List<? extends Map<String, ?>> data, int resource,
					String[] from, int[] to) {

				this.context = context;
				this.mData = data;
				this.mResource = resource;
				this.mFrom = from;
				this.mTo = to;
				this.mLayoutInflater = (LayoutInflater) context
						.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			}

			/**
			 * 系统在绘制ListView之前，将会先调用getCount方法来获取Item的个数
			 */
			public int getCount() {

				return this.mData.size();
			}

			public Object getItem(int position) {

				return this.mData.get(position);
			}

			public long getItemId(int position) {

				return position;
			}

			/**
			 * 每绘制一个 Item就会调用一次getView方法，
			 * 在此方法内就可以引用事先定义好的xml来确定显示的效果并返回一个View对象作为一个Item显示出来。 也
			 * 正是在这个过程中完成了适配器的主要转换功能，把数据和资源以开发者想要的效果显示出来。
			 * 也正是getView的重复调用，使得ListView的使用更 为简单和灵活。
			 * 这两个方法是自定ListView显示效果中最为重要的，同时只要重写好了就两个方法，ListView就能完全按开发者的要求显示。 而
			 * getItem和getItemId方法将会在调用ListView的响应方法的时候被调用到。
			 * 所以要保证ListView的各个方法有效的话，这两个方法也得重写。
			 */
			public View getView(int position, View contentView, ViewGroup parent) {

				contentView = this.mLayoutInflater.inflate(this.mResource,
						parent, false);
				// 设置contentView的内容和样式，这里重点是设置contentView中文字的大小
				contentView.setBackgroundColor(R.color.black);
				for (int index = 0; index < this.mTo.length; index++) {
					TextView textView = (TextView) contentView
							.findViewById(this.mTo[index]);
					textView.setText(this.mData.get(position)
							.get(this.mFrom[index]).toString());
					textView.setTextColor(Color.WHITE);

				}

				return contentView;
			}
		}

		/**
		 * listView的点击事件
		 * 
		 * @author haozi
		 * 
		 */
		class MyListViewOnItemClickedListener implements
				AdapterView.OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView mTextView = (TextView) view
						.findViewById(R.id.spinner_dropdown_item_textview);
				String content = mTextView.getText().toString();
				MySpinnerButton.this.setText(content);
				MySpinnerDropDownItems.this.dismiss();
			}
		}
	}
}
