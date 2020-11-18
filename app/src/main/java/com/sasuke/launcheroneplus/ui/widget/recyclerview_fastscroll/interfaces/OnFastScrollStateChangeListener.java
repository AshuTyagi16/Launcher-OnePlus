package com.sasuke.launcheroneplus.ui.widget.recyclerview_fastscroll.interfaces;

public interface OnFastScrollStateChangeListener {

    /**
     * Called when fast scrolling begins
     */
    void onFastScrollStart();

    /**
     * Called when fast scroller is being dragged
     */
    void onFastScrollDragged(String currentHeader);

    /**
     * Called when fast scrolling ends
     */
    void onFastScrollStop();
}
