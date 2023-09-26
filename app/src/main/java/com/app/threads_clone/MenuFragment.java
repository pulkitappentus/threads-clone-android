package com.app.threads_clone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import com.balysv.materialripple.MaterialRippleLayout;
import com.app.threads_clone.app.App;
import com.app.threads_clone.constants.Constants;

public class MenuFragment extends Fragment implements Constants {
    ImageLoader imageLoader;
    private ImageView mFriendsIcon, mGuestsIcon;
    private ImageView mNavGalleryIcon, mNavGroupsIcon, mNavFriendsIcon, mNavGuestsIcon, mNavMarketIcon, mNavNearbyIcon, mNavFavoritesIcon, mNavStreamIcon, mNavPopularIcon, mNavUpgradesIcon, mNavSettingsIcon;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        imageLoader = App.getInstance().getImageLoader();
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        getActivity().setTitle(R.string.nav_menu);

        MaterialRippleLayout mNavGallery = (MaterialRippleLayout) rootView.findViewById(R.id.rplGallery);
        MaterialRippleLayout mNavGroups = (MaterialRippleLayout) rootView.findViewById(R.id.rplCommunities);
        MaterialRippleLayout mNavFriends = (MaterialRippleLayout) rootView.findViewById(R.id.rplFriends);
        MaterialRippleLayout mNavGuests = (MaterialRippleLayout) rootView.findViewById(R.id.rplGuests);
        MaterialRippleLayout mNavMarket = (MaterialRippleLayout) rootView.findViewById(R.id.rplMarketPlace);
      //  mNavNearby = (CardView) rootView.findViewById(R.id.nav_nearby);
        MaterialRippleLayout mNavNearby = (MaterialRippleLayout) rootView.findViewById(R.id.rpPeopleNearBy);
        MaterialRippleLayout mNavFavorites = (MaterialRippleLayout) rootView.findViewById(R.id.rplFavorite);
        MaterialRippleLayout mNavStream = (MaterialRippleLayout) rootView.findViewById(R.id.rplStream);
        MaterialRippleLayout mNavPopular = (MaterialRippleLayout) rootView.findViewById(R.id.rplPopular);
        MaterialRippleLayout mNavUpgrades = (MaterialRippleLayout) rootView.findViewById(R.id.rplUpgrades);
        MaterialRippleLayout mNavSettings = (MaterialRippleLayout) rootView.findViewById(R.id.rplSetting);

        // Counters

        // Icons

        mNavGalleryIcon = (ImageView) rootView.findViewById(R.id.nav_gallery_icon);
        mNavGroupsIcon = (ImageView) rootView.findViewById(R.id.nav_groups_icon);
        mNavFriendsIcon = (ImageView) rootView.findViewById(R.id.nav_friends_icon);
        mNavGuestsIcon = (ImageView) rootView.findViewById(R.id.nav_guests_icon);
        mNavMarketIcon = (ImageView) rootView.findViewById(R.id.nav_market_icon);
        mNavNearbyIcon = (ImageView) rootView.findViewById(R.id.nav_nearby_icon);
        mNavFavoritesIcon = (ImageView) rootView.findViewById(R.id.nav_favorites_icon);
        mNavStreamIcon = (ImageView) rootView.findViewById(R.id.nav_stream_icon);
        mNavPopularIcon = (ImageView) rootView.findViewById(R.id.nav_popular_icon);
        mNavUpgradesIcon = (ImageView) rootView.findViewById(R.id.nav_upgrades_icon);
        mNavSettingsIcon = (ImageView) rootView.findViewById(R.id.nav_settings_icon);

        if (!MARKETPLACE_FEATURE) {

            mNavMarket.setVisibility(View.GONE);
        }

        if (!UPGRADES_FEATURE) {
            mNavUpgrades.setVisibility(View.GONE);
        }

        mNavGallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    animateIcon(mNavGalleryIcon);
                }
                return false;
            }
        });

        mNavGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GalleryActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                getActivity().startActivity(i);
            }
        });

        mNavGroups.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavGroupsIcon);
                }

                return false;
            }
        });

        mNavGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GroupsActivity.class);
                startActivity(i);
            }
        });

        mNavFriends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavFriendsIcon);
                }

                return false;
            }
        });

        mNavFriends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), FriendsActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                startActivity(i);
            }
        });

        mNavGuests.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavGuestsIcon);
                }

                return false;
            }
        });

        mNavGuests.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), GuestsActivity.class);
                startActivity(i);
            }
        });

        mNavMarket.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavMarketIcon);
                }

                return false;
            }
        });

        mNavMarket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), MarketActivity.class);
                startActivity(i);
            }
        });

        mNavNearby.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavNearbyIcon);
                }

                return false;
            }
        });

        mNavNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NearbyActivity.class);
                startActivity(i);
            }
        });

        mNavFavorites.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavFavoritesIcon);
                }

                return false;
            }
        });

        mNavFavorites.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), FavoritesActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                startActivity(i);
            }
        });

        mNavStream.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavStreamIcon);
                }

                return false;
            }
        });

        mNavStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), StreamActivity.class);
                startActivity(i);

            }
        });

        mNavPopular.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavPopularIcon);
                }

                return false;
            }
        });

        mNavPopular.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), PopularActivity.class);
                startActivity(i);
            }
        });

        mNavUpgrades.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavUpgradesIcon);
                }

                return false;
            }
        });

        mNavUpgrades.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UpgradesActivity.class);
                startActivity(i);
            }
        });

        mNavSettings.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mNavSettingsIcon);
                }

                return false;
            }
        });

        mNavSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        updateView();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateView() {

        // Counters

        if (App.getInstance().getNewFriendsCount() != 0) {

            //mFriendsIcon.setVisibility(View.VISIBLE);
        }

        if (App.getInstance().getGuestsCount() != 0) {

           // mGuestsIcon.setVisibility(View.VISIBLE);
        }
    }

    private void animateIcon(ImageView icon) {

        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
    }

    @Override
    public void onResume() {

        super.onResume();

        updateView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}