package com.mikonski.happa.utility;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikonski.happa.Models.EventCLick;
import com.mikonski.happa.Models.User;
import com.mikonski.happa.R;
import com.mikonski.happa.activities.singlePostActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.eventholder> {
    private List<EventCLick> eventList;
    private Context context;



    public RecyclerAdapter(List<EventCLick> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;


    }


    @NonNull
    @Override
    public eventholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_view,parent,false);
        return new eventholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final eventholder holder, int position) {
        //animation
        final EventCLick event = eventList.get(position);
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, singlePostActivity.class);
                intent.putExtra("id",event.getId());
                context.startActivity(intent);


            }
        });

        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate());
        Picasso.get().load(event.getImage()).placeholder(R.drawable.place_marker_26px).into(holder.imageView);


        /*
        get username and pic from User
         */

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = firebaseFirestore.collection("User").document(event.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                holder.username.setText(user.getUsername());
                Picasso.get().load(user.getImage()).placeholder(R.drawable.administrator_male_26px).fit().centerCrop().into(holder.profile);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class eventholder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView username,date,title;
        ImageView imageView;
        CardView cardView;
         public eventholder(@NonNull View itemView) {
             super(itemView);

             profile = itemView.findViewById(R.id.profile);
             username = itemView.findViewById(R.id.username);
             date = itemView.findViewById(R.id.date);
             title = itemView.findViewById(R.id.title_tv);
             imageView = itemView.findViewById(R.id.event_image);
             cardView = itemView.findViewById(R.id.card);




         }
     }
}

