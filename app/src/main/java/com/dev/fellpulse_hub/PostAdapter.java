package com.dev.fellpulse_hub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Crea una nueva vista para un item de la lista, usando el "molde" item_post.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // Obtiene el post de la lista en la posición actual
        Post post = postList.get(position);
        // "Pinta" los datos del post en la vista
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Esta clase interna representa la vista de un solo item de la lista
    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvPostTitle;
        private TextView tvPostContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
        }

        public void bind(Post post) {
            tvUserName.setText(post.getUserName());
            tvPostTitle.setText(post.getTitle());
            tvPostContent.setText(post.getContent());
        }
    }
}
