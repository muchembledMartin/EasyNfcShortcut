package ch.muchembled.martin.easynfcshortcut;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ch.muchembled.martin.easynfcshortcut.database.ShortcutDao;
import ch.muchembled.martin.easynfcshortcut.database.ShortcutDatabase;
import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Shortcut> shortcuts;

    private ShortcutDatabase db;
    private ShortcutDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d("main", "Creating views");

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        db = ShortcutDatabase.getInstance(this);
        dao = db.shortcutDao();

        shortcuts = dao.getAllShortcuts();

        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        onIntent(getIntent());
    }

    private void onIntent(Intent intent){

        if(
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
        ){
            intent.setAction(null);
            Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            StringBuilder builder = new StringBuilder("#");
            for(byte b : tag.getId()){
                builder.append(String.format("%02x", b));
            }
            String tagId = builder.toString();
            boolean exist = false;
            for(Shortcut s : shortcuts){
                if(s.getTagId().equals(tagId)){
                    exist = true;
                    Log.d("main", "onIntent: " + (getPackageManager().getLaunchIntentForPackage("com.instagram.android") == null));
                    startActivity(s.generateIntent(this));
                    finish();
                }
            }

            if(!exist){
                Intent addActivityIntent = new Intent(this, AddShortcutActivity.class);
                addActivityIntent.putExtra(AddShortcutActivity.IS_TAG_KNOWN_KEY, true);
                addActivityIntent.putExtra(AddShortcutActivity.TAG_KEY, tagId);
                startActivityForResult(addActivityIntent, AddShortcutActivity.ADD_SHORTCUT_ACTIVITY_RESULT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case AddShortcutActivity.ADD_SHORTCUT_ACTIVITY_RESULT:
                shortcuts = dao.getAllShortcuts();
                ((ShortcutAdapter) recyclerView.getAdapter()).setShortcuts(shortcuts);
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void initViews(){
        Log.d("main", "initViews: " + shortcuts.size());
        this.recyclerView = findViewById(R.id.main_activity_recycler_view);

        recyclerView.setAdapter(new ShortcutAdapter(
                shortcuts,
                (View v) -> {
                    Intent intent = new Intent(this, AddShortcutActivity.class);
                    intent.putExtra(AddShortcutActivity.IS_TAG_KNOWN_KEY, false);
                    startActivityForResult(intent, AddShortcutActivity.ADD_SHORTCUT_ACTIVITY_RESULT);
                },
                (View v, Shortcut shortcut) -> {
                    // On shortcut clicked
                }
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}