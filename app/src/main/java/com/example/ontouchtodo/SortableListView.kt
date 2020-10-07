package com.example.ontouchtodo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import androidx.core.view.updateLayoutParams

class SortableListView : ListView, AdapterView.OnItemLongClickListener {

    private val DRAG_BITMAP_CONFIG: Bitmap.Config = Bitmap.Config.ARGB_8888
    private val SCROLL_SPEED_FAST = 25
    private val SCROLL_SPEED_SLOW = 8

    private var mActionDownEvent: MotionEvent? = null
    private lateinit var mDragListener: DragListener
    private var mSortable = false
    private var mDragging = false
    private var mBitmapBackgroundColor = Color.argb(128, 0xFF, 0xFF, 0xFF)
    private var mPositionFrom: Int = 1
    private var mDragBitmap: Bitmap? = null
    private var mDragImageView: ImageView? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null

    constructor(context: Context) : super(context) {
        onItemLongClickListener = this
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        onItemLongClickListener = this
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        onItemLongClickListener = this
    }

    fun setDragListener(l: DragListener) {
        mDragListener = l
    }

    fun setSortable(sortable: Boolean) {
        this.mSortable = sortable
    }

    fun getSortable(): Boolean {
        return mSortable
    }

    override fun setBackgroundColor(color: Int) {
        mBitmapBackgroundColor = color
    }

    private fun eventToPosition(event: MotionEvent): Int {
        return pointToPosition(event.x.toInt(), event.y.toInt())
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (!mSortable) {
            return super.onTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                storeMotionEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                if (duringDrag(ev)) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (stopDrag(ev, true)) {
                    return true
                }
            }
            MotionEvent.ACTION_OUTSIDE -> {
                if (stopDrag(ev, false)) {
                    return true
                }
            }
            else -> {
                return super.onTouchEvent(ev)
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun storeMotionEvent(ev: MotionEvent) {
        mActionDownEvent = MotionEvent.obtain(ev)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        Log.i("hoge", "long touch")
        return startDragAndDrop()
    }

    private fun startDragAndDrop(): Boolean {
        mPositionFrom = eventToPosition(mActionDownEvent!!)

        if (mPositionFrom < 0) {
            return false
        }
        mDragging = true

        val view: View = getChildByIndex(mPositionFrom)
        val canvas: Canvas = Canvas()
        val wm: WindowManager = getWindowManager()

        mDragBitmap = Bitmap.createBitmap(view.width, view.height, DRAG_BITMAP_CONFIG)
        canvas.setBitmap(mDragBitmap)
        view.draw(canvas)

        if (mDragImageView != null) {
            wm.removeView(mDragImageView)
        }

        if (mLayoutParams == null) {
            initLayoutParams()
        }

        mDragImageView = ImageView(context)
        mDragImageView!!.setBackgroundColor(mBitmapBackgroundColor)
        mDragImageView!!.setImageBitmap(mDragBitmap)
        wm.addView(mDragImageView, mLayoutParams)

        if (mDragListener != null) {
            mPositionFrom = mDragListener.onStartDrag(mPositionFrom)
        }

        return duringDrag(mActionDownEvent!!)

    }

    private fun duringDrag(ev: MotionEvent): Boolean {
        if (!mDragging || mDragImageView == null) {
            return false
        }
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        val height = height
        val middle = height / 2

        var speed = 0
        val fastBound = height / 9
        val slowBound = height / 4

        when {
            ev.eventTime - ev.downTime < 500 -> speed = 0
            y < slowBound -> {
                speed = if (y < fastBound) -SCROLL_SPEED_FAST else -SCROLL_SPEED_SLOW
            }
            y > height - slowBound -> {
                speed = if (y > height - fastBound) SCROLL_SPEED_FAST else SCROLL_SPEED_SLOW
            }
        }

        if (mDragImageView!!.height < 0) {
            mDragImageView!!.visibility = View.INVISIBLE
        } else {
            mDragImageView!!.visibility = View.VISIBLE
        }
        updateLayoutParams { ev.rawY.toInt() }
        getWindowManager().updateViewLayout(mDragImageView, mLayoutParams)
        if (mDragListener != null) {
            mPositionFrom = mDragListener.onDuringDrag(mPositionFrom, pointToPosition(x, y))

        }

        return true
    }

    private fun stopDrag(ev: MotionEvent, isDrop: Boolean): Boolean {
        if (!mDragging) {
            return false
        }
        if (isDrop && mDragListener != null) {
            mDragListener.onStopDrag(mPositionFrom, eventToPosition(ev))
        }
        mDragging = false
        if (mDragImageView != null) {
            getWindowManager().removeView(mDragImageView)
            mDragImageView = null
            mDragBitmap = null

            mActionDownEvent!!.recycle()
            mActionDownEvent = null
            return true

        }
        return false
    }

    private fun getWindowManager(): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private fun getChildByIndex(index: Int): View {
        return getChildAt(index - firstVisiblePosition)
    }

    private fun initLayoutParams() {
        mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams!!.gravity = Gravity.TOP or Gravity.LEFT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        mLayoutParams!!.format = PixelFormat.TRANSLUCENT
        mLayoutParams!!.windowAnimations = 0
        mLayoutParams!!.x = left
        mLayoutParams!!.y = top
    }

    private fun updateLayoutParams(rawY: Int) {
        mLayoutParams!!.y = rawY - 32
    }

    interface DragListener {
        fun onStartDrag(position: Int): Int

        fun onDuringDrag(positionFrom: Int, positionTo: Int): Int

        fun onStopDrag(positionFrom: Int, positionTo: Int): Boolean
    }

    class SimpleDragListener : DragListener {

        override fun onStartDrag(position: Int): Int {
            return position
        }

        override fun onDuringDrag(positionFrom: Int, positionTo: Int): Int {
            return positionFrom
        }

        override fun onStopDrag(positionFrom: Int, positionTo: Int): Boolean {
            return positionFrom != positionTo && positionFrom >= 0 || positionTo >= 0
        }

    }

}