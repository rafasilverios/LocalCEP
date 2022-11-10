package com.example.viacep.recyclerview;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viacep.databinding.ActivityMainAdapterBinding;
import com.example.viacep.db.MainData;
import com.example.viacep.db.RoomDB;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<MainData> dataList;
    private Activity context;
    private RoomDB database;

    public MainAdapter(Activity context, List<MainData> dataList) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityMainAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainData data = dataList.get(position);
        database = RoomDB.getInstance(context);

        holder.binding.tvGetEndereco.setText(data.getText());
        String adress = holder.binding.tvGetEndereco.getText().toString();

        holder.binding.btnShare.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("EditText", (CharSequence) adress);
            clipboardManager.setPrimaryClip(clip);

            new Handler().postDelayed(() -> {
                String url;
                url = "https://wa.me/?text=" + adress + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }, 400);

            Toast.makeText(v.getContext(), "Endereço Copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            MainData d = dataList.get(holder.getAdapterPosition());
            database.mainDao().delete(d);
            int positions = holder.getAdapterPosition();
            dataList.remove(positions);
            notifyItemRemoved(positions);
            notifyItemRangeChanged(positions,dataList.size());
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityMainAdapterBinding binding;

        public ViewHolder(ActivityMainAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
