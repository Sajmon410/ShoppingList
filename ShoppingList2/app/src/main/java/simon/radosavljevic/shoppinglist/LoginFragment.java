package simon.radosavljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

    EditText user;
    EditText pass;
    TextView pogresno;
    DbHelper dbHelper;
    Button home;
    HttpHelper httpHelper;
    private static String LoginUrl = MainActivity.urlBase + "/login";
    private final String DB_NAME = "shared_list_app.db";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HttpHelper httpHelper = new HttpHelper();
        DbHelper dbHelper = new DbHelper(getContext(), DB_NAME, null, 1);
        Button loginPravi;
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        loginPravi = v.findViewById(R.id.loginPravi);
        user = v.findViewById(R.id.username);
        pass = v.findViewById(R.id.password);
        pogresno = v.findViewById(R.id.pogresno);


        loginPravi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (dbHelper.readUserPassword(user.getText().toString(),pass.getText().toString())==null) {
                    pogresno.setText("Pogresan username ili sifra.");
                }
                else{
                    pogresno.setText("");

                    String userValue = user.getText().toString();
                    String passValue = pass.getText().toString();
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    Bundle bund = new Bundle();
                    bund.putString("name",userValue);
                    intent.putExtras(bund);
                    startActivity(intent);

                }
            */
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject requestJSON = new JSONObject();
                            requestJSON.put("username", user.getText().toString());
                            requestJSON.put("password", pass.getText().toString());
                            boolean jsonObject = httpHelper.postJSONObjectFromURL(LoginUrl, requestJSON);
                            if (jsonObject) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        String userValue = user.getText().toString();
                                        String passValue = pass.getText().toString();
                                        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                                        Bundle bund = new Bundle();
                                        bund.putString("name", userValue);
                                        intent.putExtras(bund);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), "Greska pri logovanju", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

            ;
              /*  String userValue = user.getText().toString();
                String passValue = pass.getText().toString();
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                Bundle bund = new Bundle();
                bund.putString("name",userValue);
                intent.putExtras(bund);
                startActivity(intent);*/

        });

        home = v.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });
        return v;
    }
}