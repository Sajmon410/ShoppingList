package simon.radosavljevic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.SQLDataException;

public class DbHelper extends SQLiteOpenHelper {

    private final String TABLE_NAME_USERS = "USERS";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    private final String TABLE_NAME_LISTS = "LISTS";
    public static final String COLUMN_NAZIV_LISTE = "ListName";
    public static final String COLUMN_KREATOR_LISTE = "ListCreator";
    public static final String COLUMN_DELJENA = "Shared";

    private final String TABLE_NAME_ITEMS = "ITEMS";
    public static final String COLUMN_NAZIV = "naziv";
    public static final String COLUMN_NAZIV_LISTE_ITEMS = "naziv_liste";
    public static final String COLUMN_OTKACENA = "otkacena";
    public static final String COLUMN_ID = "ID";

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE " + TABLE_NAME_USERS +
                " ( " + COLUMN_USERNAME + " TEXT," +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT); ");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_LISTS +
                " (" + COLUMN_NAZIV_LISTE + " TEXT, " +
                COLUMN_KREATOR_LISTE + " TEXT, " +
                COLUMN_DELJENA + " TEXT);");
        sqLiteDatabase.execSQL(" CREATE TABLE " + TABLE_NAME_ITEMS +
                " ( " + COLUMN_ID + " TEXT," +
                COLUMN_NAZIV + " TEXT, " +
                COLUMN_NAZIV_LISTE_ITEMS + " TEXT," +
                COLUMN_OTKACENA + " TEXT); ");

    }

    //INSERTI
    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.insert(TABLE_NAME_USERS, null, values);
        close();
    }

    public void insertList(Lista lista) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAZIV_LISTE, lista.getListName());
        values.put(COLUMN_KREATOR_LISTE, lista.getCreatorName());
        values.put(COLUMN_DELJENA, lista.getShared());

        db.insert(TABLE_NAME_LISTS, null, values);
        close();
    }

    public void insertItem(Zadatak zadatak) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAZIV, zadatak.getNaziv());
        values.put(COLUMN_NAZIV_LISTE_ITEMS, zadatak.getNaziv_liste());
        values.put(COLUMN_OTKACENA, zadatak.getOtkacena());
        values.put(COLUMN_ID, zadatak.getID());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME_ITEMS, null, values);
        close();
    }

    //delete
    public void deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_USERS, COLUMN_USERNAME + " =?", new String[]{username});
        close();
    }

    public void deleteListe(String nazivListe) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_LISTS, COLUMN_NAZIV_LISTE + " =?", new String[]{nazivListe});
        close();
    }

    public void deleteItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_ITEMS, COLUMN_ID + " =?", new String[]{id});
        close();
    }

    //QUERI ----------------USER
    public User readUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USERS, null, COLUMN_USERNAME + " =?", new String[]{username}, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        User user = createUser(cursor);

        close();
        return user;

    }

    public User readUserPassword(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USERS, null, COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password}, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        User user = createUser(cursor);

        close();
        return user;

    }

    ///QUERI---LISTA----
    public Lista readListName(String ListName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_LISTS, null, COLUMN_NAZIV_LISTE + " =?", new String[]{ListName}, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        Lista lista = createList(cursor);

        close();
        return lista;

    }

    public Lista[] mojeIDeljene(Lista lista) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_LISTS, null, COLUMN_KREATOR_LISTE + " =? OR " + COLUMN_DELJENA + " =? ", new String[]{lista.getCreatorName(), "true"}, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        Lista lista1[] = new Lista[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            lista1[i] = createList(cursor);
            cursor.moveToNext();
        }
        close();
        return lista1;
    }

    public Lista[] mojeListe(Lista lista) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_LISTS, null, COLUMN_KREATOR_LISTE + " =? ", new String[]{lista.getCreatorName()}, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        Lista lista1[] = new Lista[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            lista1[i] = createList(cursor);
            cursor.moveToNext();
        }
        close();
        return lista1;
    }
    public Lista[] deljeneListe(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_LISTS, null,   COLUMN_DELJENA + " =? ", new String[]{"true"}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Lista lista1[] = new Lista[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            lista1[i] = createList(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return lista1;
    }
    public boolean updateListItems(String listName, Zadatak[] taskList) {
        SQLiteDatabase db = getWritableDatabase();
        int numRowsAffected = db.delete(TABLE_NAME_ITEMS, COLUMN_NAZIV_LISTE_ITEMS + " = ?", new String[]{listName});

        for(Zadatak task : taskList){
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAZIV, task.getNaziv());
            values.put(COLUMN_NAZIV_LISTE_ITEMS, listName);
            values.put(COLUMN_ID, task.getID());
            values.put(COLUMN_OTKACENA, task.getOtkacena());

            long row = db.insert(TABLE_NAME_ITEMS, null, values);
        }
        close();
        return true;
    }
    public boolean queryLists(String key) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("TABLE_NAME_LISTS", null, "COLUMN_NAZIV_LISTE = ?", new String[]{key}, null, null, null);

        boolean found = cursor.moveToFirst();

        cursor.close();
        db.close();

        return found;
    }
    public boolean addList(String title, String creator, Boolean shared) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query("TABLE_NAME_LISTS", null, "COLUMN_NAZIV_LISTE = ?", new String[]{title}, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put("COLUMN_NAZIV_LISTE", title);
        values.put("COLUMN_KREATOR_LISTE", creator);
        values.put("COLUMN_DELJENA", shared);
        db.insert("TABLE_NAME_LISTS", null, values);

        cursor.close();
        db.close();
        return true;
    }
    ///QUERI ZADATAK
    public Zadatak[] mojiZadaci(String zad) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ITEMS, null, COLUMN_NAZIV_LISTE_ITEMS + " =? ", new String[]{zad}, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        Zadatak zadatak[] = new Zadatak[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            zadatak[i] = createZadatak(cursor);
            cursor.moveToNext();
        }
        close();
        return zadatak;
    }

    public void updateItem(String name, String mChecked) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME_ITEMS, null, COLUMN_NAZIV + "=?", new String[]{name}, null, null, null);
        cursor.moveToFirst();
        ContentValues values = new ContentValues();

        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAZIV_LISTE_ITEMS));
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));

        values.put(COLUMN_NAZIV, name);
        values.put(COLUMN_NAZIV_LISTE_ITEMS, title);
        values.put(COLUMN_OTKACENA, mChecked);
        values.put(COLUMN_ID, id);
        db.update(TABLE_NAME_ITEMS, values, COLUMN_ID + "=?", new String[]{id});
        close();
    }
    public void addTask(String title, String list, String id, String checked) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("COLUMN_NAZIV", title);
        values.put("COLUMN_NAZIV_LISTE_ITEMS", list);
        values.put("COLUMN_OTKACENA", checked);
        values.put("COLUMN_ID", id);

        db.insert("TABLE_NAME_ITEMS", null, values);

        db.close();

    }
    public Boolean provera(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ITEMS, null, COLUMN_NAZIV + " =?", new String[]{name}, null, null, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        cursor.moveToFirst();
        Zadatak zadatak = createZadatak(cursor);
        if (zadatak.getOtkacena().equals("jeste")) {
            close();
            return true;
        } else {
            close();
            return false;
        }


    }

    public void deleteItemsFromList(String ime) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_ITEMS, COLUMN_NAZIV_LISTE_ITEMS + " =?", new String[]{ime});
        close();
    }

    /////////CREATE
    private User createUser(Cursor cursor) {
        String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

        return new User(username, email, password);

    }

    private Lista createList(Cursor cursor) {
        String listName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAZIV_LISTE));
        String creator = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KREATOR_LISTE));
        String shared = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELJENA));

        return new Lista(listName, creator, shared);

    }

    private Zadatak createZadatak(Cursor cursor) {
        String naziv = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAZIV));
        String naziv_liste = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAZIV_LISTE_ITEMS));
        String otkacena = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OTKACENA));
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));

        return new Zadatak(naziv, naziv_liste, otkacena, id);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
