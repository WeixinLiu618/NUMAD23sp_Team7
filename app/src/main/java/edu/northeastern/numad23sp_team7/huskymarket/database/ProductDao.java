package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class ProductDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference productsRef = db.collection(Constants.KEY_COLLECTION_PRODUCTS);
    private final static String TAG = "Product Dao";

    public void getProductsBySearch(String searchTerm, String category, String location, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_AVAILABLE);

        if (!category.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_CATEGORY, category);
        }

        if (!location.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_LOCATION, location);
        }

        productsQuery = productsQuery.orderBy(Constants.KEY_PRODUCT_TIMESTAMP, Query.Direction.DESCENDING);

        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (
                                !searchTerm.isEmpty()
                                        && (product.getDescription().toLowerCase().contains(searchTerm.toLowerCase())
                                        || product.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                        ) {
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

    public void getLatestProductsForUser(String currentUserId, Date currentTimestamp, Date latestTimeStamp, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_AVAILABLE);

        productsQuery = productsQuery.whereLessThanOrEqualTo(Constants.KEY_PRODUCT_TIMESTAMP, currentTimestamp);
        productsQuery = productsQuery.whereGreaterThanOrEqualTo(Constants.KEY_PRODUCT_TIMESTAMP, latestTimeStamp);

        productsQuery = productsQuery.orderBy(Constants.KEY_PRODUCT_TIMESTAMP, Query.Direction.DESCENDING);
        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (!currentUserId.isEmpty()) {
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

    public void getLocalProductsForUser(String currentUserId, String selectedLocation, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_AVAILABLE);

        productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_LOCATION, selectedLocation);

        productsQuery = productsQuery.orderBy(Constants.KEY_PRODUCT_TIMESTAMP, Query.Direction.DESCENDING);
        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (!currentUserId.isEmpty() && !selectedLocation.isEmpty()) {
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

    public void getProductById (String currentUserId, String productId, final Consumer<Product> callback) {
        Query productQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_ID, productId);
        productQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                if (!currentUserId.isEmpty()) {
                                    callback.accept(product);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getForYouProductsForUser(String currentUserId, ArrayList<String> myFavoriteCategoryList,
                                         Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();

        if (myFavoriteCategoryList.isEmpty()) {
            Query productsQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_AVAILABLE);
            productsQuery = productsQuery.orderBy(Constants.KEY_PRODUCT_TIMESTAMP, Query.Direction.DESCENDING);

            productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (!currentUserId.isEmpty()) {
                                products.add(product);
                            }
                        }
                        callback.accept(products);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        } else {
            for (String category : myFavoriteCategoryList) {
                Query productsQuery = productsRef.whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_AVAILABLE);
                productsQuery = productsQuery.whereEqualTo(Constants.KEY_PRODUCT_CATEGORY, category);

                productsQuery = productsQuery.orderBy(Constants.KEY_PRODUCT_TIMESTAMP, Query.Direction.DESCENDING);
                productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                if (!currentUserId.isEmpty()) {
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
        }
    }


    public void getMyPostsProductsForUser(String currentUserId, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef
                .whereEqualTo(Constants.KEY_POST_USER_ID, currentUserId);

        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (!currentUserId.isEmpty()) {
                            products.add(product);
                        }
                    }
                    Log.d(TAG, "onComplete: " + products.size());

                    callback.accept(products);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getMySoldProductsForUser(String currentUserId, final Consumer<ArrayList<Product>> callback) {
        ArrayList<Product> products = new ArrayList<>();
        Query productsQuery = productsRef
                .whereEqualTo(Constants.KEY_POST_USER_ID, currentUserId)
                .whereEqualTo(Constants.KEY_PRODUCT_STATUS, Constants.VALUE_PRODUCT_STATUS_SOLD);

        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (!currentUserId.isEmpty()) {
                            products.add(product);
                        }
                    }
                        callback.accept(products);
                    } else{
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }

            });
        }

    public void addProducts(ArrayList<Product> products) {
        for (Product product : products) {
            String productId = productsRef.document().getId();
            product.setProductId(productId);
            productsRef.document(productId).set(product)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Product added with ID: " + productId))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding product", e));
            productsRef.document(productId).update(Constants.KEY_PRODUCT_TIMESTAMP, FieldValue.serverTimestamp())
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Product updated with timestamp"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating timestamp", e));
        }
    }

    public void updateProductStatus(String productId, String status, final Consumer<Product> callback) {
        DocumentReference productRef = productsRef.document(productId);
        productRef.update(Constants.KEY_PRODUCT_STATUS, status)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Product status successfully updated!" + status);
                    productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Product product = documentSnapshot.toObject(Product.class);
                            callback.accept(product);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating product status.", e);
                });
    }

    public void updateProductId(String productId) {
        productsRef.document(productId)
                .update(Constants.KEY_PRODUCT_ID, productId)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "ProductId successfully updated!" + productId);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating productId.", e);
                });
    }

}
