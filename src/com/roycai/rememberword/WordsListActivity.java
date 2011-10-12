package com.roycai.rememberword;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import static  com.roycai.rememberword.WordDbManager.*;

public class WordsListActivity extends TabActivity implements TabHost.TabContentFactory {
	
	private static final String TAG = "WordsListActivity";
	private static final int DELETE_ID = Menu.FIRST;
	private static final int SHOW_ITEM_ID = Menu.FIRST + 1;
	
	private static final int SHOW_ALL = Menu.FIRST + 2;
	private static final int SHOW_IMPORTANCE = Menu.FIRST + 3;
	private static final int SHOW_EXCEPT_UNIMPORTANCE = Menu.FIRST + 4;

	private Cursor mCursor;
	private WordDbManager mWordDbManager;
	private long wordBookId = 0L;
	
	private String sqlImportanceShowWay = null;
	private String sqlRememberStatusShowWay = null;
	private boolean isShowExplain= false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = this.getIntent();
		wordBookId = intent.getLongExtra("wordBookId", 0);

		mWordDbManager = new WordDbManager(this);
		mWordDbManager.open();
		
//		PullWordHandler pullHandler = new PullWordHandler();
//		List<Word> wordList = pullHandler.parse(readEarthquakeDataFromFile());
//		for (Word word : wordList) {
//			mWordDbManager.addWord(word.getWord(),word.getExplain(), word.getYinBiao(), wordBookId);
//		}
//for (int i = 0; i < 3000; i++) {
//		mWordDbManager.addWord("benefit" + i, "n. ���棬�ô����ȼý� vt. �����ڣ������� vi. ���棬���� Benefit: ������ | ���� | ����", "'benifit", wordBookId);
//			
//		}
//for (int i = 0; i < 10; i++) {
//	mWordDbManager.addWord("guardian", "n. [��] �໤�ˣ������ˣ��ػ��� adj. �ػ���", "'benifit", wordBookId);
//	
//}
//for (int i = 0; i < 10; i++) {
//		mWordDbManager.addWord("shadow", "n. ��Ӱ��Ӱ�ӣ����飻�ӻ������δ� vt. �ڱΣ�ʹ���ʣ�β�棻Ԥʾ vi. ���䣻������ adj. Ӱ���ڸ��", "'benifit", wordBookId);
//	
//}
//for (int i = 0; i < 10; i++) {
//		mWordDbManager.addWord("past", "n. ��ȥ������ adj. ��ȥ�ģ������� prep. Խ�������� adv. ��������", "'benifit", wordBookId);
//}
//for (int i = 0; i < 10; i++) {
//		mWordDbManager.addWord("deny", "vt. �񶨣����ϣ��ܾ����裻�ܾ�����Ҫ�� vi. ���ϣ��ܾ�", "'benifit", wordBookId);
//}
//for (int i = 0; i < 10; i++) {
//		mWordDbManager.addWord("apart", "adv. ��ࣻ���ڲ�ͬ�أ������� adj. ����ģ����ڲ�ͬ��", "'benifit", wordBookId);
//}	

