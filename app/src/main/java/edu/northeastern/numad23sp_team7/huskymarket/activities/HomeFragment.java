package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.FragmentHomeBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
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
    private EditText searchPlate;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Product> products = new ArrayList<>();


    private SearchResultAdapter filterResultAdapter;

    private UserDao userDao = new UserDao();

    private String selectedLocation = "";

    private PreferenceManager pm;

    private String currentUserId;

    private static final ProductDao productDao = new ProductDao();

    private static final long LATEST_INTERVAL_IN_SECONDS = 360000000;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection(Constants.KEY_COLLECTION_PRODUCTS);
    private final CollectionReference usersRef = db.collection(Constants.KEY_COLLECTION_USERS);

    private final static String TAG = "Database Client";


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

    private OnBackPressedCallback callback;

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

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.recyclerViewHuskyFilterResult.setLayoutManager(layoutManager);

        // get current user
        pm = new PreferenceManager(getContext());
        currentUserId = pm.getString(Constants.KEY_USER_ID);
        userDao.getUserById(currentUserId, user -> {
            filterResultAdapter.updateLoggedInUser(user);
            filterResultAdapter.notifyDataSetChanged();
        });

        // select location
        ArrayAdapter<CharSequence> locationSpinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.locations , R.layout.home_spinner_item);

        binding.locationSpinner.setAdapter(locationSpinnerAdapter);
        binding.locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String location = String.valueOf(binding.locationSpinner.getSelectedItem());
                selectedLocation = location;
                if (binding.localFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                    localFilterTapped(view);
                }
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
        binding.searchPlate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Filters
        binding.forYouFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forYouFilterTapped(view);
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        });
        // Home screen launched with For You button clicked by default.
        //Todo:not working
        binding.forYouFilter.performClick();

        binding.latestFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestFilterTapped(view);
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        });

        binding.localFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localFilterTapped(view);
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        });

        binding.myFavoritesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFavoritesFilterTapped(view);
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        });

        // show About us
        showAboutUs();

        // Handle the back button press event
        requireActivity().getOnBackPressedDispatcher().addCallback(this.getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the previous screen
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    // If there are fragments in the back stack, pop them
                    getChildFragmentManager().popBackStack();
                } else {
                    // Otherwise, let the activity handle the back button press
                    if (requireActivity() != null && requireActivity() instanceof HuskyMainActivity) {
                        requireActivity().onBackPressed();
                    }

                }
            }
        });

        return binding.getRoot();
    }


    public void forYouFilterTapped(View view) {
        getMyFavoriteCategoriesForUser();
    }

    private void getMyFavoriteCategoriesForUser() {
        Map<String, Integer> myFavoriteCategoryMap = new HashMap<>();
        ArrayList<String> myFavoriteCategoryList = new ArrayList<>();

        ArrayList<Product> myFavorites = new ArrayList<>();
        userDao.getUserById(currentUserId, user -> {
            if (user != null && user.getFavorites() != null && !user.getFavorites().isEmpty()) {
                List<CompletableFuture<Product>> futures = new ArrayList<>();
                for (String productId: user.getFavorites()) {
                    CompletableFuture<Product> future = new CompletableFuture<>();
                    productDao.getProductById(currentUserId, productId, product -> {
                        if (product != null) {
                            future.complete(product);
                        } else {
                            future.completeExceptionally(new RuntimeException("Product not found"));
                        }
                    });
                    futures.add(future);
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                        .thenRun(() -> {
                            futures.stream()
                                    .map(CompletableFuture::join)
                                    .forEach(myFavorites::add);
                            // Use the myFavorites list here
                            for (Product myFavorite: myFavorites) {
                                String category = myFavorite.getCategory();
                                if (myFavoriteCategoryMap.containsKey(category)) {
                                    myFavoriteCategoryMap.put(category, myFavoriteCategoryMap.get(category) + 1);
                                }
                                myFavoriteCategoryMap.put(category, 1);
                            }

                            int maxValue = Integer.MIN_VALUE;
                            for (int value : myFavoriteCategoryMap.values()) {
                                if (value > maxValue) {
                                    maxValue = value;
                                }
                            }

                            for (Map.Entry<String, Integer> entry: myFavoriteCategoryMap.entrySet()) {
                                if (entry.getValue() == maxValue) {
                                    myFavoriteCategoryList.add(entry.getKey());
                                }
                            }
                            productDao.getForYouProductsForUser(currentUserId, myFavoriteCategoryList, productsList -> {
                                if (productsList != null) {
                                    filterResultAdapter.setProducts(productsList);
                                    filterResultAdapter.notifyDataSetChanged();
                                }
                            });
                        })
                        .exceptionally(ex -> {
                            // Handle the exception here
                            return null;
                        });
            }
        });
    }

    public void latestFilterTapped(View view) {
        Date currentTimestamp = new Date();
        Date latestTimestamp = new Date(currentTimestamp.getTime() - LATEST_INTERVAL_IN_SECONDS * 1000);

        productDao.getLatestProductsForUser(currentUserId, currentTimestamp, latestTimestamp, productsList -> {
            filterResultAdapter.setProducts(productsList);
            filterResultAdapter.notifyDataSetChanged();
        });

    }

    public void localFilterTapped(View view) {
        productDao.getLocalProductsForUser(currentUserId, selectedLocation, productsList -> {
            filterResultAdapter.setProducts(productsList);
            filterResultAdapter.notifyDataSetChanged();
        });
    }

    public void myFavoritesFilterTapped(View view) {
        getMyFavorites();
    }

    private void getMyFavorites() {
        ArrayList<Product> myFavorites = new ArrayList<>();
        userDao.getUserById(currentUserId, user -> {
            if (!user.getFavorites().isEmpty()) {
                for (String productId: user.getFavorites()) {
                    productDao.getProductById(currentUserId, productId, product -> {
                        myFavorites.add(product);
                        filterResultAdapter.setProducts(myFavorites);
                        filterResultAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filterResultAdapter.setProducts(myFavorites);
                filterResultAdapter.notifyDataSetChanged();
            }
        });
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

    public void listenOnProductChanges() {
        productsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: ", error);
                    return;
                }

//                ArrayList<Product> myProductList = new ArrayList<>();
//                for (QueryDocumentSnapshot document : snapshots) {
//                    Product myProduct = document.toObject(Product.class);
//                    if (myProduct.getLocation().equals(selectedLocation)) {
//                        myProductList.add(myProduct);
//                        filterResultAdapter.setProducts(myProductList);
//                        filterResultAdapter.notifyDataSetChanged();
//                    }
//                }
                if (binding.localFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                    localFilterTapped(getView());
                } else if (binding.forYouFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                    forYouFilterTapped(getView());
                } else if (binding.latestFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                    latestFilterTapped(getView());
                } else if (binding.myFavoritesFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                    myFavoritesFilterTapped(getView());
                }


//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Update the UI with the new data
//                        filterResultAdapter.setProducts(myProductList);
//                        filterResultAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        });
            }
        });
    }

    public void listenOnUserChanges() {
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: ", error);
                    return;
                }
                for (QueryDocumentSnapshot document : snapshots) {
                    User myUser = document.toObject(User.class);
                    if (myUser.getId().equals(currentUserId)) {
                        if (binding.localFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                            localFilterTapped(getView());
                        } else if (binding.forYouFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                            forYouFilterTapped(getView());
                        } else if (binding.latestFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                            latestFilterTapped(getView());
                        } else if (binding.myFavoritesFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                            myFavoritesFilterTapped(getView());
                        }
                    }
                }
            }
        });
    }

    public void listenOnCurrentUserFavoritesChanges() {
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed: ", error);
                    return;
                }
                ArrayList<Product> myFavorites = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshots) {
                    User myUser = document.toObject(User.class);
                    if (myUser.getId().equals(currentUserId)) {
                        for (String productId: myUser.getFavorites()) {
                            productDao.getProductById(currentUserId, productId, product -> {
                                myFavorites.add(product);
                                filterResultAdapter.setProducts(myFavorites);
                                filterResultAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI with the new data
                        filterResultAdapter.setProducts(myFavorites);
                        filterResultAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.searchPlate.setOnClickListener(v -> startSearchPage());
        binding.searchPlate.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                startSearchPage();
            }
        });
        filterResultAdapter.setupLoggedInUser();
    }

    private void startSearchPage() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra(Constants.KEY_PRODUCT_LOCATION, selectedLocation);
        startActivity(intent);
    }

}