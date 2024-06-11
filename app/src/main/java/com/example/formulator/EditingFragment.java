package com.example.formulator;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.formulator.myClass.Comp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class EditingFragment extends Fragment {
    EditText editingNameBox, editingUnitNumberBox, editingUnitNameBox;
    LinearLayout editingLL;
    TextView editingTitle, editingAddBtn;
    Button editingSaveBtn;
    File dir;
    String fileName;
    ArrayList<Comp> compList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_editing, container, false);

        // Initialize UI elements
        editingLL = root.findViewById(R.id.editingLL);
        editingTitle = root.findViewById(R.id.editingTitle);
        editingAddBtn = root.findViewById(R.id.editingAddBtn);
        editingSaveBtn = root.findViewById(R.id.editingSaveBtn);
        editingNameBox = root.findViewById(R.id.editingNameBox);
        editingUnitNumberBox = root.findViewById(R.id.editingUnitNumberBox);
        editingUnitNameBox = root.findViewById(R.id.editingUnitNameBox);

        // Get the directory for "Formula Files" in external storage
        dir = getActivity().getExternalFilesDir("Formula Files");

        // Retrieve the file name from the arguments
        fileName = getArguments().getString("fileName");
        editingTitle.setText("Editing " + fileName.toUpperCase() + " :");

        // Read the file and load components
        compList = readFile(fileName);
        for (int i = 0; i < compList.size(); i++) {
            String UwUn = compList.get(i).getUnit() + " " + compList.get(i).getUnitName();
            insertRow(compList.get(i).getName(), UwUn, compList.get(i));
        }

        // Set onClickListener for adding a new component
        editingAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String InputName = editingNameBox.getText().toString();
                String InputUnit = editingUnitNumberBox.getText().toString();
                String InputUnitName = editingUnitNameBox.getText().toString();

                if (InputName.equals("") || InputUnit.equals("") || InputUnit.equals(".") || InputUnitName.equals("")) {
                    Toast.makeText(getActivity(), "fill all box", Toast.LENGTH_SHORT).show();
                } else {
                    float unit = Float.parseFloat(InputUnit);
                    Comp temp = new Comp(InputName, unit, InputUnitName);
                    compList.add(temp);
                    String UwUn = InputUnit + " " + InputUnitName;
                    insertRow(InputName, UwUn, temp);
                }
            }
        });

        // Set onClickListener for saving the components to the file
        editingSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write("", false);
                for (int i = 0; i < compList.size(); i++) {
                    String OP = compList.get(i).getName() + "|" + compList.get(i).getUnit() + "|" + compList.get(i).getUnitName() + "\n";
                    write(OP, true);
                }

                // Navigate back to SubjectFragment after saving
                Fragment ldf = new SubjectFragment();
                Bundle args = new Bundle();
                args.putString("fileName", fileName);
                ldf.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FLayout, ldf, "edit");
                fragmentTransaction.commit();
                hideKeyboard();

                Toast.makeText(getActivity(), "saved successfully", Toast.LENGTH_SHORT).show();
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

    // Method to insert a row with a given name and unit into the LinearLayout
    private void insertRow(String name, String unit, Comp del) {
        LinearLayout LL = new LinearLayout(getActivity());
        TextView delete = new TextView(getActivity());
        ConstraintLayout CL = new ConstraintLayout(getActivity());
        TextView nameView = new TextView(getActivity());
        TextView unitView = new TextView(getActivity());

        // Configure LinearLayout
        LL.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        LL.setMinimumHeight(topx(60));
        LL.setPadding(0, topx(10), 0, 0);

        // Configure delete TextView
        delete.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        delete.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        delete.setGravity(Gravity.CENTER);
        delete.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f);
        delete.setText("×");
        delete.setPadding(0, 0, 0, 0);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) LL.getParent()).removeView(LL);
                compList.remove(del);
            }
        });

        // Configure ConstraintLayout
        CL.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.white_stroke));
        CL.setPadding(topx(15), 0, topx(15), 0);

        // Set layout parameters
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(topx(50), -1);
        deleteParams.setMargins(0, 0, topx(5), 0);

        ConstraintLayout.LayoutParams nameParams = new ConstraintLayout.LayoutParams(-2, -1);
        ConstraintLayout.LayoutParams unitParams = new ConstraintLayout.LayoutParams(-2, -1);
        unitParams.rightToRight = 0;

        // Configure name TextView
        nameView.setSingleLine(true);
        nameView.setGravity(Gravity.CENTER_VERTICAL);
        nameView.setText(name);
        nameView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);

        // Configure unit TextView
        unitView.setSingleLine(true);
        unitView.setGravity(Gravity.CENTER_VERTICAL);
        unitView.setText(unit);
        unitView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        unitView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);

        // Add views to the ConstraintLayout and LinearLayout
        CL.addView(nameView, nameParams);
        CL.addView(unitView, unitParams);
        LL.addView(delete, deleteParams);
        LL.addView(CL, -1, -1);
        editingLL.addView(LL);

        // Clear input fields
        editingNameBox.setText("");
        editingUnitNumberBox.setText("");
    }

    // Method to write text to a file
    public void write(String txt, boolean append) {
        File temp = new File(dir, fileName + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(temp, append);
            fos.write(txt.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read components from a file
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