		String allTxt = getString(R.string.txt_all);
		String hadRememberedTxt = getString(R.string.txt_had_remembered);
		String rememberingTxt = getString(R.string.txt_remembering);
		String notRememberTxt = getString(R.string.txt_not_remember);
		final TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(allTxt).setIndicator(allTxt)
				.setContent(this));
		tabHost.addTab(tabHost.newTabSpec(rememberingTxt).setIndicator(rememberingTxt)
				.setContent(this));
		tabHost.addTab(tabHost.newTabSpec(notRememberTxt).setIndicator(notRememberTxt)
				.setContent(this));
		tabHost.addTab(tabHost.newTabSpec(hadRememberedTxt).setIndicator(hadRememberedTxt)
				.setContent(this));
	}
	
	@Override
    public View createTabContent(String tag) {

		LinearLayout mainLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.words_list, null);
		ListView wordsListView = (ListView) mainLayout.findViewById(R.id.words_list);
		wordsListView.setOnItemClickListener(showWordDetail);
		//��������ListView����Menu  ������ʵ�ֵ�onContextItemSelected()  
		//��onCreateContextMenu ��֮��Ӧ
		wordsListView.setOnCreateContextMenuListener(this);  
		mainLayout.findViewById(R.id.btn_display).setOnClickListener(changeShowExplain);
		
		if(tag.equals(getString(R.string.txt_had_remembered))){
			sqlRememberStatusShowWay = SELECTTION_ALREADY_REMEMBER;
		}else if(tag.equals(getString(R.string.txt_remembering))){
			sqlRememberStatusShowWay = SELECTTION_REMEMBERING;
		}else if(tag.equals(getString(R.string.txt_not_remember))){
			sqlRememberStatusShowWay = SELECTTION_NOT_REMEMBER;
		}else{
			sqlRememberStatusShowWay = "";
		}
		renderListView(mainLayout);
        return mainLayout;
    }
	
	private OnItemClickListener showWordDetail = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			intent.setClass(WordsListActivity.this, WordDetailActivity.class);
			intent.putExtra("sqlSelections", new String[]{sqlImportanceShowWay,sqlRememberStatusShowWay});
			intent.putExtra("wordBookId", wordBookId);
			intent.putExtra("position", position);
			startActivity(intent);
		}
	};
	
	private View.OnClickListener changeShowExplain = new View.OnClickListener() {
		public void onClick(View v) {
			Button btn = (Button)v;
			ListView wordsListView = (ListView)getTabHost().getCurrentView().findViewById(R.id.words_list);
			WordListAdapter adapter = (WordListAdapter)wordsListView.getAdapter();
			boolean isShowExplain = adapter.isShowExplain();
			adapter.setShowExplain(!isShowExplain);
			
			if(isShowExplain){
				btn.setText(R.string.display_detail);
			}else{
				btn.setText(R.string.display_word);
			}
			adapter.notifyDataSetChanged();
			isShowExplain = adapter.isShowExplain();
		}
	};
	

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		
		ListView wordsListView = (ListView)getTabHost().getCurrentView().findViewById(R.id.words_list);
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
        Cursor cursor = (Cursor) wordsListView.getAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }
        
        menu.setHeaderTitle(cursor.getString(INDEX_COLUM_WORD_BOOK_NAME));

		menu.add(0, DELETE_ID, 0, "ɾ��");
		menu.add(0, SHOW_ITEM_ID, 0, "�鿴");
	};
	
	private void renderListView(View mainLayout) {
		ListView wordsListView ;
		if(mainLayout != null){
			wordsListView = (ListView)mainLayout.findViewById(R.id.words_list);
		}
		else{
			wordsListView = (ListView)getTabHost().getCurrentView().findViewById(R.id.words_list);
		}
		
		mCursor = mWordDbManager.getWordsWithSelection(wordBookId,new String[]{sqlImportanceShowWay, sqlRememberStatusShowWay});
		startManagingCursor(mCursor);
		String[] from = new String[] { BaseColumns._ID,
				WordDbManager.WORD_COLUM_WORD, WordDbManager.WORD_COLUM_EXPLAIN };
		int[] to = new int[] { R.id.word_id, R.id.words_list_word,
				R.id.words_list_explain };
		WordListAdapter workBooks = new WordListAdapter(this,
				R.layout.words_list_item, mCursor, from, to);
		workBooks.setShowExplain(isShowExplain);
		wordsListView.setAdapter(workBooks);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWordDbManager.closeclose();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SHOW_ALL, 0, R.string.txt_show_all);
		menu.add(0, SHOW_IMPORTANCE, 0, R.string.txt_show_importance);
		menu.add(0, SHOW_EXCEPT_UNIMPORTANCE, 0, R.string.txt_show_unimportance);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		ListView wordsListView = (ListView)getTabHost().getCurrentView().findViewById(R.id.words_list);
		
		if (item.getItemId() == DELETE_ID || item.getItemId() == SHOW_ITEM_ID) {
			AdapterView.AdapterContextMenuInfo info;
			try {
				info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			} catch (ClassCastException e) {
				Log.e(TAG, "bad menuInfo", e);
				return false;
			}
			Cursor cursor = (Cursor) wordsListView.getAdapter().getItem(
					info.position);
			if (cursor == null) {
				return false;
			}

			long id = cursor.getLong(0);
			switch (item.getItemId()) {
			case DELETE_ID:
				boolean flag = mWordDbManager.deleteWord(id);
				if (flag) {
					Toast.makeText(WordsListActivity.this, "ɾ���ɹ�!",
							Toast.LENGTH_SHORT).show();
					renderListView(null);
				}
				return true;
			case SHOW_ITEM_ID:
				showItemWordDetail();
				return true;
			}
		}
		
        switch (item.getItemId()) {
            case SHOW_ALL:
            	sqlImportanceShowWay = "";
            	renderListView(null);
    			return true;
    		case SHOW_IMPORTANCE:
    			sqlImportanceShowWay = SELECTTION_ONLY_IMPORTANCE;
    			renderListView(null);
    			return true;
    		case SHOW_EXCEPT_UNIMPORTANCE:
    			sqlImportanceShowWay = SELECTTION_EXCEPT_UNIMPORTANCE;
    			renderListView(null);
    			return true;
        }
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void showItemWordDetail(){
		
	}
	
}