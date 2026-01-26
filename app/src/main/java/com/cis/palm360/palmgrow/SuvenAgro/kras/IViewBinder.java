package com.cis.palm360.palmgrow.SuvenAgro.kras;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by siva on 09/09/17.
 */

public interface IViewBinder<T, VH> extends LayoutItemType {
    VH provideViewHolder(View itemView);
    void bindView(StickyHeaderViewAdapter adapter, VH holder, int position, T entity, AppCompatActivity activity);
}
