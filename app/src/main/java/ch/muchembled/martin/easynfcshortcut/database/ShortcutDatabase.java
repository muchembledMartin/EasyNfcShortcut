package ch.muchembled.martin.easynfcshortcut.database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

@Database(entities = {Shortcut.class}, version = 1)
public abstract class ShortcutDatabase extends RoomDatabase {
    private static ShortcutDatabase myDatabase;
    public static String DATABASE_NAME = "shortcut-db";

    public synchronized static ShortcutDatabase getInstance(Context context){
        if(myDatabase == null){
            myDatabase = Room.databaseBuilder(context, ShortcutDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        }

        return myDatabase;
    }

    public abstract ShortcutDao shortcutDao();

}
