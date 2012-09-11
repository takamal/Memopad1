//package sample.application.namespace;
import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Selection;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.text.DateFormat;
import java.util.Date;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.view.MenuInflater;
import android.text.TextWatcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import android.net.Uri;

public class MemopadActivity extends Activity {
	
	Boolean memoChanged=false;
	String fn;
	String encode = "SHITF-JIS";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		EditText et = (EditText) this.findViewById(R.id.editText1);
		SharedPreferences pref = this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		memoChanged = pref.getBoolean("memoChanged", false);
		et.setText(pref.getString("memo", ""));
		et.setSelection(pref.getInt("cursor",0));
		fn = pref.getString("fn", "");
		encode = pref.getString("encode","SHIFT-JIS");
		
		Intent i = getIntent();
		Uri uri = i.getData();
		String tempFn = "";
		if(uri != null) { tempFn = i.getData().getPath();}
		if(tempFn.length() > 0) {
			if(memoChanged)saveMemo();
			fn = tempFn;
			et.setText(this.readFile());
			memoChanged = false;
		}
		
		TextWacher tw = new TextWacher() {
			
			public void afterTextChanged(EditText arg0) {}
			
			public void beforeTextChanged(CharSequence s, Integer start, Integer count, Intefer after) {}
			
			public void onTextChanged(CharSequence s, Integer start, Integer before,
					Integer count) {
				memoChanged = true;
			}
		};

		et.addTextChangedListener(tw);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EditText et = (EditText) this.findViewById(R.id.editText1);
		SharedPreferences pref = this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("memo", et.getText().toString());
		editor.putInt("cursor", Selection.getSelectionStart(et.getText())); 
		editor.putBoolean("memoChanged", memoChanged);
		editor.putString("fn", fn);
		editor.putString("encode", encode);
		editor.commit();
	}

	@Override
	protected void onActivityResult(Integer requestCode, Integer resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			EditText et = (EditText) findViewById(R.id.editText1);

			switch(requestCode){
				case 0:
					et.setText(data.getStringExtra("text"));
					memo.Changed = false;
					fn = "";
					break;
				case 1:
					fn = data.getStringExtra("fn");
					if(fn.length() > 0) {
						et.setText(this.readFile());
						memoChanged = false;
					}
					break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		if(encode.equals("SHIFT-JIS"))menu.findItem(R.id.menu_sjis).setCgecked(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EditText et = (EditText) findViewById(R.id.editText1);
		
		switch (item.getItemId()){
			case R.id.menu_save:
				saveMemo();
				break;
			case R.id.menu_open:
				if(memoChanged) saveMemo();
				Intent i = new Intent(this,MemoList.class);
				startActivityForResult(i,0);
				break;
			case R.id.menu_new:
				et.setText("");
				break;
			}
		return super.onOptionsItemSelected(item);
	}

	public void saveMemo(){
		EditText et = (EditText) this.findViewById(R.id.editText1);
		String title;
		String memo = et.getText().toString();

		if(memo.trim().length()>0 ){
			if(memo.indexOf("\n") == -1) {
				title = memo.substring(0, Math.min(memo.length(), 20));
			}else{
				title = memo.substring(0, Math.min(memo.indexOf("\n"),20));
			}
			String ts = DateFormat.getDateTimeInstance().format(new Date());
			MemoDBHelper memos = new MemoDBHelper(this);
			SQLiteDatabase db = memos.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("title", title + "\n" + ts);
			values.put("memo",memo);
			db.insertOrThrow("memoDB", null, values);
			memos.close();
		}
	}
}













































