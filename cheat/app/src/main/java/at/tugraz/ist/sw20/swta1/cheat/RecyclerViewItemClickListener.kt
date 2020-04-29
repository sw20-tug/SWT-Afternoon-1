package at.tugraz.ist.sw20.swta1.cheat

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(context: Context?, recyclerView: RecyclerView,
                                listener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
    private val mListener: OnItemClickListener? = listener
    private var mGestureDetector: GestureDetector
    
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onLongItemClick(view: View?, position: Int)
    }
    
    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
            
            override fun onLongPress(e: MotionEvent) {
                val child =
                    recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(
                        child,
                        recyclerView.getChildAdapterPosition(child)
                    )
                }
            }
        })
    }
    
    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }
    
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}