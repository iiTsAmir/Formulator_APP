package com.example.formulator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ManagingFragment extends Fragment {

    // Declare UI elements
    LinearLayout managingLL;
    EditText managingNameBox;
    TextView managingAddBtn;
    File dir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_managing, container, false);

        // Initialize UI elements
        managingLL = root.findViewById(R.id.managingLL);
        managingNameBox = root.findViewById(R.id.managingNameBox);
        managingAddBtn = root.findViewById(R.id.managingAddBtn);

        // Get the directory for "Formula Files" in external storage
        dir = getActivity().getExternalFilesDir("Formula Files");
        File[] filesArr = dir.listFiles();
        for (int i = 0; i < filesArr.length; i++) {
            String fileName = filesArr[i].getName();
            String name = fileName.substring(0, fileName.length() - 4);
            insertRow(name);
        }

        // Set onClickListener for managingAddBtn to create a new file and insert a new row
        managingAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String IName = managingNameBox.getText().toString();
                if (!IName.isEmpty()) {
                    boolean exs = fileCreate(IName);
                    if (!exs) {
                        insertRow(IName);
                        hideKeyboard();
                    }
                } else {
                    Toast.makeText(getActivity(), "Enter Formula name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    // Method to create a new file
    public boolean fileCreate(String fileName) {
        File temp = new File(dir, fileName + ".txt");
        boolean ex = temp.exists();
        if (ex) {
            Toast.makeText(getActivity(), fileName + " file is already exists", Toast.LENGTH_SHORT).show();
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(temp);
                fos.write("".getBytes());
                fos.close();
                Toast.makeText(getActivity(), fileName + " file created", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ex;
    }

    // Method to delete a file
    public void fileDelete(String fileName) {
        File temp = new File(dir, fileName + ".txt");
        boolean deleted = temp.delete();
        if (deleted) {
            Toast.makeText(getActivity(), fileName + " file successfully deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), fileName + " file not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert dp to px
    private int topx(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    // Method to insert a row with a given name into the LinearLayout
    private void insertRow(String name) {
        // Create a new LinearLayout for the row
        LinearLayout newLL = new LinearLayout(getActivity());
        TextView txtView = new TextView(getActivity());
        TextView delete = new TextView(getActivity());

        newLL.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        newLL.setMinimumHeight(topx(60));
        newLL.setPadding(0, topx(10), 0, 0);

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(topx(50), -1);
        deleteParams.setMargins(0, 0, topx(5), 0);

        // Create and configure the delete button
        delete.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        delete.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        delete.setGravity(Gravity.CENTER);
        delete.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f);
        delete.setText("×");
        delete.setPadding(0, 0, 0, 0);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle)
                        .setTitle("Warning")
                        .setMessage("Are you sure?!\nYou want to delete " + name + " ?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((ViewGroup) newLL.getParent()).removeView(newLL);
                                fileDelete(name);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // Create and configure the TextView for the formula name
        txtView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.white_stroke));
        txtView.setPadding(topx(15), 0, 0, 0);
        txtView.setSingleLine(true);
        txtView.setGravity(Gravity.CENTER_VERTICAL);
        txtView.setText(name);
        txtView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_green));
        txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);
        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment ldf = new EditingFragment();
                Bundle args = new Bundle();
                args.putString("fileName", name);
                ldf.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.FLayout, ldf, "edit");
                fragmentTransaction.commit();
            }
        });

        // Add the delete button and TextView to the row layout
        newLL.addView(delete, deleteParams);
        newLL.addView(txtView, -1, -1);
        managingLL.addView(newLL);
        managingNameBox.setText("");
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
