package com.origamilabs.library.views;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * modified by Maurycy Wojtowicz
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;

import com.actionbarsherlock.R;





import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListAdapter;

// TODO: Auto-generated Javadoc
/**
 * ListView and GridView just not complex enough? Try StaggeredGridView!
 *
 * <p>StaggeredGridView presents a multi-column grid with consistent column sizes
 * but varying row sizes between the columns. Each successive item from a
 * {@link android.widget.ListAdapter ListAdapter} will be arranged from top to bottom,
 * left to right. The largest vertical gap is always filled first.</p>
 *
 * <p>Item views may span multiple columns as specified by their {@link LayoutParams}.
 * The attribute <code>android:layout_span</code> may be used when inflating
 * item views from xml.</p>
 */
@SuppressLint("Recycle")
public class StaggeredGridView extends ViewGroup {
    
    /** The Constant TAG. */
    private static final String TAG = "StaggeredGridView";

    /*
     * There are a few things you should know if you're going to make modifications
     * to StaggeredGridView.
     *
     * Like ListView, SGV populates from an adapter and recycles views that fall out
     * of the visible boundaries of the grid. A few invariants always hold:
     *
     * - mFirstPosition is the adapter position of the View returned by getChildAt(0).
     * - Any child index can be translated to an adapter position by adding mFirstPosition.
     * - Any adapter position can be translated to a child index by subtracting mFirstPosition.
     * - Views for items in the range [mFirstPosition, mFirstPosition + getChildCount()) are
     *   currently attached to the grid as children. All other adapter positions do not have
     *   active views.
     *
     * This means a few things thanks to the staggered grid's nature. Some views may stay attached
     * long after they have scrolled offscreen if removing and recycling them would result in
     * breaking one of the invariants above.
     *
     * LayoutRecords are used to track data about a particular item's layout after the associated
     * view has been removed. These let positioning and the choice of column for an item
     * remain consistent even though the rules for filling content up vs. filling down vary.
     *
     * Whenever layout parameters for a known LayoutRecord change, other LayoutRecords before
     * or after it may need to be invalidated. e.g. if the item's height or the number
     * of columns it spans changes, all bets for other items in the same direction are off
     * since the cached information no longer applies.
     */

    /** The m adapter. */
    private ListAdapter mAdapter;

    /** The Constant COLUMN_COUNT_AUTO. */
    public static final int COLUMN_COUNT_AUTO = -1;

    /** The m col count setting. */
    private int mColCountSetting = 2;
    
    /** The m col count. */
    private int mColCount = 2;
    
    /** The m min col width. */
    private int mMinColWidth = 0;
    
    /** The m item margin. */
    private int mItemMargin;

    /** The m item tops. */
    private int[] mItemTops;
    
    /** The m item bottoms. */
    private int[] mItemBottoms;

    /** The m fast child layout. */
    private boolean mFastChildLayout;
    
    /** The m populating. */
    private boolean mPopulating;
    
    /** The m in layout. */
    private boolean mInLayout;
    
    /** The m restore offsets. */
    private int[] mRestoreOffsets;

    /** The m recycler. */
    private final RecycleBin mRecycler = new RecycleBin();

    /** The m observer. */
    private final AdapterDataSetObserver mObserver = new AdapterDataSetObserver();

    /** The m data changed. */
    private boolean mDataChanged;
    
    /** The m item count. */
    private int mItemCount;
    
    /** The m has stable ids. */
    private boolean mHasStableIds;

    /** The m first position. */
    private int mFirstPosition;

    /** The m touch slop. */
    private int mTouchSlop;
    
    /** The m maximum velocity. */
    private int mMaximumVelocity;
    
    /** The m fling velocity. */
    private int mFlingVelocity;
    
    /** The m last touch y. */
    private float mLastTouchY;
    
    /** The m last touch x. */
    private float mLastTouchX;
    
    /** The m touch remainder y. */
    private float mTouchRemainderY;
    
    /** The m active pointer id. */
    private int mActivePointerId;
    
    /** The m motion position. */
    private int mMotionPosition;
    
    /** The m col width. */
    private int mColWidth;
    
    /** The m num cols. */
    @SuppressWarnings("unused")
	private int mNumCols;
    
    /** The m first adapter id. */
    private long mFirstAdapterId;
    
    /** The m begin click. */
    private boolean mBeginClick;
    
    /** The Constant TOUCH_MODE_IDLE. */
    private static final int TOUCH_MODE_IDLE = 0;
    
    /** The Constant TOUCH_MODE_DRAGGING. */
    private static final int TOUCH_MODE_DRAGGING = 1;
    
    /** The Constant TOUCH_MODE_FLINGING. */
    private static final int TOUCH_MODE_FLINGING = 2;
	
	/** The Constant TOUCH_MODE_DOWN. */
	private static final int TOUCH_MODE_DOWN = 3;
	
	/** The Constant TOUCH_MODE_TAP. */
	private static final int TOUCH_MODE_TAP = 4;
	
	/** The Constant TOUCH_MODE_DONE_WAITING. */
	private static final int TOUCH_MODE_DONE_WAITING = 5;
	
	/** The Constant TOUCH_MODE_REST. */
	private static final int TOUCH_MODE_REST = 6;
	
	/** The Constant INVALID_POSITION. */
	private static final int INVALID_POSITION = -1;
	
    /** The m touch mode. */
    private int mTouchMode;
    
    /** The m velocity tracker. */
    private final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    
    /** The m scroller. */
    private final ScrollerCompat mScroller;

    /** The m top edge. */
    private final EdgeEffectCompat mTopEdge;
    
    /** The m bottom edge. */
    private final EdgeEffectCompat mBottomEdge;
    
    /** The m col mappings. */
    private ArrayList<ArrayList<Integer>> mColMappings = new ArrayList<ArrayList<Integer>>(); 
    
    /** The m pending check for tap. */
    private Runnable mPendingCheckForTap;
    
    /** The m context menu info. */
    private ContextMenuInfo mContextMenuInfo = null;
    
    /** The drawable used to draw the selector. */
    Drawable mSelector;
    
    /** The m draw selector on top. */
    boolean mDrawSelectorOnTop = false;
    
    /**
     * Delayed action for touch mode.
     */
    private Runnable mTouchModeReset;
    
    /** The selection's left padding. */
    int mSelectionLeftPadding = 0;

    /** The selection's top padding. */
    int mSelectionTopPadding = 0;

    /** The selection's right padding. */
    int mSelectionRightPadding = 0;

    /** The selection's bottom padding. */
    int mSelectionBottomPadding = 0;
    
    /**
     * The select child's view (from the adapter's getView) is enabled.
     */
    private boolean mIsChildViewEnabled;
    
    /** Defines the selector's location and dimension at drawing time. */
    Rect mSelectorRect = new Rect();
    
    /**
     * The current position of the selector in the list.
     */
    int mSelectorPosition = INVALID_POSITION;
    
    /**
     * The listener that receives notifications when an item is clicked.
     */
    OnItemClickListener mOnItemClickListener;

    /**
     * The listener that receives notifications when an item is long clicked.
     */
    OnItemLongClickListener mOnItemLongClickListener;
    
    /** The last CheckForLongPress runnable we posted, if any. */
    private CheckForLongPress mPendingCheckForLongPress;
    
    /** Acts upon click. */
    private PerformClick mPerformClick;
    
    /** Rectangle used for hit testing children. */
    private Rect mTouchFrame;
    
    /**
     * The Class LayoutRecord.
     */
    private static final class LayoutRecord {
        
        /** The column. */
        public int column;
        
        /** The id. */
        public long id = -1;
        
        /** The height. */
        public int height;
        
        /** The span. */
        public int span;
        
        /** The m margins. */
        private int[] mMargins;

        /**
         * Ensure margins.
         */
        private final void ensureMargins() {
            if (mMargins == null) {
                // Don't need to confirm length;
                // all layoutrecords are purged when column count changes.
                mMargins = new int[span * 2];
            }
        }

        /**
         * Gets the margin above.
         *
         * @param col the col
         * @return the margin above
         */
        public final int getMarginAbove(int col) {
            if (mMargins == null) {
                return 0;
            }
            return mMargins[col * 2];
        }

        /**
         * Gets the margin below.
         *
         * @param col the col
         * @return the margin below
         */
        public final int getMarginBelow(int col) {
            if (mMargins == null) {
                return 0;
            }
            return mMargins[col * 2 + 1];
        }

        /**
         * Sets the margin above.
         *
         * @param col the col
         * @param margin the margin
         */
        public final void setMarginAbove(int col, int margin) {
            if (mMargins == null && margin == 0) {
                return;
            }
            ensureMargins();
            mMargins[col * 2] = margin;
        }

