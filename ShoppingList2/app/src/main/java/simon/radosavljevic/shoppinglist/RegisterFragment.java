package simon.radosavljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button registerButton;
    Button home;
    EditText username;
    EditText email;
    EditText password;
    TextView istiUsername;
    HttpHelper httpHelper;
    private static String RegisterURL = MainActivity.urlBase + "/users";

    private final String DB_NAME = "shared_list_app.db";

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerButton = view.findViewById(R.id.registerPravi);
        httpHelper = new HttpHelper();

        registerButton.setOnClickListener(view1 -> {
            DbHelper dbHelper = new DbHelper(getContext(), DB_NAME, null, 1);
            username = view.findViewById(R.id.username);
            email = view.findViewById(R.id.email);
            password = view.findViewById(R.id.password);
            istiUsername = view.findViewById(R.id.istiUsername);
            /*if(dbHelper.readUser(username.getText().toString())==null){
                istiUsername.setText("");
                User user=new User(username.getText().toString(),email.getText().toString(),password.getText().toString());
                dbHelper.insertUser(user);*/

            new Thread(new Runnable() {
                public void run() {
                    try {
                        JSONObject requestJSON = new JSONObject();
                        requestJSON.put("username", username.getText().toString());
                        requestJSON.put("password", password.getText().toString());
                        requestJSON.put("email", email.getText().toString());

                        boolean jsonObject = httpHelper.postJSONObjectFromURL(RegisterURL, requestJSON);
                        if (jsonObject) {
                            String userValue = username.getText().toString();
                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            Bundle bund = new Bundle();
                            bund.putString("name", userValue);
                            intent.putExtras(bund);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    startActivity(intent);
                                }
                            });

                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    istiUsername.setText("Error");
                                }
                            });
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        istiUsername.setText("Error");
                    }
                }
            }).start();



           /*}
            else{
                istiUsername.setText("Username koji ste uneli vec posotji.");
            }*/
        });
        home = view.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}