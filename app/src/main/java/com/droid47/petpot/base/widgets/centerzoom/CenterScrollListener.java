package com.droid47.petpot.base.widgets.centerzoom;

import androidx.recyclerview.widget.RecyclerView;

public class CenterScrollListener extends RecyclerView.OnScrollListener{
    private boolean mAutoSet = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(!(layoutManager instanceof CircleLayoutManager) && !(layoutManager instanceof ScrollZoomLayoutManager)){
            mAutoSet = true;
            return;
        }

        if(!mAutoSet){
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                final int dx;
                if(layoutManager instanceof CircleLayoutManager){
                    dx = ((CircleLayoutManager) layoutManager).getOffsetCenterView();
                }else{
                    dx = ((ScrollZoomLayoutManager)layoutManager).getOffsetCenterView();
                }
                recyclerView.smoothScrollBy(dx,0);
            }
            mAutoSet = true;
        }
        if(newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING){
            mAutoSet = false;
        }
    }
}
