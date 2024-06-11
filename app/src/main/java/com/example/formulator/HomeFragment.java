package com.example.formulator;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

public class HomeFragment extends Fragment {

    // Declare UI elements and directory for files
    LinearLayout homeLL;
    TextView homeCNF;
    File dir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the UI elements
        homeLL = root.findViewById(R.id.homeLL);
        homeCNF = root.findViewById(R.id.homeCNF);

        // Get the directory for "Formula Files" in external storage
        dir = getActivity().getExternalFilesDir("Formula Files");
        // List all files in the directory
        File[] filesArr = dir.listFiles();

        // Loop through each file and insert a row for each file name
        for (int i = 0; i < filesArr.length; i++) {
            String fileName = filesArr[i].getName();
            String name = fileName.substring(0, fileName.length() - 4); // Remove file extension
            insertRow(name);
        }

        // Set an onClickListener for the homeCNF TextView
        homeCNF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new ManagingFragment
                Fragment ldf = new ManagingFragment();
                // Replace the current fragment with ManagingFragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FLayout, ldf);
                fragmentTransaction.commit();
            }
        });

        // Return the root view
        return root;
    }

    // Convert dp to px
    private int topx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    // Method to insert a row with the given name into the LinearLayout
    private void insertRow(final String name) {
        // Create a new LinearLayout and TextView
        LinearLayout newLL = new LinearLayout(getActivity());
        TextView txtView = new TextView(getActivity());

        // Set layout parameters for the LinearLayout
        newLL.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        newLL.setMinimumHeight(topx(60));
        newLL.setPadding(0, topx(10), 0, 0);

        // Set properties for the TextView
        txtView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_back));
        txtView.setPadding(topx(15), 0, 0, 0);
        txtView.setSingleLine(true);
        txtView.setGravity(Gravity.CENTER_VERTICAL);
        txtView.setText(name);
        txtView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);

        // Set an onClickListener for the TextView
        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new SubjectFragment and pass the file name as an argument
                Fragment ldf = new SubjectFragment();
                Bundle args = new Bundle();
                args.putString("fileName", name);
                ldf.setArguments(args);

                // Replace the current fragment with SubjectFragment and tag it as "sub"
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FLayout, ldf, "sub");
                fragmentTransaction.commit();
            }
        });

        // Add the TextView to the LinearLayout and the LinearLayout to the homeLL
        newLL.addView(txtView, -1, -1);
        homeLL.addView(newLL);
    }
}
