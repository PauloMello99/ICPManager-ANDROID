package br.com.fatec.icpmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.Helper;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private int selectedOption = -1;
    public static int FILTER = 0;
    public static int REMOVE = 1;

    private List<Student> students;
    private List<Upload> uploads;
    private Context context;
    private RecyclerViewClickListener itemListener;

    public StudentAdapter(List<Student> students, List<Upload> uploads,
                          Context context, RecyclerViewClickListener itemListener, int TAG) {
        this.students = students;
        this.uploads = uploads;
        this.context = context;
        this.itemListener = itemListener;
        if (TAG == 1 || TAG == 0) selectedOption = TAG;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (selectedOption) {
            case 0:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_project, parent, false);
                break;
            case 1:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_round, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_user, parent, false);
                break;
        }

        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder viewHolder, int i) {
        Upload upload = uploads.get(i);
        Student student = students.get(i);
        if (selectedOption == FILTER) viewHolder.infoTextView.setText(student.getEmail());

        viewHolder.nameTextView.setText(Helper.cutName(student.getName(), 0,2));

        if (!upload.getPhoto().equals("null"))
            Picasso.get()
                    .load(upload.getPhoto())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(viewHolder.photoView);

        if(itemListener!=null)
            viewHolder.itemView.setOnClickListener(v -> itemListener.recyclerViewListClicked(v, i, selectedOption,"STUDENT"));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;
        TextView nameTextView;
        TextView infoTextView;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.card_photo);
            nameTextView = itemView.findViewById(R.id.card_title);
            infoTextView = itemView.findViewById(R.id.card_desc);
        }
    }
}