        /**
         * Sets the margin below.
         *
         * @param col the col
         * @param margin the margin
         */
        public final void setMarginBelow(int col, int margin) {
            if (mMargins == null && margin == 0) {
                return;
            }
            ensureMargins();
            mMargins[col * 2 + 1] = margin;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String result = "LayoutRecord{c=" + column + ", id=" + id + " h=" + height +
                    " s=" + span;
            if (mMargins != null) {
                result += " margins[above, below](";
                for (int i = 0; i < mMargins.length; i += 2) {
                    result += "[" + mMargins[i] + ", " + mMargins[i+1] + "]";
                }
                result += ")";
            }
            return result + "}";
        }
    }
    
    /** The m layout records. */
    private final SparseArrayCompat<LayoutRecord> mLayoutRecords =
            new SparseArrayCompat<LayoutRecord>();

    /**
     * Instantiates a new staggered grid view.
     *
     * @param context the context
     */
    public StaggeredGridView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new staggered grid view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public StaggeredGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new staggered grid view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public StaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        if(attrs!=null){
        	TypedArray a=getContext().obtainStyledAttributes( attrs, R.styleable.StaggeredGridView);
            mColCount = a.getInteger(R.styleable.StaggeredGridView_numColumns, 2);
            mDrawSelectorOnTop = a.getBoolean(R.styleable.StaggeredGridView_drawSelectorOnTop, false);
        }else{
        	mColCount = 2;
        	mDrawSelectorOnTop = false;
        }
        
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        mFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mScroller = ScrollerCompat.from(context);

        mTopEdge = new EdgeEffectCompat(context);
        mBottomEdge = new EdgeEffectCompat(context);
        setWillNotDraw(false);
        setClipToPadding(false);
        this.setFocusableInTouchMode(false);
        
