package com.gamecard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gamecard.R;
import com.gamecard.model.VideoDisplayModel;
import com.gamecard.view.YouTubeFragment;
import com.gamecard.viewholder.GameInfoViewHoldr;
import com.gamecard.viewholder.VideoDisplayViewHolder;

import java.util.List;

/**
 * Created by bridgeit on 29/8/16.
 */

public class AdapterVideoDisplay extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIDEO_TYPE = 0;
    private static final int VIDEO_DISPLAY_TYPE = 1;
    private static final String TAG = AdapterVideoDisplay.class.getSimpleName();
    private List<VideoDisplayModel> videos2;
    private Context context;
    private String sourceDir;
    private CharSequence loadLabel;

    public AdapterVideoDisplay(Context context, List<VideoDisplayModel> videos2,
                               String sourceDir, CharSequence loadLabel){

        this.videos2 = videos2;
        this.context = context;
        this.sourceDir=sourceDir;
        this.loadLabel=loadLabel;
    }

    @Override
    public int getItemCount() {

        return videos2.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch(position) {
            case 0:
                return VIDEO_TYPE;
            default:
                return VIDEO_DISPLAY_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIDEO_TYPE) {

            View viewUserInfo = inflater.inflate(R.layout.activity_app_description, parent, false);
            return new GameInfoViewHoldr(viewUserInfo);
        }
        else {
            View viewUserInfo = inflater.inflate(R.layout.activity_show_video_details, parent, false);
            return new VideoDisplayViewHolder(viewUserInfo);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {
            if (holder.getItemViewType() == VIDEO_TYPE) {

               /* GameInfoViewHoldr holder1 = (GameInfoViewHoldr) holder;
                setGameName(holder1.appName,((GameResponseModel) videos1.get(position)).getGametittle());
                Glide.with(context).load(((GameResponseModel) videos1.get(position)).getIconLink()).into(holder1.appImage);*/

            }
            else{

                VideoDisplayViewHolder holder2 = (VideoDisplayViewHolder) holder;

             /*   holder2.videoDisplay.setVideoURI(Uri.parse(videos2.get(position).getVedioLink()));
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder2.videoDisplay);
                holder2.videoDisplay.setMediaController(mediaController);
                holder2.videoDisplay.start();*/

                YouTubeFragment.newInstance(videos2.get(position).getVedioLink(), sourceDir, loadLabel);

             /*   View view = holder2.videoDisplay;
                if(view.getVisibility() == View.VISIBLE){
                    Animation out = AnimationUtils.makeOutAnimation(context,true);
                    view.startAnimation(out);
                    view.setVisibility(View.INVISIBLE);
                } else {
                    Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    view.startAnimation(in);
                    view.setVisibility(View.VISIBLE);
                }
*/
                setGameName(holder2.gameTitle, videos2.get(position).getGameTitle());

                setGameName(holder2.packageName, videos2.get(position).getPackageName());

                Glide.with(context).load(videos2.get(position).getIconLink()).into(holder2.iconImage);

                holder2.apkDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String apklink = (String) videos2.get(position).getApkLink();
                        String inputApk = apklink.replace(" ","%20");
                        String title = videos2.get(position).getGameTitle();
                        String inputTitle = title.replace(" ","_");
                        String inputTitle1 = inputTitle.trim();
                    }
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setGameName(TextView textView, String text){

        textView.setText(text);
    }
}