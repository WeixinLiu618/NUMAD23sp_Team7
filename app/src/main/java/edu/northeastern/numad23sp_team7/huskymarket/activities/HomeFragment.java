package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.FragmentHomeBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Activity context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText searchPlate;

    private ArrayList<Product> products = new ArrayList<>();


    private SearchResultAdapter filterResultAdapter;

    private UserDao userDao;

    private String selectedLocation = "";

    private PreferenceManager pm;

    private String currentUserId;

    private static final ProductDao dbClient = new ProductDao();

    private static final long LATEST_INTERVAL_IN_SECONDS = 360000000;


    private static String ABOUT_US = "Find preloved deals within NEU community today!\n" +
            "If you are current faculty, current student or alumni at Northeastern University, and are looking for second-hand deals from trusted sources, HuskyMarket is your go-to-place.\n" +
            "Key features:\n" +
            "\n" +
            "\t- NEU verified users\n" +
            "\t- Live chat\n" +
            "\t- Free to post with pictures\n" +
            "\t- Recommend new posts for users\n" +
            "\n" +
            "We are looking forward to seeing you at HuskyMarket!";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


    public void allFilterTapped(View view) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);

        binding = FragmentHomeBinding.inflate(getLayoutInflater());
//        this.context.setContentView(binding.getRoot());

        // filter result adapter
        filterResultAdapter = new SearchResultAdapter(products, binding.getRoot().getContext());
        binding.recyclerViewHuskyFilterResult.setAdapter(filterResultAdapter);

        // get current user
        userDao = new UserDao();
        pm = new PreferenceManager(getContext());
        currentUserId = pm.getString(Constants.KEY_USER_ID);
        userDao.getUserById(currentUserId, user -> {
            filterResultAdapter.updateLoggedInUser(user);
            filterResultAdapter.notifyDataSetChanged();
        });

        // select location
        binding.locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String location = String.valueOf(binding.locationSpinner.getSelectedItem());
                selectedLocation = location;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Please select a location.");
                builder.setCancelable(true);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something when OK button is clicked
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // direct search bar to search page
        binding.searchPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // Filters
        binding.latestFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestFilterTapped(view);
            }
        });

        binding.localFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localFilterTapped(view);
            }
        });

        // show About us
        showAboutUs();
        return binding.getRoot();
    }

    public void latestFilterTapped(View view) {
        Date currentTimestamp = new Date();
        Date latestTimestamp = new Date(currentTimestamp.getTime() - LATEST_INTERVAL_IN_SECONDS * 1000);

        dbClient.getLatestProductsForUser(currentUserId, currentTimestamp, latestTimestamp, productsList -> {
            filterResultAdapter.setProducts(productsList);
            filterResultAdapter.notifyDataSetChanged();
        });

    }

    public void localFilterTapped(View view) {
        dbClient.getLocalProductsForUser(currentUserId, selectedLocation, productsList -> {
            filterResultAdapter.setProducts(productsList);
            filterResultAdapter.notifyDataSetChanged();
        });
    }

    public void myPostsFilterTapped() {

    }

    public void myFavoritesFilterTapped() {

    }
    private void showAboutUs() {
        binding.aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("About us");
                builder.setMessage(ABOUT_US);
                builder.setCancelable(true);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something when OK button is clicked
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}