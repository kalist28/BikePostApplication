package com.kalistratov.bikepost.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.api.Action;
import com.kalistratov.bikepost.api.Server;
import com.kalistratov.bikepost.api.inquiry.PostsListInquiry;
import com.kalistratov.bikepost.entitys.BikePost;
import com.kalistratov.bikepost.entitys.Entity;
import com.kalistratov.bikepost.entitys.EntityList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Class PostsActivities.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class PostsActivity extends AMapActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Server.get().getResponse(new Action<EntityList<BikePost>>() {
            @Override
            public void action(final Response<EntityList<BikePost>> response) {
                Marker marker = null;
                BitmapDescriptor bitmap;
                for (Entity<BikePost> p : response.body().data) {
                    BikePost post = p.getProperty();
                    bitmap = bitmapDescriptorFromVector(
                            PostsActivity.this,
                            R.drawable.ic_point
                    );
                    MarkerOptions options = new MarkerOptions()
                            .position(post.getCoordinates())
                            .title(post.name)
                            .icon(bitmap);
                    marker = map.addMarker(options);

                }

                map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public Call<EntityList<BikePost>> getCall(Retrofit retrofit) {
                return retrofit.create(PostsListInquiry.class).getPosts();
            }
        });
    }

    @Override
    public int viewLayout() {
        return R.layout.activity_map_posts;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
