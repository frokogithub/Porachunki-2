package com.porachunki;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;



public class DeleteDialog {

    private OnDeleteClickListener deleteClickListener;


    public DeleteDialog(Activity activity, final int position) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_del, viewGroup, false);



        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        final AlertDialog saveDialog = builder.create();
        saveDialog.show();

        Button btDelete = dialogView.findViewById(R.id.bt_delete);
        Button btCancel = dialogView.findViewById(R.id.bt_cancel);

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog.dismiss();
                if(deleteClickListener!=null) deleteClickListener.onDeleteClick(position);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog.dismiss();

            }
        });
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener){
        deleteClickListener = listener;
    }
}
