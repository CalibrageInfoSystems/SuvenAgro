package com.cis.palm360.palmgrow.SuvenAgro.conversion;

/**
 * Created by Siva on 28/11/16.
 */
public interface PalmDetailsEditListener {
    void onEditClicked(int position);

    void loadComplete(int nbPages);

    void onPageChanged(int page, int pageCount);
}
