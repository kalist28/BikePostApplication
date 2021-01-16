package com.kalistratov.bikepost.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.gps.TrackerActivity;
import com.kalistratov.bikepost.route.RoutesActivity;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TrackerActivity.class));

            }
        });

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                .setTitle("Delete?")
                .setMessage("Are you sure want to delete this file?")
                .setCancelable(false)
                .setPositiveButton("Delete", R.drawable.ic_point, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_point, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RoutesActivity.class));

                //mBottomSheetDialog.show();
            }
        });
    }
}