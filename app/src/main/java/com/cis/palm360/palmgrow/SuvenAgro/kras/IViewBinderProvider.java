package com.cis.palm360.palmgrow.SuvenAgro.kras;


import androidx.collection.SparseArrayCompat;

/**
 * Created by siva on 09/09/17.
 */

public interface IViewBinderProvider {
    IViewBinder provideViewBinder(StickyHeaderViewAdapter adapter, SparseArrayCompat<? extends IViewBinder> viewBinderPool, int position);
}
