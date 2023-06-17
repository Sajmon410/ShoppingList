package simon.radosavljevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button login;
    Button register;
    public static String DB_NAME="shared_list_app.db";
    DbHelper dbHelper;
    public static String urlBase ="http://192.168.0.31:3000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        //za ostale baze


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setVisibility(View.INVISIBLE);
                register.setVisibility(View.INVISIBLE);
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, loginFragment).commit();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setVisibility(View.INVISIBLE);
                register.setVisibility(View.INVISIBLE);
                RegisterFragment registerFragment = new RegisterFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, registerFragment).commit();
            }
        });

    }
}