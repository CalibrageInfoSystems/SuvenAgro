package com.cis.palm360.palmgrow.SuvenAgro.kras;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by siva on 09/09/17.
 */

public abstract class ViewBinder<T extends IViewBinderProvider, VH extends RecyclerView.ViewHolder> implements IViewBinder<T, VH> {
    public abstract VH provideViewHolder(View itemView);

    public abstract void bindView(StickyHeaderViewAdapter adapter, VH holder, int position, T entity, AppCompatActivity activity);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View rootView) {
            super(rootView);
        }

        protected <T extends View> T findViewById(@IdRes int id) {
            return (T) itemView.findViewById(id);
        }
    }

}