        if (mSelector == null) {
            useDefaultSelector();
        }
    }

    /**
     * Set a fixed number of columns for this grid. Space will be divided evenly
     * among all columns, respecting the item margin between columns.
     * The default is 2. (If it were 1, perhaps you should be using a
     *
     * @param colCount Number of columns to display.
     * {@link android.widget.ListView ListView}.)
     * @see #setMinColumnWidth(int)
     */
    public void setColumnCount(int colCount) {
        if (colCount < 1 && colCount != COLUMN_COUNT_AUTO) {
            throw new IllegalArgumentException("Column count must be at least 1 - received " +
                    colCount);
        }
        final boolean needsPopulate = colCount != mColCount;
        mColCount = mColCountSetting = colCount;
        if (needsPopulate) {
            populate(false);
        }
    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    public int getColumnCount() {
        return mColCount;
    }

    /**
     * Set a minimum column width for.
     *
     * @param minColWidth the new min column width
     */
    public void setMinColumnWidth(int minColWidth) {
        mMinColWidth = minColWidth;
        setColumnCount(COLUMN_COUNT_AUTO);
    }

    /**
     * Set the margin between items in pixels. This margin is applied
     * both vertically and horizontally.
     *
     * @param marginPixels Spacing between items in pixels
     */
    public void setItemMargin(int marginPixels) {
        final boolean needsPopulate = marginPixels != mItemMargin;
        mItemMargin = marginPixels;
        if (needsPopulate) {
            populate(false);
        }
    }

    /**
     * Return the first adapter position with a view currently attached as
     * a child view of this grid.
     *
     * @return the adapter position represented by the view at getChildAt(0).
     */
    public int getFirstPosition() {
        return mFirstPosition;
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker.clear();
                mScroller.abortAnimation();
                mLastTouchY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mTouchRemainderY = 0;
                if (mTouchMode == TOUCH_MODE_FLINGING) {
                    // Catch!
                    mTouchMode = TOUCH_MODE_DRAGGING;
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (index < 0) {
                    Log.e(TAG, "onInterceptTouchEvent could not find pointer with id " +
                            mActivePointerId + " - did StaggeredGridView receive an inconsistent " +
                            "event stream?");
                    return false;
                }
                final float y = MotionEventCompat.getY(ev, index);
                final float dy = y - mLastTouchY + mTouchRemainderY;
                final int deltaY = (int) dy;
                mTouchRemainderY = dy - deltaY;

                if (Math.abs(dy) > mTouchSlop) {
                    mTouchMode = TOUCH_MODE_DRAGGING;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Hide selector.
     */
    void hideSelector() {
        if (this.mSelectorPosition != INVALID_POSITION) {
            // TODO: hide selector properly
        }
    }
    
    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        
        int motionPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            	
                mVelocityTracker.clear();
                mScroller.abortAnimation();
                mLastTouchY = ev.getY();
                mLastTouchX = ev.getX();
                motionPosition = pointToPosition((int) mLastTouchX, (int) mLastTouchY);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mTouchRemainderY = 0;
                
                if(mTouchMode != TOUCH_MODE_FLINGING && !mDataChanged && motionPosition >= 0 && mAdapter.isEnabled(motionPosition)){
                	mTouchMode = TOUCH_MODE_DOWN;
                	
                	mBeginClick = true;
                	
                	if (mPendingCheckForTap == null) {
                    	mPendingCheckForTap = new CheckForTap();
                    }
                    
                    postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                }
                
                mMotionPosition = motionPosition;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE: {
            	
            	
                final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (index < 0) {
                    Log.e(TAG, "onInterceptTouchEvent could not find pointer with id " +
                            mActivePointerId + " - did StaggeredGridView receive an inconsistent " +
                            "event stream?");
                    return false;
                }
                final float y = MotionEventCompat.getY(ev, index);
                final float dy = y - mLastTouchY + mTouchRemainderY;
                final int deltaY = (int) dy;
                mTouchRemainderY = dy - deltaY;

                if (Math.abs(dy) > mTouchSlop) {
                    mTouchMode = TOUCH_MODE_DRAGGING;
                }

                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    mLastTouchY = y;

                    if (!trackMotionScroll(deltaY, true)) {
                        // Break fling velocity if we impacted an edge.
                        mVelocityTracker.clear();
                    }
                }
                
                updateSelectorState();
            } break;

            case MotionEvent.ACTION_CANCEL:
                mTouchMode = TOUCH_MODE_IDLE;
                updateSelectorState();
                setPressed(false);
                View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
                if (motionView != null) {
                    motionView.setPressed(false);
                }
                final Handler handler = getHandler();
                if (handler != null) {
                    handler.removeCallbacks(mPendingCheckForLongPress);
                }
                
                if (mTopEdge != null) {
                	mTopEdge.onRelease();
                	mBottomEdge.onRelease();
                }
                
                mTouchMode = TOUCH_MODE_IDLE;
                break;

            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                final float velocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId);
                final int prevTouchMode = mTouchMode;
                
                if (Math.abs(velocity) > mFlingVelocity) { // TODO
                    mTouchMode = TOUCH_MODE_FLINGING;
                    mScroller.fling(0, 0, 0, (int) velocity, 0, 0,
                            Integer.MIN_VALUE, Integer.MAX_VALUE);
                    mLastTouchY = 0;
                    invalidate();
                } else {
                    mTouchMode = TOUCH_MODE_IDLE;
                }
                
                if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
                    // TODO : handle
                	mTouchMode = TOUCH_MODE_TAP;
                } else {
                    mTouchMode = TOUCH_MODE_REST;
                }
                
                switch(prevTouchMode){
                	case TOUCH_MODE_DOWN:
                	case TOUCH_MODE_TAP:
                	case TOUCH_MODE_DONE_WAITING:
                        final View child = getChildAt(motionPosition - mFirstPosition);
                        final float x = ev.getX();
                        final boolean inList = x > getPaddingLeft() && x < getWidth() - getPaddingRight();
                        if (child != null && !child.hasFocusable() && inList) {
                        	if (mTouchMode != TOUCH_MODE_DOWN) {
                                child.setPressed(false);
                            }
                        	
                        	if (mPerformClick == null) {
                        		invalidate();
                                mPerformClick = new PerformClick();
                            }
                        	
                        	final PerformClick performClick = mPerformClick;
                            performClick.mClickMotionPosition = motionPosition;
                            performClick.rememberWindowAttachCount();
                            
                            
                            if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP) {
                                final Handler handlerTouch = getHandler();
                                if (handlerTouch != null) {
                                	handlerTouch.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ?
                                            mPendingCheckForTap : mPendingCheckForLongPress);
                                }
                                
                                if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
                                    mTouchMode = TOUCH_MODE_TAP;
                                    
                                    layoutChildren(mDataChanged);
                                    child.setPressed(true);
                                    positionSelector(mMotionPosition, child);
                                    setPressed(true);
                                    if (mSelector != null) {
                                        Drawable d = mSelector.getCurrent();
                                        if (d != null && d instanceof TransitionDrawable) {
                                            ((TransitionDrawable) d).resetTransition();
                                        }
                                    }
                                    if (mTouchModeReset != null) {
                                        removeCallbacks(mTouchModeReset);
                                    }
                                    mTouchModeReset = new Runnable() {
                                        @Override
                                        public void run() {
                                            mTouchMode = TOUCH_MODE_REST;
                                            child.setPressed(false);
                                            setPressed(false);
                                            if (!mDataChanged) {
                                                performClick.run();
                                            }
                                        }
                                    };
                                    postDelayed(mTouchModeReset, ViewConfiguration.getPressedStateDuration());
                                    
                                } else {
                                    mTouchMode = TOUCH_MODE_REST;
                                }
                                return true;
                            } else if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
                                performClick.run();
                            }
                        }
                        
                        mTouchMode = TOUCH_MODE_REST;
                }
                
                mBeginClick = false;
                
                updateSelectorState();
            } break;
        }
        return true;
    }

    /**
     * Track motion scroll.
     *
     * @param deltaY Pixels that content should move by
     * @param allowOverScroll the allow over scroll
     * @return true if the movement completed, false if it was stopped prematurely.
     */
    private boolean trackMotionScroll(int deltaY, boolean allowOverScroll) {
        final boolean contentFits = contentFits();
        final int allowOverhang = Math.abs(deltaY);

        final int overScrolledBy;
        final int movedBy;
        if (!contentFits) {
            final int overhang;
            final boolean up;
            mPopulating = true;
            if (deltaY > 0) {
                overhang = fillUp(mFirstPosition - 1, allowOverhang)+ mItemMargin;
                up = true;
            } else {
                overhang = fillDown(mFirstPosition + getChildCount(), allowOverhang) + mItemMargin;
                up = false;
            }
            movedBy = Math.min(overhang, allowOverhang);
            offsetChildren(up ? movedBy : -movedBy);
            recycleOffscreenViews();
            mPopulating = false;
            overScrolledBy = allowOverhang - overhang;
        } else {
            overScrolledBy = allowOverhang;
            movedBy = 0;
        }

        if (allowOverScroll) {
            final int overScrollMode = ViewCompat.getOverScrollMode(this);
 
            if (overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS ||
                    (overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits)) {
                if (overScrolledBy > 0) {
                    EdgeEffectCompat edge = deltaY > 0 ? mTopEdge : mBottomEdge;
                    edge.onPull((float) Math.abs(deltaY) / getHeight());
                    invalidate();
                }
            }
        }

        if (mSelectorPosition != INVALID_POSITION) {
            final int childIndex = mSelectorPosition - mFirstPosition;
            if (childIndex >= 0 && childIndex < getChildCount()) {
                positionSelector(INVALID_POSITION, getChildAt(childIndex));
            }
        } else {
            mSelectorRect.setEmpty();
        }
        
        return deltaY == 0 || movedBy != 0;
    }

    /**
     * Content fits.
     *
     * @return true, if successful
     */
    private final boolean contentFits() {
        if (mFirstPosition != 0 || getChildCount() != mItemCount) {
            return false;
        }

        int topmost = Integer.MAX_VALUE;
        int bottommost = Integer.MIN_VALUE;
        for (int i = 0; i < mColCount; i++) {
            if (mItemTops[i] < topmost) {
                topmost = mItemTops[i];
            }
            if (mItemBottoms[i] > bottommost) {
                bottommost = mItemBottoms[i];
            }
        }

        return topmost >= getPaddingTop() && bottommost <= getHeight() - getPaddingBottom();
    }

    /**
     * Recycle all views.
     */
    private void recycleAllViews() {
        for (int i = 0; i < getChildCount(); i++) {
            mRecycler.addScrap(getChildAt(i));
        }

        if (mInLayout) {
            removeAllViewsInLayout();
        } else {
            removeAllViews();
        }
    }

    /**
     * Important: this method will leave offscreen views attached if they
     * are required to maintain the invariant that child view with index i
     * is always the view corresponding to position mFirstPosition + i.
     */
    private void recycleOffscreenViews() {
        final int height = getHeight();
        final int clearAbove = -mItemMargin;
        final int clearBelow = height + mItemMargin;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getTop() <= clearBelow)  {
                // There may be other offscreen views, but we need to maintain
                // the invariant documented above.
                break;
            }

            if (mInLayout) {
                removeViewsInLayout(i, 1);
            } else {
                removeViewAt(i);
            }

            mRecycler.addScrap(child);
        }

        while (getChildCount() > 0) {
            final View child = getChildAt(0);
            if (child.getBottom() >= clearAbove) {
                // There may be other offscreen views, but we need to maintain
                // the invariant documented above.
                break;
            }

            if (mInLayout) {
                removeViewsInLayout(0, 1);
            } else {
                removeViewAt(0);
            }

            mRecycler.addScrap(child);
            mFirstPosition++;
        }

        final int childCount = getChildCount();
        if (childCount > 0) {
            // Repair the top and bottom column boundaries from the views we still have
            Arrays.fill(mItemTops, Integer.MAX_VALUE);
            Arrays.fill(mItemBottoms, Integer.MIN_VALUE);

            for (int i = 0; i < childCount; i++){
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int top = child.getTop() - mItemMargin;
                final int bottom = child.getBottom();
                final LayoutRecord rec = mLayoutRecords.get(mFirstPosition + i);

                final int colEnd = lp.column + Math.min(mColCount, lp.span);
                for (int col = lp.column; col < colEnd; col++) {
                    final int colTop = top - rec.getMarginAbove(col - lp.column);
                    final int colBottom = bottom + rec.getMarginBelow(col - lp.column);
                    if (colTop < mItemTops[col]) {
                        mItemTops[col] = colTop;
                    }
                    if (colBottom > mItemBottoms[col]) {
                        mItemBottoms[col] = colBottom;
                    }
                }
            }

            for (int col = 0; col < mColCount; col++) {
                if (mItemTops[col] == Integer.MAX_VALUE) {
                    // If one was untouched, both were.
                    mItemTops[col] = 0;
                    mItemBottoms[col] = 0;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#computeScroll()
     */
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int y = mScroller.getCurrY();
            final int dy = (int) (y - mLastTouchY);
            mLastTouchY = y;
            final boolean stopped = !trackMotionScroll(dy, false);

            if (!stopped && !mScroller.isFinished()) {
                postInvalidate();
            } else {
                if (stopped) {
                    final int overScrollMode = ViewCompat.getOverScrollMode(this);
                    if (overScrollMode != ViewCompat.OVER_SCROLL_NEVER) {
                        final EdgeEffectCompat edge;
                        if (dy > 0) {
                            edge = mTopEdge;
                        } else {
                            edge = mBottomEdge;
                        }
                        edge.onAbsorb(Math.abs((int) mScroller.getCurrVelocity()));
                        postInvalidate();
                    }
                    mScroller.abortAnimation();
                }
                mTouchMode = TOUCH_MODE_IDLE;
            }
        }
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final boolean drawSelectorOnTop = mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }
    }
    
    /**
     * Draw selector.
     *
     * @param canvas the canvas
     */
    private void drawSelector(Canvas canvas) {
    	if (!mSelectorRect.isEmpty() && mSelector != null && mBeginClick ) {
            final Drawable selector = mSelector;
            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }
    
    /* (non-Javadoc)
     * @see android.view.View#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mTopEdge != null) {
            boolean needsInvalidate = false;
            if (!mTopEdge.isFinished()) {
                mTopEdge.draw(canvas);
                needsInvalidate = true;
            }
            if (!mBottomEdge.isFinished()) {
                final int restoreCount = canvas.save();
                final int width = getWidth();
                canvas.translate(-width, getHeight());
                canvas.rotate(180, width, 0);
                mBottomEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
                needsInvalidate = true;
            }

            if (needsInvalidate) {
                invalidate();
            }
        }
        
//        drawSelector(canvas);
    }

    /**
     * Begin fast child layout.
     */
    public void beginFastChildLayout() {
        mFastChildLayout = true;
    }

    /**
     * End fast child layout.
     */
    public void endFastChildLayout() {
        mFastChildLayout = false;
        populate(false);
    }

    /* (non-Javadoc)
     * @see android.view.View#requestLayout()
     */
    @Override
    public void requestLayout() {
        if (!mPopulating && !mFastChildLayout) {
            super.requestLayout();
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
    	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthMode = MeasureSpec.EXACTLY;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightMode = MeasureSpec.EXACTLY;
        }

        setMeasuredDimension(widthSize, heightSize);

        if (mColCountSetting == COLUMN_COUNT_AUTO) {
            final int colCount = widthSize / mMinColWidth;
            if (colCount != mColCount) {
                mColCount = colCount;
            }
        }
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	mInLayout = true;
        populate(false);
        mInLayout = false;

        final int width = r - l;
        final int height = b - t;
        mTopEdge.setSize(width, height);
        mBottomEdge.setSize(width, height);
    }

    /**
     * Populate.
     *
     * @param clearData the clear data
     */
    private void populate(boolean clearData) {

    	if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

    	if (mColCount == COLUMN_COUNT_AUTO) {
            final int colCount = getWidth() / mMinColWidth;
            if (colCount != mColCount) {
                mColCount = colCount;
            }
        }

        final int colCount = mColCount;
        
        // setup arraylist for mappings
        if(mColMappings.size() != mColCount){
        	mColMappings.clear();
        	for(int i=0; i < mColCount; i++){
        		mColMappings.add(new ArrayList<Integer>());
        	}
        }
        
        if (mItemTops == null || mItemTops.length != colCount) {
        	mItemTops = new int[colCount];
            mItemBottoms = new int[colCount];
            
            mLayoutRecords.clear();
            if (mInLayout) {
                removeAllViewsInLayout();
            } else {
                removeAllViews();
            }
        }

        final int top = getPaddingTop();
        for(int i = 0; i<colCount; i++){
        	final int offset =  top + ((mRestoreOffsets != null)? Math.min(mRestoreOffsets[i], 0) : 0);
        	mItemTops[i] = (offset == 0) ? mItemTops[i] : offset;
        	mItemBottoms[i] = (offset == 0) ? mItemBottoms[i] : offset;
        }
        
        mPopulating = true;
        
        layoutChildren(mDataChanged);
        fillDown(mFirstPosition + getChildCount(), 0);
        fillUp(mFirstPosition - 1, 0);
        mPopulating = false;
        mDataChanged = false;
        
        if(clearData){
        	if(mRestoreOffsets!=null)
        		Arrays.fill(mRestoreOffsets,0);
        }
    }

    
    
    /**
     * Offset children.
     *
     * @param offset the offset
     */
    final void offsetChildren(int offset) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.layout(child.getLeft(), child.getTop() + offset,
                    child.getRight(), child.getBottom() + offset);
        }

        final int colCount = mColCount;
        for (int i = 0; i < colCount; i++) {
            mItemTops[i] += offset;
            mItemBottoms[i] += offset;
        }
    }

    /**
     * Measure and layout all currently visible children.
     *
     * @param queryAdapter true to requery the adapter for view data
     */
    final void layoutChildren(boolean queryAdapter) {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth = (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        mColWidth = colWidth;
        int rebuildLayoutRecordsBefore = -1;
        int rebuildLayoutRecordsAfter = -1;

        Arrays.fill(mItemBottoms, Integer.MIN_VALUE);

        final int childCount = getChildCount();
        int amountRemoved = 0;
        
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int col = lp.column;
            final int position = mFirstPosition + i;
            final boolean needsLayout = queryAdapter || child.isLayoutRequested();
            
            if (queryAdapter) {
            	
                View newView = obtainView(position, child);
                if(newView == null){
                	// child has been removed 
                	removeViewAt(i);
                	if(i-1>=0) invalidateLayoutRecordsAfterPosition(i-1);
                	amountRemoved++;
                	continue;
                }else if (newView != child) {
                    removeViewAt(i);
                    addView(newView, i);
                    child = newView;
                }
                lp = (LayoutParams) child.getLayoutParams(); // Might have changed
            }

            final int span = Math.min(mColCount, lp.span);
            final int widthSize = colWidth * span + itemMargin * (span - 1);

            if (needsLayout) {
                final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

                final int heightSpec;
                if (lp.height == LayoutParams.WRAP_CONTENT) {
                    heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                } else {
                    heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
                }

                child.measure(widthSpec, heightSpec);
            }

            int childTop = mItemBottoms[col] > Integer.MIN_VALUE ? mItemBottoms[col] + mItemMargin : child.getTop();
            
            if (span > 1) {
                int lowest = childTop;
                for (int j = col + 1; j < col + span; j++) {
                    final int bottom = mItemBottoms[j] + mItemMargin;
                    if (bottom > lowest) {
                        lowest = bottom;
                    }
                }
                childTop = lowest;
            }
            final int childHeight = child.getMeasuredHeight();
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + col * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);

            for (int j = col; j < col + span; j++) {
                mItemBottoms[j] = childBottom;
            }

            final LayoutRecord rec = mLayoutRecords.get(position);
            if (rec != null && rec.height != childHeight) {
                // Invalidate our layout records for everything before this.
                rec.height = childHeight;
                rebuildLayoutRecordsBefore = position;
            }

            if (rec != null && rec.span != span) {
                // Invalidate our layout records for everything after this.
                rec.span = span;
                rebuildLayoutRecordsAfter = position;
            }
        }

        // Update mItemBottoms for any empty columns
        for (int i = 0; i < mColCount; i++) {
            if (mItemBottoms[i] == Integer.MIN_VALUE) {
                mItemBottoms[i] = mItemTops[i];
            }
        }

        if (rebuildLayoutRecordsBefore >= 0 || rebuildLayoutRecordsAfter >= 0) {
            if (rebuildLayoutRecordsBefore >= 0) {
                invalidateLayoutRecordsBeforePosition(rebuildLayoutRecordsBefore);
            }
            if (rebuildLayoutRecordsAfter >= 0) {
                invalidateLayoutRecordsAfterPosition(rebuildLayoutRecordsAfter);
            }
            for (int i = 0; i < (childCount - amountRemoved); i++) {
                final int position = mFirstPosition + i;
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                LayoutRecord rec = mLayoutRecords.get(position);
                if (rec == null) {
                    rec = new LayoutRecord();
                    mLayoutRecords.put(position, rec);
                }
                rec.column = lp.column;
                rec.height = child.getHeight();
                rec.id = lp.id;
                rec.span = Math.min(mColCount, lp.span);
            }
        }
        
        if(this.mSelectorPosition != INVALID_POSITION){
        	View child = getChildAt(mMotionPosition - mFirstPosition);
        	if (child != null) positionSelector(mMotionPosition, child);
         } else if (mTouchMode > TOUCH_MODE_DOWN) {
             View child = getChildAt(mMotionPosition - mFirstPosition);
             if (child != null) positionSelector(mMotionPosition, child);
         } else {
             mSelectorRect.setEmpty();
         }
    }

    /**
     * Invalidate layout records before position.
     *
     * @param position the position
     */
    final void invalidateLayoutRecordsBeforePosition(int position) {
        int endAt = 0;
        while (endAt < mLayoutRecords.size() && mLayoutRecords.keyAt(endAt) < position) {
            endAt++;
        }
        mLayoutRecords.removeAtRange(0, endAt);
    }

    /**
     * Invalidate layout records after position.
     *
     * @param position the position
     */
    final void invalidateLayoutRecordsAfterPosition(int position) {
        int beginAt = mLayoutRecords.size() - 1;
        while (beginAt >= 0 && mLayoutRecords.keyAt(beginAt) > position) {
            beginAt--;
        }
        beginAt++;
        mLayoutRecords.removeAtRange(beginAt + 1, mLayoutRecords.size() - beginAt);
    }

    /**
     * Should be called with mPopulating set to true.
     *
     * @param fromPosition Position to start filling from
     * @param overhang the number of extra pixels to fill beyond the current top edge
     * @return the max overhang beyond the beginning of the view of any added items at the top
     */
    final int fillUp(int fromPosition, int overhang) {
    	
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth =
                (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        mColWidth = colWidth;
        final int gridTop = getPaddingTop();
        final int fillTo = gridTop - overhang;
        int nextCol = getNextColumnUp();
        int position = fromPosition;

        while (nextCol >= 0 && mItemTops[nextCol] > fillTo && position >= 0) {
            // make sure the nextCol is correct. check to see if has been mapped 
        	// otherwise stick to getNextColumnUp()
        	if(!mColMappings.get(nextCol).contains((Integer) position)){
        		for(int i=0; i < mColMappings.size(); i++){
        			if(mColMappings.get(i).contains((Integer) position)){
        				nextCol = i;
        				break;
        			}
        		}
        	}
        	
//        	displayMapping();
        	
        	final View child = obtainView(position, null);
        	
        	if(child == null) continue;
        	
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            
            if(lp == null){
            	lp = this.generateDefaultLayoutParams();
            	child.setLayoutParams(lp);
            }
            
            if (child.getParent() != this) {
                if (mInLayout) {
                    addViewInLayout(child, 0, lp);
                } else {
                    addView(child, 0);
                }
            }

            final int span = Math.min(mColCount, lp.span);
            final int widthSize = colWidth * span + itemMargin * (span - 1);
            final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

            LayoutRecord rec;
            if (span > 1) {
                rec = getNextRecordUp(position, span);
//                nextCol = rec.column;
            } else {
                rec = mLayoutRecords.get(position);
            }

            boolean invalidateBefore = false;
            if (rec == null) {
                rec = new LayoutRecord();
                mLayoutRecords.put(position, rec);
                rec.column = nextCol;
                rec.span = span;
            } else if (span != rec.span) {
                rec.span = span;
                rec.column = nextCol;
                invalidateBefore = true;
            } else {
//                nextCol = rec.column;
            }

            if (mHasStableIds) {
                final long id = mAdapter.getItemId(position);
                rec.id = id;
                lp.id = id;
            }

            lp.column = nextCol;

            final int heightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }
            child.measure(widthSpec, heightSpec);

            final int childHeight = child.getMeasuredHeight();
            if (invalidateBefore || (childHeight != rec.height && rec.height > 0)) {
                invalidateLayoutRecordsBeforePosition(position);
            }
            rec.height = childHeight;
            
            @SuppressWarnings("unused")
			int itemTop = mItemTops[nextCol];
            
            final int startFrom;
            if (span > 1) {
                int highest = mItemTops[nextCol];
                for (int i = nextCol + 1; i < nextCol + span; i++) {
                    final int top = mItemTops[i];
                    if (top < highest) {
                        highest = top;
                    }
                }
                startFrom = highest;
            } else {
                startFrom = mItemTops[nextCol];
            }
            
            
            int childBottom = startFrom;
            int childTop = childBottom - childHeight;
            final int childLeft = paddingLeft + nextCol * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            
//            if(position == 0){
//            	if(this.getChildCount()>1 && this.mColCount>1){
//            		childTop = this.getChildAt(1).getTop();
//            		childBottom = childTop + childHeight;
//            	}
//            }
            
            child.layout(childLeft, childTop, childRight, childBottom);

            
            for (int i = nextCol; i < nextCol + span; i++) { 
                mItemTops[i] = childTop - rec.getMarginAbove(i-nextCol) - itemMargin;
            }

            nextCol = getNextColumnUp();
            mFirstPosition = position--;
        }

        int highestView = getHeight();
        
        for (int i = 0; i < mColCount; i++) {
        	final View child = getFirstChildAtColumn(i);
        	if(child == null){
        		highestView = 0;
        		break;
        	}
        	final int top = child.getTop();

        	if (top < highestView) {
                highestView = top;
            }
        }
        
        return gridTop - highestView;
    }

    
    /**
     * Gets the first child at column.
     *
     * @param column the column
     * @return the first child at column
     */
    private View getFirstChildAtColumn(int column){
    	
    	if(this.getChildCount() > column){
    		for(int i = 0; i<this.mColCount; i++){
    			final View child = getChildAt(i);
    			final int left = child.getLeft();
    			
    			
    			if(child!=null){
        			
        			int col = 0;
        			
        			// determine the column by cycling widths
        			while( left > col*(this.mColWidth + mItemMargin*2) + getPaddingLeft() ){
        				col++;
        			}
        			
        			if(col == column){
        				return child;
        			}
        			
    			}
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Should be called with mPopulating set to true.
     *
     * @param fromPosition Position to start filling from
     * @param overhang the number of extra pixels to fill beyond the current bottom edge
     * @return the max overhang beyond the end of the view of any added items at the bottom
     */
    final int fillDown(int fromPosition, int overhang) {
    	
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth = (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        final int gridBottom = getHeight() - getPaddingBottom();
        final int fillTo = gridBottom + overhang;
        int nextCol = getNextColumnDown(fromPosition);
        int position = fromPosition;

        while (nextCol >= 0 && mItemBottoms[nextCol] < fillTo && position < mItemCount) {
        	
        	final View child = obtainView(position, null);
        	
        	if(child == null) continue;
        	
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if(lp == null){
            	lp = this.generateDefaultLayoutParams();
            	child.setLayoutParams(lp);
            }
            if (child.getParent() != this) {
                if (mInLayout) {
                    addViewInLayout(child, -1, lp);
                } else {
                    addView(child);
                }
            }

            final int span = Math.min(mColCount, lp.span);
            final int widthSize = colWidth * span + itemMargin * (span - 1);
            final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

            LayoutRecord rec;
            if (span > 1) {
                rec = getNextRecordDown(position, span);
//                nextCol = rec.column;
            } else {
                rec = mLayoutRecords.get(position);
            }

            boolean invalidateAfter = false;
            if (rec == null) {
                rec = new LayoutRecord();
                mLayoutRecords.put(position, rec);
                rec.column = nextCol;
                rec.span = span;
            } else if (span != rec.span) {
                rec.span = span;
                rec.column = nextCol;
                invalidateAfter = true;
            } else {
//                nextCol = rec.column;
            }

            if (mHasStableIds) {
                final long id = mAdapter.getItemId(position);
                rec.id = id;
                lp.id = id;
            }

            lp.column = nextCol;

            final int heightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }
            child.measure(widthSpec, heightSpec);

            final int childHeight = child.getMeasuredHeight();
            if (invalidateAfter || (childHeight != rec.height && rec.height > 0)) {
                invalidateLayoutRecordsAfterPosition(position);
            }
            rec.height = childHeight;

            final int startFrom;
            if (span > 1) {
                int lowest = mItemBottoms[nextCol];
                for (int i = nextCol + 1; i < nextCol + span; i++) {
                    final int bottom = mItemBottoms[i];
                    if (bottom > lowest) {
                        lowest = bottom;
                    }
                }
                startFrom = lowest;
            } else {
                startFrom = mItemBottoms[nextCol];
            }
            
            
            
            final int childTop = startFrom + itemMargin;
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + nextCol * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);

            
            // add the position to the mapping
            if(!mColMappings.get(nextCol).contains(position)){
            	
            	// check to see if the mapping exists in other columns
            	// this would happen if list has been updated
            	for(ArrayList<Integer> list : mColMappings){
            		if(list.contains(position)){
            			list.remove((Integer) position);
            		}
            	}
            	
            	mColMappings.get(nextCol).add(position);
            	
            }
            	
            
            for (int i = nextCol; i < nextCol + span; i++) {
                mItemBottoms[i] = childBottom + rec.getMarginBelow(i - nextCol);
            }

            
            position++;
            nextCol = getNextColumnDown(position);
        }

        int lowestView = 0;
        for (int i = 0; i < mColCount; i++) {
            if (mItemBottoms[i] > lowestView) {
                lowestView = mItemBottoms[i];
            }
        }
        return lowestView - gridBottom;
    }

    /**
     * for debug purposes.
     */
    @SuppressWarnings("unused")
	private void displayMapping(){
    	Log.w("DISPLAY", "MAP ****************");
    	StringBuilder sb = new StringBuilder();
    	int col = 0;
    	
    	for(ArrayList<Integer> map : this.mColMappings){
    		sb.append("COL"+col+":");
    		sb.append(' ');
    		for(Integer i: map){
    			sb.append(i);
    			sb.append(" , ");
    		}
    		Log.w("DISPLAY", sb.toString());
    		sb = new StringBuilder();
    		col++;
    	}
    	Log.w("DISPLAY", "MAP END ****************");
    }
    
    /**
     * Gets the next column up.
     *
     * @return column that the next view filling upwards should occupy. This is the bottom-most
     * position available for a single-column item.
     */
    final int getNextColumnUp() {
        int result = -1;
        int bottomMost = Integer.MIN_VALUE;

        final int colCount = mColCount;
        for (int i = colCount - 1; i >= 0; i--) {
            final int top = mItemTops[i];
            if (top > bottomMost) {
                bottomMost = top;
                result = i;
            }
        }
        return result;
    }

    /**
     * Return a LayoutRecord for the given position.
     *
     * @param position the position
     * @param span the span
     * @return the next record up
     */
    final LayoutRecord getNextRecordUp(int position, int span) {
        LayoutRecord rec = mLayoutRecords.get(position);
        if (rec == null) {
            rec = new LayoutRecord();
            rec.span = span;
            mLayoutRecords.put(position, rec);
        } else if (rec.span != span) {
            throw new IllegalStateException("Invalid LayoutRecord! Record had span=" + rec.span +
                    " but caller requested span=" + span + " for position=" + position);
        }
        int targetCol = -1;
        int bottomMost = Integer.MIN_VALUE;

        final int colCount = mColCount;
        for (int i = colCount - span; i >= 0; i--) {
            int top = Integer.MAX_VALUE;
            for (int j = i; j < i + span; j++) {
                final int singleTop = mItemTops[j];
                if (singleTop < top) {
                    top = singleTop;
                }
            }
            if (top > bottomMost) {
                bottomMost = top;
                targetCol = i;
            }
        }

        rec.column = targetCol;

        for (int i = 0; i < span; i++) {
            rec.setMarginBelow(i, mItemTops[i + targetCol] - bottomMost);
        }

        return rec;
    }

    /**
     * Gets the next column down.
     *
     * @param position the position
     * @return column that the next view filling downwards should occupy. This is the top-most
     * position available.
     */
    final int getNextColumnDown(int position) {
        int result = -1;
        int topMost = Integer.MAX_VALUE;

        final int colCount = mColCount;
        
        for (int i = 0; i < colCount; i++) {
            final int bottom = mItemBottoms[i];
            if (bottom < topMost) {
                topMost = bottom;
                result = i;
            }
        }
        
        return result;
    }

    /**
     * Gets the next record down.
     *
     * @param position the position
     * @param span the span
     * @return the next record down
     */
    final LayoutRecord getNextRecordDown(int position, int span) {
        LayoutRecord rec = mLayoutRecords.get(position);
        if (rec == null) {
            rec = new LayoutRecord();
            rec.span = span;
            mLayoutRecords.put(position, rec);
        } else if (rec.span != span) {
            throw new IllegalStateException("Invalid LayoutRecord! Record had span=" + rec.span +
                    " but caller requested span=" + span + " for position=" + position);
        }
        int targetCol = -1;
        int topMost = Integer.MAX_VALUE;

        final int colCount = mColCount;
        for (int i = 0; i <= colCount - span; i++) {
            int bottom = Integer.MIN_VALUE;
            for (int j = i; j < i + span; j++) {
                final int singleBottom = mItemBottoms[j];
                if (singleBottom > bottom) {
                    bottom = singleBottom;
                }
            }
            if (bottom < topMost) {
                topMost = bottom;
                targetCol = i;
            }
        }

        rec.column = targetCol;

        for (int i = 0; i < span; i++) {
            rec.setMarginAbove(i, topMost - mItemBottoms[i + targetCol]);
        }

        return rec;
    }

    /**
     * Obtain a populated view from the adapter. If optScrap is non-null and is not
     * reused it will be placed in the recycle bin.
     *
     * @param position position to get view for
     * @param optScrap Optional scrap view; will be reused if possible
     * @return A new view, a recycled view from mRecycler, or optScrap
     */
    final View obtainView(int position, View optScrap) {
        View view = mRecycler.getTransientStateView(position);
        if (view != null) {
            return view;
        }
        
        if(position >= mAdapter.getCount()){
        	view = null;
        	return null;
        }

        // Reuse optScrap if it's of the right type (and not null)
        final int optType = optScrap != null ?
                ((LayoutParams) optScrap.getLayoutParams()).viewType : -1;
        final int positionViewType = mAdapter.getItemViewType(position);
        final View scrap = optType == positionViewType ?
                optScrap : mRecycler.getScrapView(positionViewType);

        view = mAdapter.getView(position, scrap, this);

        if (view != scrap && scrap != null) {
            // The adapter didn't use it; put it back.
            mRecycler.addScrap(scrap);
        }

        ViewGroup.LayoutParams lp = view.getLayoutParams();

        if (view.getParent() != this) {
            if (lp == null) {
                lp = generateDefaultLayoutParams();
            } else if (!checkLayoutParams(lp)) {
                lp = generateLayoutParams(lp);
            }
        }

        final LayoutParams sglp = (LayoutParams) lp;
        sglp.position = position;
        sglp.viewType = positionViewType;

        return view;
    }

    /**
     * Gets the adapter.
     *
     * @return the adapter
     */
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the adapter.
     *
     * @param adapter the new adapter
     */
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        // TODO: If the new adapter says that there are stable IDs, remove certain layout records
        // and onscreen views if they have changed instead of removing all of the state here.
        clearAllState();
        mAdapter = adapter;
        mDataChanged = true;
        
        if (adapter != null) {
            adapter.registerDataSetObserver(mObserver);
            mRecycler.setViewTypeCount(adapter.getViewTypeCount());
            mHasStableIds = adapter.hasStableIds();
        } else {
            mHasStableIds = false;
        }
        populate(adapter!=null);
    }

    /**
     * Clear all state because the grid will be used for a completely different set of data.
     */
    private void clearAllState() {
        // Clear all layout records and views
        mLayoutRecords.clear();
        removeAllViews();

        // Reset to the top of the grid
        resetStateForGridTop();

        // Clear recycler because there could be different view types now
        mRecycler.clear();
        
        mSelectorRect.setEmpty();
        mSelectorPosition = INVALID_POSITION;
    }

    /**
     * Reset all internal state to be at the top of the grid.
     */
    private void resetStateForGridTop() {
        // Reset mItemTops and mItemBottoms
        final int colCount = mColCount;
        if (mItemTops == null || mItemTops.length != colCount) {
            mItemTops = new int[colCount];
            mItemBottoms = new int[colCount];
        }
        final int top = getPaddingTop();
        Arrays.fill(mItemTops, top);
        Arrays.fill(mItemBottoms, top);

        // Reset the first visible position in the grid to be item 0
        mFirstPosition = 0;
        if(mRestoreOffsets!=null)
        Arrays.fill(mRestoreOffsets, 0);
    }

    /**
     * Scroll the list so the first visible position in the grid is the first item in the adapter.
     */
    public void setSelectionToTop() {
        // Clear out the views (but don't clear out the layout records or recycler because the data
        // has not changed)
        removeAllViews();

        // Reset to top of grid
        resetStateForGridTop();

        // Start populating again
        populate(false);
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#generateDefaultLayoutParams()
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT);
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#generateLayoutParams(android.view.ViewGroup.LayoutParams)
     */
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
    	return new LayoutParams(lp);
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#checkLayoutParams(android.view.ViewGroup.LayoutParams)
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#generateLayoutParams(android.util.AttributeSet)
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState ss = new SavedState(superState);
        final int position = mFirstPosition;
        ss.position = mFirstPosition;
        
        if (position >= 0 && mAdapter != null && position < mAdapter.getCount()) {
            ss.firstId = mAdapter.getItemId(position);
        }
        
        if (getChildCount() > 0) {
        	
        	int topOffsets[]= new int[this.mColCount];
        	
        	if(this.mColWidth>0)
        	for(int i =0; i < mColCount; i++){
        		if(getChildAt(i)!=null){
        			final View child = getChildAt(i);
        			final int left = child.getLeft();
        			int col = 0;
        			Log.w("mColWidth", mColWidth+" "+left);
        			
        			// determine the column by cycling widths
        			while( left > col*(this.mColWidth + mItemMargin*2) + getPaddingLeft() ){
        				col++;
        			}
        			
        			topOffsets[col] = getChildAt(i).getTop() - mItemMargin - getPaddingTop();
        		}
        			
        	}
        	
            ss.topOffsets = topOffsets;
            
            // convert nested arraylist so it can be parcelable 
            ArrayList<ColMap> convert = new ArrayList<ColMap>();
            for(ArrayList<Integer> cols : mColMappings){
            	convert.add(new ColMap(cols));
            }
            
            ss.mapping = convert;
        }
        return ss;
    }

    /* (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mDataChanged = true;
        mFirstPosition = ss.position;
        mRestoreOffsets = ss.topOffsets;
        
        ArrayList<ColMap> convert = ss.mapping;
        
        if(convert != null){
        	mColMappings.clear();
        	for(ColMap colMap : convert){
        		mColMappings.add(colMap.values);
        	}
        }
        
        if(ss.firstId>=0){
        	this.mFirstAdapterId = ss.firstId;
        	mSelectorPosition = INVALID_POSITION;	
        }
        
        requestLayout();
    }

    /**
     * The Class LayoutParams.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        
        /** The Constant LAYOUT_ATTRS. */
        private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.layout_span
        };

        /** The Constant SPAN_INDEX. */
        private static final int SPAN_INDEX = 0;

        /** The number of columns this item should span. */
        public int span = 1;

        /** Item position this view represents. */
        int position;

        /** Type of this view as reported by the adapter. */
        int viewType;

        /** The column this view is occupying. */
        int column;

        /** The stable ID of the item this view displays. */
        long id = -1;

        /**
         * Instantiates a new layout params.
         *
         * @param height the height
         */
        public LayoutParams(int height) {
            super(MATCH_PARENT, height);

            if (this.height == MATCH_PARENT) {
                Log.w(TAG, "Constructing LayoutParams with height FILL_PARENT - " +
                        "impossible! Falling back to WRAP_CONTENT");
                this.height = WRAP_CONTENT;
            }
        }

        /**
         * Instantiates a new layout params.
         *
         * @param c the c
         * @param attrs the attrs
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            if (this.width != MATCH_PARENT) {
                Log.w(TAG, "Inflation setting LayoutParams width to " + this.width +
                        " - must be MATCH_PARENT");
                this.width = MATCH_PARENT;
            }
            if (this.height == MATCH_PARENT) {
                Log.w(TAG, "Inflation setting LayoutParams height to MATCH_PARENT - " +
                        "impossible! Falling back to WRAP_CONTENT");
                this.height = WRAP_CONTENT;
            }

            TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            span = a.getInteger(SPAN_INDEX, 1);
            a.recycle();
        }

        /**
         * Instantiates a new layout params.
         *
         * @param other the other
         */
        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);

            if (this.width != MATCH_PARENT) {
                Log.w(TAG, "Constructing LayoutParams with width " + this.width +
                        " - must be MATCH_PARENT");
                this.width = MATCH_PARENT;
            }
            if (this.height == MATCH_PARENT) {
                Log.w(TAG, "Constructing LayoutParams with height MATCH_PARENT - " +
                        "impossible! Falling back to WRAP_CONTENT");
                this.height = WRAP_CONTENT;
            }
        }
    }

    /**
     * The Class RecycleBin.
     */
    private class RecycleBin {
        
        /** The m scrap views. */
        private ArrayList<View>[] mScrapViews;
        
        /** The m view type count. */
        private int mViewTypeCount;
        
        /** The m max scrap. */
        private int mMaxScrap;

        /** The m transient state views. */
        private SparseArray<View> mTransientStateViews;

        /**
         * Sets the view type count.
         *
         * @param viewTypeCount the new view type count
         */
        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Must have at least one view type (" +
                        viewTypeCount + " types reported)");
            }
            if (viewTypeCount == mViewTypeCount) {
                return;
            }

            @SuppressWarnings("unchecked")
			ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mScrapViews = scrapViews;
        }

        /**
         * Clear.
         */
        public void clear() {
            final int typeCount = mViewTypeCount;
            for (int i = 0; i < typeCount; i++) {
                mScrapViews[i].clear();
            }
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * Clear transient views.
         */
        public void clearTransientViews() {
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        /**
         * Adds the scrap.
         *
         * @param v the v
         */
        public void addScrap(View v) {
            final LayoutParams lp = (LayoutParams) v.getLayoutParams();
            if (ViewCompat.hasTransientState(v)) {
                if (mTransientStateViews == null) {
                    mTransientStateViews = new SparseArray<View>();
                }
                mTransientStateViews.put(lp.position, v);
                return;
            }

            final int childCount = getChildCount();
            if (childCount > mMaxScrap) {
                mMaxScrap = childCount;
            }

            ArrayList<View> scrap = mScrapViews[lp.viewType];
            if (scrap.size() < mMaxScrap) {
                scrap.add(v);
            }
        }

        /**
         * Gets the transient state view.
         *
         * @param position the position
         * @return the transient state view
         */
        public View getTransientStateView(int position) {
            if (mTransientStateViews == null) {
                return null;
            }

            final View result = mTransientStateViews.get(position);
            if (result != null) {
                mTransientStateViews.remove(position);
            }
            return result;
        }

        /**
         * Gets the scrap view.
         *
         * @param type the type
         * @return the scrap view
         */
        public View getScrapView(int type) {
            ArrayList<View> scrap = mScrapViews[type];
            if (scrap.isEmpty()) {
                return null;
            }

            final int index = scrap.size() - 1;
            final View result = scrap.get(index);
            scrap.remove(index);
            return result;
        }
    }

    /**
     * An asynchronous update interface for receiving notifications
     * about AdapterDataSet information as the AdapterDataSet is constructed.
     */
    private class AdapterDataSetObserver extends DataSetObserver {
        
        /* (non-Javadoc)
         * @see android.database.DataSetObserver#onChanged()
         */
        @Override
        public void onChanged() {
            mDataChanged = true;
            mItemCount = mAdapter.getCount();

            // TODO: Consider matching these back up if we have stable IDs.
            mRecycler.clearTransientViews();

            if (!mHasStableIds) {
                // Clear all layout records and recycle the views
                mLayoutRecords.clear();
                recycleAllViews();

                // Reset item bottoms to be equal to item tops
                final int colCount = mColCount;
                for (int i = 0; i < colCount; i++) {
                    mItemBottoms[i] = mItemTops[i];
                }
            }

            // reset list if position does not exist or id for position has changed
            if(mFirstPosition > mItemCount-1 || mAdapter.getItemId(mFirstPosition) != mFirstAdapterId){
            	mFirstPosition = 0;
            	Arrays.fill(mItemTops, 0);
            	Arrays.fill(mItemBottoms, 0);
            	
            	if(mRestoreOffsets!=null)
            	Arrays.fill(mRestoreOffsets, 0);
            }
            
            // TODO: consider repopulating in a deferred runnable instead
            // (so that successive changes may still be batched)
            requestLayout();
        }

        /* (non-Javadoc)
         * @see android.database.DataSetObserver#onInvalidated()
         */
        @Override
        public void onInvalidated() {
        }
    }

    /**
     * The Class ColMap.
     */
    static class ColMap implements Parcelable {
    	
	    /** The values. */
	    private ArrayList<Integer> values;
    	
	    /** The temp map. */
	    int tempMap[];
    	
    	/**
	     * Instantiates a new col map.
	     *
	     * @param values the values
	     */
	    public ColMap(ArrayList<Integer> values){
    		this.values = values;
    	}
    	
    	/**
	     * Instantiates a new col map.
	     *
	     * @param in the in
	     */
	    private ColMap(Parcel in) {
    		in.readIntArray(tempMap);
    		values = new ArrayList<Integer>();
    	    for (int index = 0; index < tempMap.length; index++) {
    	    	values.add(tempMap[index]);
    	    }
    	}
    	
		/* (non-Javadoc)
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel out, int flags) {
			tempMap = toIntArray(values);
			out.writeIntArray(tempMap);
		}
    	
		/** The Constant CREATOR. */
		public static final Creator<ColMap> CREATOR = new Creator<ColMap>() {
			public ColMap createFromParcel(Parcel source) {
				return new ColMap(source);
			}

			public ColMap[] newArray(int size) {
				return new ColMap[size];
			}
		};

		/**
		 * To int array.
		 *
		 * @param list the list
		 * @return the int[]
		 */
		int[] toIntArray(ArrayList<Integer> list) {
			int[] ret = new int[list.size()];
			for (int i = 0; i < ret.length; i++)
				ret[i] = list.get(i);
			return ret;
		}

		/* (non-Javadoc)
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}
    }
    
    /**
     * The Class SavedState.
     */
    static class SavedState extends BaseSavedState {
        
        /** The first id. */
        long firstId = -1;
        
        /** The position. */
        int position;
        
        /** The top offsets. */
        int topOffsets[];
        
        /** The mapping. */
        ArrayList<ColMap> mapping;

        /**
         * Instantiates a new saved state.
         *
         * @param superState the super state
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Instantiates a new saved state.
         *
         * @param in the in
         */
        private SavedState(Parcel in) {
            super(in);
            firstId = in.readLong();
            position = in.readInt();
            in.readIntArray(topOffsets);
            in.readTypedList(mapping, ColMap.CREATOR);
            
        }

        /* (non-Javadoc)
         * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(firstId);
            out.writeInt(position);
            out.writeIntArray(topOffsets);
            out.writeTypedList(mapping);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "StaggereGridView.SavedState{"
                        + Integer.toHexString(System.identityHashCode(this))
                        + " firstId=" + firstId
                        + " position=" + position + "}";
        }

        /** The Constant CREATOR. */
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
    /**
     * A base class for Runnables that will check that their view is still attached to
     * the original window as when the Runnable was created.
     *
     */
    private class WindowRunnnable {
        
        /** The m original attach count. */
        private int mOriginalAttachCount;

        /**
         * Remember window attach count.
         */
        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

        /**
         * Same window.
         *
         * @return true, if successful
         */
        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }
    
    /**
     * Use default selector.
     */
    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(android.R.drawable.list_selector_background));
    }
    
    
	/**
	 * Position selector.
	 *
	 * @param position the position
	 * @param sel the sel
	 */
	void positionSelector(int position, View sel) {
        if (position != INVALID_POSITION) {
            mSelectorPosition = position;
        }

        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster)sel).adjustListItemSelectionBounds(selectorRect);
        }
        
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);

        final boolean isChildViewEnabled = mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            mIsChildViewEnabled = !isChildViewEnabled;
            if (getSelectedItemPosition() != INVALID_POSITION) {
                refreshDrawableState();
            }
        }
    }
    
	/**
     * The top-level view of a list item can implement this interface to allow
     * itself to modify the bounds of the selection shown for that item.
     */
    public interface SelectionBoundsAdjuster {
        /**
         * Called to allow the list item to adjust the bounds shown for
         * its selection.
         *
         * @param bounds On call, this contains the bounds the list has
         * selected for the item (that is the bounds of the entire view).  The
         * values can be modified as desired.
         */
        public void adjustListItemSelectionBounds(Rect bounds);
    }
    
    /**
     * Gets the selected item position.
     *
     * @return the selected item position
     */
    private int getSelectedItemPosition(){
    	// TODO: setup mNextSelectedPosition
    	return this.mSelectorPosition;
    }
    
    /* (non-Javadoc)
     * @see android.view.ViewGroup#onCreateDrawableState(int)
     */
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // If the child view is enabled then do the default behavior.
        if (mIsChildViewEnabled) {
            // Common case
            return super.onCreateDrawableState(extraSpace);
        }

        // The selector uses this View's drawable state. The selected child view
        // is disabled, so we need to remove the enabled state from the drawable
        // states.
        final int enabledState = ENABLED_STATE_SET[0];

        // If we don't have any extra space, it will return one of the static state arrays,
        // and clearing the enabled state on those arrays is a bad thing!  If we specify
        // we need extra space, it will create+copy into a new array that safely mutable.
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        int enabledPos = -1;
        for (int i = state.length - 1; i >= 0; i--) {
            if (state[i] == enabledState) {
                enabledPos = i;
                break;
            }
        }

        // Remove the enabled state
        if (enabledPos >= 0) {
            System.arraycopy(state, enabledPos + 1, state, enabledPos,
                    state.length - enabledPos - 1);
        }

        return state;
    }

    
    /**
     * Position selector.
     *
     * @param l the l
     * @param t the t
     * @param r the r
     * @param b the b
     */
    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r
                + mSelectionRightPadding, b + mSelectionBottomPadding);
    }
    
    /**
     * The Class CheckForTap.
     */
    final class CheckForTap implements Runnable {
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
            	
                mTouchMode = TOUCH_MODE_TAP;
                final View child = getChildAt(mMotionPosition - mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    
                    if (!mDataChanged) {
                    	child.setSelected(true);
                    	child.setPressed(true);
                        
                        setPressed(true);
                        layoutChildren(true);
                        positionSelector(mMotionPosition, child);
                        refreshDrawableState();

                        final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        final boolean longClickable = isLongClickable();

                        if (mSelector != null) {
                            Drawable d = mSelector.getCurrent();
                            if (d instanceof TransitionDrawable) {
                                if (longClickable) {
                                    ((TransitionDrawable) d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable) d).resetTransition();
                                }
                            }
                        }

                        if (longClickable) {
                            if (mPendingCheckForLongPress == null) {
                                mPendingCheckForLongPress = new CheckForLongPress();
                            }
                            mPendingCheckForLongPress.rememberWindowAttachCount();
                            postDelayed(mPendingCheckForLongPress, longPressTimeout);
                        } else {
                            mTouchMode = TOUCH_MODE_DONE_WAITING;
                        }
                        
                        postInvalidate();
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            }
        }
    }
    
    /**
     * The Class CheckForLongPress.
     */
    private class CheckForLongPress extends WindowRunnnable implements Runnable {
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            final int motionPosition = mMotionPosition;
            final View child = getChildAt(motionPosition - mFirstPosition);
            if (child != null) {
                final int longPressPosition = mMotionPosition;
                final long longPressId = mAdapter.getItemId(mMotionPosition);

                boolean handled = false;
                if (sameWindow() && !mDataChanged) {
                    handled = performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_REST;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }
    
    /**
     * The Class PerformClick.
     */
    private class PerformClick extends WindowRunnnable implements Runnable {
        
        /** The m click motion position. */
        int mClickMotionPosition;

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // The data has changed since we posted this action in the event queue,
            // bail out before bad things happen
            if (mDataChanged) return;

            final ListAdapter adapter = mAdapter;
            final int motionPosition = mClickMotionPosition;
            if (adapter != null && mItemCount > 0 &&
                    motionPosition != INVALID_POSITION &&
                    motionPosition < adapter.getCount() && sameWindow()) {
                final View view = getChildAt(motionPosition - mFirstPosition);
                // If there is no view, something bad happened (the view scrolled off the
                // screen, etc.) and we should cancel the click
                if (view != null) {
                    performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }
    
    /**
     * Perform item click.
     *
     * @param view the view
     * @param position the position
     * @param id the id
     * @return true, if successful
     */
    public boolean performItemClick(View view, int position, long id) {
        if (mOnItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            mOnItemClickListener.onItemClick(this, view, position, id);
            return true;
        }

        return false;
    }
    
    /**
     * Perform long press.
     *
     * @param child the child
     * @param longPressPosition the long press position
     * @param longPressId the long press id
     * @return true, if successful
     */
    boolean performLongPress(final View child,
            final int longPressPosition, final long longPressId) {
    	
    	// TODO : add check for multiple choice mode.. currently modes are yet to be supported

        boolean handled = false;
        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(this);
        }
        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }
    
    /* (non-Javadoc)
     * @see android.view.View#getContextMenuInfo()
     */
    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }
    
    /**
     * Creates the ContextMenuInfo returned from {@link #getContextMenuInfo()}. This
     * methods knows the view, position and ID of the item that received the
     * long press.
     *
     * @param view The view that received the long press.
     * @param position The position of the item that received the long press.
     * @param id The ID of the item that received the long press.
     * @return The extra information that should be returned by
     *         {@link #getContextMenuInfo()}.
     */
    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }
    
    /**
     * Extra menu information provided to the.
     *
     * {@link android.view.View.OnCreateContextMenuListener#onCreateContextMenu(ContextMenu, View, ContextMenuInfo) }
     * callback when a context menu is brought up for this AdapterView.
     */
    public static class AdapterContextMenuInfo implements ContextMenu.ContextMenuInfo {

        /**
         * Instantiates a new adapter context menu info.
         *
         * @param targetView the target view
         * @param position the position
         * @param id the id
         */
        public AdapterContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }

        /**
         * The child view for which the context menu is being displayed. This
         * will be one of the children of this AdapterView.
         */
        public View targetView;

        /**
         * The position in the adapter for which the context menu is being
         * displayed.
         */
        public int position;

        /**
         * The row id of the item for which the context menu is being displayed.
         */
        public long id;
    }
    
    /**
     * Returns the selector {@link android.graphics.drawable.Drawable} that is used to draw the
     * selection in the list.
     *
     * @return the drawable used to display the selector
     */
    public Drawable getSelector() {
        return mSelector;
    }
    
    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     *
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }
    
    /* (non-Javadoc)
     * @see android.view.View#verifyDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public boolean verifyDrawable(Drawable dr) {
        return mSelector == dr || super.verifyDrawable(dr);
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#jumpDrawablesToCurrentState()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mSelector != null) mSelector.jumpToCurrentState();
    }
    
    /**
     * Sets the selector.
     *
     * @param sel the new selector
     */
    public void setSelector(Drawable sel) {
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        
        mSelector = sel;
        
        if(mSelector==null){
        	return;
        }
         
        Rect padding = new Rect();
        sel.getPadding(padding);
        mSelectionLeftPadding = padding.left;
        mSelectionTopPadding = padding.top;
        mSelectionRightPadding = padding.right;
        mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }
    
    /**
     * Update selector state.
     */
    void updateSelectorState() {
        if (mSelector != null) {
            if (shouldShowSelector()) {
                mSelector.setState(getDrawableState());
            } else {
                mSelector.setState(new int[] { 0 });
            }
        }
    }
    
    /* (non-Javadoc)
     * @see android.view.ViewGroup#drawableStateChanged()
     */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }
    
    /**
     * Indicates whether this view is in a state where the selector should be drawn. This will
     * happen if we have focus but are not in touch mode, or we are in the middle of displaying
     * the pressed state for an item.
     *
     * @return True if the selector should be shown
     */
    boolean shouldShowSelector() {
        return ((hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState()) &&  ( mBeginClick ) ;
    }
    
    /**
     * Touch mode draws in pressed state.
     *
     * @return True if the current touch mode requires that we draw the selector in the pressed
     * state.
     */
    boolean touchModeDrawsInPressedState() {
        // FIXME use isPressed for this
        switch (mTouchMode) {
        case TOUCH_MODE_TAP:
        case TOUCH_MODE_DONE_WAITING:
            return true;
        default:
            return false;
        }
    }
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Gets the on item click listener.
     *
     * @return The callback to be invoked with an item in this AdapterView has
     * been clicked, or null id no callback has been set.
     */
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }
    
    /**
     * The listener interface for receiving onItemClick events.
     * The class that is interested in processing a onItemClick
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnItemClickListener<code> method. When
     * the onItemClick event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnItemClickEvent
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(StaggeredGridView parent, View view, int position, long id);
    }
    
    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held.
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        } 
        mOnItemLongClickListener = listener;
    }

    /**
     * Gets the on item long click listener.
     *
     * @return The callback to be invoked with an item in this AdapterView has
     * been clicked and held, or null id no callback as been set.
     */
    public final OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }
    
    /**
     * The listener interface for receiving onItemLongClick events.
     * The class that is interested in processing a onItemLongClick
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnItemLongClickListener<code> method. When
     * the onItemLongClick event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnItemLongClickEvent
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in this view has been
         * clicked and held.
         *
         * Implementers can call getItemAtPosition(position) if they need to access
         * the data associated with the selected item.
         *
         * @param parent The AbsListView where the click happened
         * @param view The view within the AbsListView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         *
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id);
    }
    
    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link #INVALID_POSITION} if the point does not intersect an item.
     */
    public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

	/**
	 * Checks if is draw selector on top.
	 *
	 * @return true, if is draw selector on top
	 */
	public boolean isDrawSelectorOnTop() {
		return mDrawSelectorOnTop;
	}

	/**
	 * Sets the draw selector on top.
	 *
	 * @param mDrawSelectorOnTop the new draw selector on top
	 */
	public void setDrawSelectorOnTop(boolean mDrawSelectorOnTop) {
		this.mDrawSelectorOnTop = mDrawSelectorOnTop;
	}
    
    
}