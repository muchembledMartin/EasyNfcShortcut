package ch.muchembled.martin.easynfcshortcut.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.muchembled.martin.easynfcshortcut.model.Shortcut;

@Dao
public interface ShortcutDao {

    @Query("SELECT * FROM shortcut")
    List<Shortcut> getAllShortcuts();

    @Insert
    void insert(Shortcut shortcut);

    @Delete
    void delete(Shortcut shortcut);

    @Delete
    void deleteAll(List<Shortcut> shortcuts);
}