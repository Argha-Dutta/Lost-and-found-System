package com.shruti.lofo.ui.MyItems;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.shruti.lofo.R;
import com.shruti.lofo.Utility;
import com.shruti.lofo.databinding.FragmentLostBinding;
import com.shruti.lofo.ui.Found.FoundItems;
import com.shruti.lofo.ui.Found.FoundItemsAdapter;
import com.shruti.lofo.ui.Lost.LostItems;
import com.shruti.lofo.ui.Lost.LostItemsAdapter;

public class MyItems extends Fragment {

    private FragmentLostBinding binding;
    private LostItemsAdapter lostAdapter;
    private FoundItemsAdapter foundAdapter;
    private String userId;

    private RecyclerView lostRecyclerView;
    private RecyclerView foundRecyclerView;
    private FloatingActionButton add;
    private TextView filterButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case where currentUser is null (e.g., user not authenticated)
            // You may choose to navigate to the login screen or display a message
            // Example: startActivity(new Intent(getContext(), LoginActivity.class));
        }

        lostRecyclerView = root.findViewById(R.id.lostRecyclerView);
        foundRecyclerView = root.findViewById(R.id.foundRecyclerView);
        add = root.findViewById(R.id.add_lost);
        filterButton = root.findViewById(R.id.filterButton);

        setupRecyclerView(true);

        return root;
    }

    void setupRecyclerView(boolean showDeleteButton) {
        add.setVisibility(View.GONE);
        filterButton.setText("All LoFo!");
        filterButton.setTextSize(24);

        // Configure lost items RecyclerView
        if (userId != null) {
            Query lostQuery = Utility.getCollectionReferrenceForItems2();
            FirestoreRecyclerOptions<LostItems> lostOptions = new FirestoreRecyclerOptions.Builder<LostItems>()
                    .setQuery(lostQuery, LostItems.class).build();

            lostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            lostAdapter = new LostItemsAdapter(lostOptions, requireContext(), "", showDeleteButton);
            lostRecyclerView.setAdapter(lostAdapter);
        }

        // Configure found items RecyclerView
        if (userId != null) {
            Query foundQuery = Utility.getCollectionReferrenceForFound();
            FirestoreRecyclerOptions<FoundItems> foundOptions = new FirestoreRecyclerOptions.Builder<FoundItems>()
                    .setQuery(foundQuery, FoundItems.class).build();

            foundRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            foundAdapter = new FoundItemsAdapter(foundOptions, requireContext(), "", showDeleteButton);
            foundRecyclerView.setAdapter(foundAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lostAdapter != null) {
            lostAdapter.startListening();
        }
        if (foundAdapter != null) {
            foundAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lostAdapter != null) {
            lostAdapter.stopListening();
        }
        if (foundAdapter != null) {
            foundAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lostAdapter != null) {
            lostAdapter.notifyDataSetChanged();
        }
        if (foundAdapter != null) {
            foundAdapter.notifyDataSetChanged();
        }
    }
}
