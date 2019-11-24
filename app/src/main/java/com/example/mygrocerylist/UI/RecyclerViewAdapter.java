package com.example.mygrocerylist.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.Activities.DetailsActivity;
import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Grocery grocery = groceryItems.get(position);

        holder.groceryItemName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAded.setText(grocery.getDateItemAdded());

    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            groceryItemName = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.quantity);
            dateAded = view.findViewById(R.id.dateAdded);

            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen
                    int position = getAdapterPosition();

                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);

                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("id", grocery.getId());
                    intent.putExtra("date", grocery.getDateItemAdded());
                    context.startActivity(intent);
                }
            });
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editButton:
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    editItem(grocery);
                    break;

                case R.id.deleteButton:
                    position = getAdapterPosition();
                    grocery = groceryItems.get(position);
                    deleteItem(grocery.getId());
                    break;
            }
        }

        public void deleteItem(final int id) {
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = view.findViewById(R.id.noButton);
            Button yesButton = view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteGrocery(id);
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();
                }
            });
        }

        public void editItem(final Grocery grocery) {
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            final EditText groceryItem = view.findViewById(R.id.groceryItem);
            final EditText quantity = view.findViewById(R.id.groceryQty);
            TextView title = view.findViewById(R.id.title);

            title.setText("Edit Grocery");
            Button saveButton = view.findViewById(R.id.saveButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = new DatabaseHandler(context);

                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(quantity.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty()
                            && !quantity.getText().toString().isEmpty()){
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);
                    }else
                    {
                        Snackbar.make(view,"Add Grocery and Quantity", Snackbar.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                }
            });
        }
    }
}
