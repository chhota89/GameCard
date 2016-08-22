package com.gamecard.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gamecard.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {


    private final static String IMAGE_URL="IMAGE_URL";
    private String imageUrl;
  //  static ProgressDialog progressDialog;
    private ProgressBar progressBar1;

    public ImageFragment() {
        // Required empty public constructor

    }

    public static ImageFragment newInstance(String url){
        ImageFragment imageFragment=new ImageFragment();
        Bundle bundle=new Bundle();
        bundle.putString(IMAGE_URL,url);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        imageUrl=bundle.getString(IMAGE_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_image_layout, container, false);
        ImageView appImage=(ImageView)view.findViewById(R.id.appImage);
    //    progressDialog=new ProgressDialog(view.getContext());
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar1.setVisibility(View.GONE);

        //progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_animation));
      /*  String TAG="ImageUrl";
        Log.i(TAG, "ImageUrl:................ "+imageUrl);
*/
        Glide.with(view.getContext()).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
           //     progressDialog.hide();
                progressBar1.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            //    progressDialog.hide();
                progressBar1.setVisibility(View.GONE);
                return false;
            }
        }).into(appImage);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
     //   progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
     //   progressDialog.show();
        progressBar1.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

     //   if(progressDialog!=null && progressDialog.isShowing())
       //     progressDialog.hide();
        progressBar1.setVisibility(View.GONE);
    }
}
