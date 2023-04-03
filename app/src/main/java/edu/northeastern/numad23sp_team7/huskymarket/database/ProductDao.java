package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.function.Consumer;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class ProductDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection(Constants.KEY_COLLECTION_PRODUCTS);
    private final static String TAG = "Database Client";

    public void getProducts(String searchTerm, String category, String location, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef;

        if (!category.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_CATEGORY, category);
        }

        if (!location.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_LOCATION, location);
        }

        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (searchTerm.isEmpty() || product.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                            products.add(product);
                        }
                    }

                    callback.accept(products);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addProducts(ArrayList<Product> products) {
        for (Product product : products) {
            productsRef.add(product)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Product added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding product", e));
        }
    }
}
