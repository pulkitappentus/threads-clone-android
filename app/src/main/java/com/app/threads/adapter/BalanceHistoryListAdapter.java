package com.app.threads.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import com.app.threads.R;
import com.app.threads.constants.Constants;
import com.app.threads.model.BalanceItem;
import com.app.threads.model.Chat;

public class BalanceHistoryListAdapter extends RecyclerView.Adapter<BalanceHistoryListAdapter.ViewHolder> implements Constants {

    private final Context ctx;
    private final List<BalanceItem> items;

    public interface OnItemClickListener {

        void onItemClick(View view, BalanceItem item, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView credits, time;
        public LinearLayout parent;
        public EmojiconTextView message;
        ImageView ivCreditDebit;

        public ViewHolder(View view) {

            super(view);

            credits = (TextView) view.findViewById(R.id.credits);
            message = (EmojiconTextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
            parent = (LinearLayout) view.findViewById(R.id.parent);
            ivCreditDebit = (ImageView) view.findViewById(R.id.ivCreditDebit);
        }
    }

    public BalanceHistoryListAdapter(Context mContext, List<BalanceItem> items) {

        this.ctx = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance_history_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BalanceItem item = items.get(position);

        if (item.getPaymentAction() == PA_BUY_CREDITS || item.getPaymentAction() == PA_BUY_REGISTRATION_BONUS || item.getPaymentAction() == PA_BUY_REFERRAL_BONUS) {
            holder.credits.setText("+" + item.getCreditsCount() + " " + ctx.getString(R.string.label_credits));
            holder.ivCreditDebit.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_credit));
        } else {
            holder.credits.setText("-" + item.getCreditsCount() + " " + ctx.getString(R.string.label_credits));
            holder.ivCreditDebit.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_debit));
        }

        switch (item.getPaymentAction()) {
            case PA_BUY_CREDITS: {
                switch (item.getPaymentType()) {

                    case PT_CARD: {
                        holder.message.setText(ctx.getString(R.string.label_payments_credits_stripe));
                        break;
                    }

                    case PT_GOOGLE_PURCHASE: {

                        holder.message.setText(ctx.getString(R.string.label_payments_credits_android));

                        break;
                    }

                    case PT_APPLE_PURCHASE: {

                        holder.message.setText(ctx.getString(R.string.label_payments_credits_ios));

                        break;
                    }

                    case PT_ADMOB_REWARDED_ADS: {

                        holder.message.setText(ctx.getString(R.string.label_payments_credits_admob));

                        break;
                    }
                }

                break;
            }

            case PA_BUY_GIFT: {

                holder.message.setText(ctx.getString(R.string.label_payments_send_gift));

                break;
            }

            case PA_BUY_VERIFIED_BADGE: {

                holder.message.setText(ctx.getString(R.string.label_payments_verified_badge));

                break;
            }

            case PA_BUY_GHOST_MODE: {

                holder.message.setText(ctx.getString(R.string.label_payments_ghost_mode));

                break;
            }

            case PA_BUY_DISABLE_ADS: {

                holder.message.setText(ctx.getString(R.string.label_payments_off_admob));

                break;
            }

            case PA_BUY_REGISTRATION_BONUS: {

                holder.message.setText(ctx.getString(R.string.label_payments_registration_bonus));

                break;
            }

            case PA_BUY_REFERRAL_BONUS: {

                holder.message.setText(ctx.getString(R.string.label_payments_referral_bonus));

                break;
            }
        }

        holder.time.setText(item.getDate());
    }

    public BalanceItem getItem(int position) {

        return items.get(position);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public interface OnClickListener {

        void onItemClick(View view, Chat item, int pos);
    }
}