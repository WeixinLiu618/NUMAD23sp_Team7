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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    //24 hours
    private static final long LATEST_INTERVAL_IN_SECONDS = 86400;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection(Constants.KEY_COLLECTION_PRODUCTS);
    private final CollectionReference usersRef = db.collection(Constants.KEY_COLLECTION_USERS);

    private final static String TAG = "Database Client";

    private Integer mLastClickedButtonId = null;


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
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

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
        binding.searchPlate.setOnClickListener(v -> startSearchPage());
//        binding.searchPlate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    startSearchPage();
//                }
//            }
//        });

        // Filters
        binding.forYouFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forYouFilterTapped(view);
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                mLastClickedButtonId = R.id.forYouFilter;
            }
        });

        // Home screen launched with For You button clicked by default.8ì
        if (savedInstanceState == null) {
            binding.forYouFilter.performClick();
        }


        binding.latestFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestFilterTapped(view);
                binding.latestFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                binding.myFavoritesFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.forYouFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                binding.localFilter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                mLastClickedButtonId = R.id.latestFilter;
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
                mLastClickedButtonId = R.id.localFilter;
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
                mLastClickedButtonId = R.id.myFavoritesFilter;
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

        // Check savedInstanceState
        if (savedInstanceState != null) {
            mLastClickedButtonId = savedInstanceState.getInt("last_clicked_button_id");
            selectedLocation = savedInstanceState.getString("selected_location");
            currentUserId = savedInstanceState.getString("logged_in_user_id");

            // Perform the click action for the last clicked button
            if (mLastClickedButtonId != null) {
                View lastClickedButton = binding.getRoot().findViewById(mLastClickedButtonId);
                lastClickedButton.performClick();
            }
        }

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
                                    products = productsList;
                                    filterResultAdapter.setProducts(products);
                                    filterResultAdapter.notifyDataSetChanged();
                                    loading(false, products);
                                }
                            });
                        })
                        .exceptionally(ex -> {
                            // Handle the exception here
                            System.err.println("Exception occurred: " + ex.getMessage());
//                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occurred", ex);
                            return null;
                        });
            }
        });
    }

    public void latestFilterTapped(View view) {
        Date currentTimestamp = new Date();
        Date latestTimestamp = new Date(currentTimestamp.getTime() - LATEST_INTERVAL_IN_SECONDS * 1000);

        productDao.getLatestProductsForUser(currentUserId, LATEST_INTERVAL_IN_SECONDS, productsList -> {
            products = productsList;
            filterResultAdapter.setProducts(products);
            filterResultAdapter.notifyDataSetChanged();
            loading(false, products);
        });

    }

    public void localFilterTapped(View view) {
        productDao.getLocalProductsForUser(currentUserId, selectedLocation, productsList -> {
            products = productsList;
            filterResultAdapter.setProducts(products);
            filterResultAdapter.notifyDataSetChanged();
            loading(false, products);
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
                        products = myFavorites;
                        filterResultAdapter.setProducts(products);
                        filterResultAdapter.notifyDataSetChanged();
                        loading(false, products);
                    });
                }
            }
        });
    }

    private void showAboutUs() {
        binding.info.setOnClickListener(new View.OnClickListener() {
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

        if (binding.myFavoritesFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
            myFavoritesFilterTapped(binding.getRoot());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("filter_result_list", products);
        outState.putString("selected_location", selectedLocation);
        outState.putInt("last_clicked_button_id", mLastClickedButtonId);
        outState.putString("logged_in_user_id", currentUserId);

    }


    private void startSearchPage() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra(Constants.KEY_PRODUCT_LOCATION, selectedLocation);
        startActivity(intent);
    }

    private void loading(boolean isLoading, ArrayList myProductList) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerViewHuskyFilterResult.setVisibility(View.GONE);
            binding.noFavoritessPrompt.setVisibility(View.GONE);
            binding.noLocalPrompt.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (myProductList.size() == 0 && binding.localFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                binding.noFavoritessPrompt.setVisibility(View.GONE);
                binding.noLocalPrompt.setVisibility(View.VISIBLE);
            } else if (myProductList.size() == 0 && binding.myFavoritesFilter.getCurrentTextColor() == getResources().getColor(R.color.primary)) {
                binding.noFavoritessPrompt.setVisibility(View.VISIBLE);
                binding.noLocalPrompt.setVisibility(View.GONE);
            } else {
                binding.recyclerViewHuskyFilterResult.smoothScrollToPosition(0);
                Log.d(TAG, "loading: " + "来到这里");
                binding.recyclerViewHuskyFilterResult.setVisibility(View.VISIBLE);
                binding.noFavoritessPrompt.setVisibility(View.GONE);
                binding.noLocalPrompt.setVisibility(View.GONE);
            }
        }
    }

}