package com.app.threads_clone.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.threads_clone.ProfileActivity;
import com.app.threads_clone.R;
import com.app.threads_clone.app.App;
import com.app.threads_clone.constants.Constants;
import com.app.threads_clone.model.BlacklistItem;
import com.app.threads_clone.util.BlacklistItemInterface;
import com.app.threads_clone.util.CustomRequest;

public class BlackListAdapter extends BaseAdapter implements Constants {

	private final Activity activity;
	private LayoutInflater inflater;
	private final List<BlacklistItem> blackList;

    private final BlacklistItemInterface responder;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public BlackListAdapter(Activity activity, List<BlacklistItem> blackList, BlacklistItemInterface responder) {

		this.activity = activity;
		this.blackList = blackList;
        this.responder = responder;
	}

	@Override
	public int getCount() {

		return blackList.size();
	}

	@Override
	public Object getItem(int location) {

		return blackList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView mBlockedUserFullname;
        public TextView mBlockedTimeAgo;
        public TextView mBlockedReason;
        public Button mBlockedAction;
		public CircularImageView mBlockedUser;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.blacklist_row, null);
			
			viewHolder = new ViewHolder();
			viewHolder.mBlockedUser = (CircularImageView) convertView.findViewById(R.id.blockedUser);
            viewHolder.mBlockedUserFullname = (TextView) convertView.findViewById(R.id.blockedUserFullname);
			viewHolder.mBlockedReason = (TextView) convertView.findViewById(R.id.blockedReason);
            viewHolder.mBlockedTimeAgo = (TextView) convertView.findViewById(R.id.blockedTimeAgo);
            viewHolder.mBlockedAction = (Button) convertView.findViewById(R.id.blockedAction);

            convertView.setTag(viewHolder);

            viewHolder.mBlockedUser.setOnClickListener(v -> {

                int getPosition = (Integer) v.getTag();

                BlacklistItem item = blackList.get(getPosition);

                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("profileId", item.getBlockedUserId());
                activity.startActivity(intent);
            });

            viewHolder.mBlockedUserFullname.setOnClickListener(v -> {

                int getPosition = (Integer) v.getTag();

                BlacklistItem item = blackList.get(getPosition);

                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("profileId", item.getBlockedUserId());
                activity.startActivity(intent);
            });

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.mBlockedTimeAgo.setTag(position);
        viewHolder.mBlockedReason.setTag(position);
        viewHolder.mBlockedUserFullname.setTag(position);
        viewHolder.mBlockedAction.setTag(position);
        viewHolder.mBlockedUser.setTag(position);
        viewHolder.mBlockedUser.setTag(R.id.blockedUser, viewHolder);
		
		final BlacklistItem item = blackList.get(position);

        viewHolder.mBlockedUserFullname.setText(item.getBlockedUserFullname());

        if (item.getBlockedUserVerify() != 1) {

            viewHolder.mBlockedUserFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        } else {

            viewHolder.mBlockedUserFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
        }

        if (item.getBlockedUserPhotoUrl().length() > 0) {

            imageLoader.get(item.getBlockedUserPhotoUrl(), ImageLoader.getImageListener(viewHolder.mBlockedUser, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.mBlockedUser.setImageResource(R.drawable.profile_default_photo);
        }

        viewHolder.mBlockedReason.setVisibility(View.GONE);

        viewHolder.mBlockedTimeAgo.setText(item.getTimeAgo());

        viewHolder.mBlockedAction.setOnClickListener(view -> {

            final int getPosition = (Integer) view.getTag();

            if (App.getInstance().isConnected()) {

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_REMOVE, null,
                        response -> {

                            try {

                                if (!response.getBoolean("error")) {


                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                responder.remove(getPosition);
                                notifyDataSetChanged();
                            }
                        }, error -> Toast.makeText(activity.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("profileId", Long.toString(item.getBlockedUserId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);

            } else {

                Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
            }
        });

		return convertView;
	}
}