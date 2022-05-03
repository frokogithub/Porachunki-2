package com.porachunki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 03-Jan-17.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RowData> rowDataArrayList;


    public CustomAdapter(Context context, ArrayList<RowData> rowDataArrayList) {

        this.context = context;
        this.rowDataArrayList = rowDataArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return rowDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return rowDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvRowDate = convertView.findViewById(R.id.tv_row_date);
            holder.tvRowTotal = convertView.findViewById(R.id.tv_row_total);
            holder.tvRowDescription = convertView.findViewById(R.id.tv_row_description);
            holder.clRowParent = convertView.findViewById(R.id.cl_row_parent);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        DateHelper dh = new DateHelper();
        holder.tvRowDate.setText(dh.dateToCharString(rowDataArrayList.get(position).getDate()));
        String bill = String.format("%.02f",rowDataArrayList.get(position).getBill())+" zł";
        holder.tvRowTotal.setText(bill);

        //wyświetla tylko pierwszy wyraz
        String descriptionFull = rowDataArrayList.get(position).getDescription();
        String[] desc = descriptionFull.split(" ");
        holder.tvRowDescription.setText(desc[0]);


        // zaznacza wiersz na wskazanej pozycji
        if (position == HistoryActivity.forceCheckedPosition){
            holder.clRowParent.setBackgroundColor(context.getResources().getColor(R.color.selected_row));
        }else{
            holder.clRowParent.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }

        return convertView;
    }


    private class ViewHolder {

        protected TextView tvRowDate;
        protected TextView tvRowTotal;
        protected TextView tvRowDescription;
        protected ConstraintLayout clRowParent;
    }

}