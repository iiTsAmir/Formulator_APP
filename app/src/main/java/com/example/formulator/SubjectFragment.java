package com.example.formulator;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.formulator.myClass.Comp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class SubjectFragment extends Fragment {

    // Declare UI elements and variables
    TextView subLabel, subANC;
    Button subCalBtn, subResetBtn;
    String fileName, label;
    LinearLayout subLL;
    File dir;
    ArrayList<Float> rows;
    ArrayList<Comp> compList = new ArrayList<>();
    private int rowCount = 0;
    private Map<Integer, EditText> rowMap = new HashMap<>();
    Float nesbat = 1f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_subject, container, false);

        // Get the file name from the arguments
        fileName = getArguments().getString("fileName");

        // Initialize UI elements
        subLabel = root.findViewById(R.id.subLabel);
        subANC = root.findViewById(R.id.subANC);
        subLL = root.findViewById(R.id.subLL);
        subCalBtn = root.findViewById(R.id.subCalBtn);
        subResetBtn = root.findViewById(R.id.subResetBtn);

        rows = new ArrayList<>();

        // Format and set the label for subLabel TextView
        byte[] labelChar = fileName.getBytes();
        label = ((char) labelChar[0]) + "";
        label = label.toUpperCase();
        for (int i = 1; i < labelChar.length; i++) {
            char x = (char) labelChar[i];
            label = label + " " + x;
        }
        subLabel.setText(label);

        // Get the directory for "Formula Files" in external storage
        dir = getActivity().getExternalFilesDir("Formula Files");

        // Read the file and get the list of Comp objects
        compList = readFile(fileName);

        // Load and insert rows for each Comp object
        for (int i = 0; i < compList.size(); i++) {
            insertRow(compList.get(i).getName(), compList.get(i).getUnit() + "", compList.get(i).getUnitName());
        }

        // Initialize the rows list with the values from EditTexts
        for (Map.Entry<Integer, EditText> entry : rowMap.entrySet()) {
            rows.add(Float.valueOf(entry.getValue().getText().toString()));
        }

        // Set onClickListener for subANC TextView to replace fragment with EditingFragment
        subANC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment ldf = new EditingFragment();
                Bundle args = new Bundle();
                args.putString("fileName", fileName);
                ldf.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FLayout, ldf, "edit");
                fragmentTransaction.commit();
            }
        });

        // Set onClickListener for subCalBtn Button to perform calculations
        subCalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, EditText> entry : rowMap.entrySet()) {
                    Float lastET = rows.get(entry.getKey());
                    Float newET = Float.valueOf(entry.getValue().getText().toString());
                    if (!Objects.equals(lastET, newET)) {
                        nesbat = newET / lastET;
                        break;
                    }
                }

                for (Map.Entry<Integer, EditText> entry : rowMap.entrySet()) {
                    Float newUnit = rows.get(entry.getKey()) * nesbat;
                    setText(entry.getKey(), newUnit + "");
                }
                hideKeyboard();
            }
        });

        // Set onClickListener for subResetBtn Button to reset EditText values
        subResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, EditText> entry : rowMap.entrySet()) {
                    setText(entry.getKey(), rows.get(entry.getKey()) + "");
                    nesbat = 1f;
                }
                hideKeyboard();
            }
        });

        return root;
    }

    // Convert dp to px
    private int topx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    // Method to insert a row with given name, unit number, and unit name into the LinearLayout
    private void insertRow(String name, String unitnum, String unitname) {
        // Create a new LinearLayout for the row
        LinearLayout rowLayout = new LinearLayout(getActivity());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.home_back));

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, topx(50));
        rowParams.setMargins(0, topx(10), 0, 0);
        rowLayout.setLayoutParams(rowParams);

        // Create and configure the TextView for the component name
        TextView obj1 = new TextView(getActivity());
        obj1.setText(name);
        obj1.setPadding(topx(10), 0, 0, 0);
        obj1.setGravity(Gravity.CENTER_VERTICAL);
        obj1.setTextSize(17);
        obj1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));

        LinearLayout.LayoutParams obj1Params = new LinearLayout.LayoutParams(0, -1, 0.55f);
        obj1.setLayoutParams(obj1Params);
        rowLayout.addView(obj1);

        // Create a LinearLayout for the EditText and unit name
        LinearLayout obj2row = new LinearLayout(getActivity());
        obj2row.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams obj2rowParams = new LinearLayout.LayoutParams(topx(200), -1);
        obj2row.setLayoutParams(obj2rowParams);

        // Create and configure the EditText for the unit number
        EditText obj2 = new EditText(getActivity());
        obj2.setGravity(Gravity.CENTER);
        obj2.setText(unitnum);
        obj2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        obj2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        obj2.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));

        // Generate a unique ID for the EditText and store it in the map
        int id = rowCount++;
        obj2.setId(id);
        rowMap.put(id, obj2);

        LinearLayout.LayoutParams obj2Params = new LinearLayout.LayoutParams(topx(80), -1);
        obj2.setLayoutParams(obj2Params);
        obj2row.addView(obj2);

        // Create and configure the TextView for the unit name
        TextView obj3 = new TextView(getActivity());
        obj3.setText(unitname);
        obj3.setGravity(Gravity.CENTER);
        obj3.setTextSize(17);
        obj3.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));

        LinearLayout.LayoutParams obj3Params = new LinearLayout.LayoutParams(-1, -1);
        obj3.setLayoutParams(obj3Params);
        obj2row.addView(obj3);

        // Add the EditText and unit name TextView to the row layout
        rowLayout.addView(obj2row);

        // Add the row layout to the parent LinearLayout
        subLL.addView(rowLayout);
    }

    // Method to set text for an EditText by its ID
    private void setText(int id, String text) {
        EditText editText = rowMap.get(id);
        if (editText != null) {
            editText.setText(text);
        } else {
            Toast.makeText(getActivity(), "EditText with ID " + id + " not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to read a file and return a list of Comp objects
    private ArrayList<Comp> readFile(String fName) {
        String line;
        String[] items;
        ArrayList<Comp> tempList = new ArrayList<>();

        try {
            File load = new File(dir, fName + ".txt");
            Scanner s = new Scanner(load);

            while (s.hasNextLine()) {
                line = s.nextLine();
                items = line.split("\\|");
                tempList.add(new Comp(items[0], Float.parseFloat(items[1]), items[2]));
            }

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempList;
    }

    // Method to hide the keyboard
    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
