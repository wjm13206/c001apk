package com.example.c001apk.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.absinthe.libraries.utils.extensions.dp
import com.example.c001apk.R

class SmoothInputLayout : LinearLayout {

    private var mHasInit = false
    private var mHasKeyBoard = false
    private var mIsBottom = false
    private var mDefaultKeyboardHeight = 0
    private var mMinKeyboardHeight = 0
    private var mKeyboardHeight = 0
        get() = if (field < 233.dp) 233.dp
        else field
    private var mInputViewId = 0
    private var mInputView: View? = null
    private val imm by lazy {
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SmoothInputLayout)
        mInputViewId = ta.getResourceId(R.styleable.SmoothInputLayout_inputView, 0)
        ta.recycle()
        isBottom = true
    }

    var isBottom: Boolean
        get() = mIsBottom
        set(value) {
            mIsBottom = value
            if (mIsBottom)
                setPadding(0, 0, 0, 0)
        }

    var isKeyBoardOpen: Boolean
        get() = mHasKeyBoard
        set(value) {
            mHasKeyBoard = value
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!mHasInit) {
            mHasInit = true
            initView()
        }
    }

    private fun initView() {
        if (mInputViewId > 0)
            mInputView = findViewById(mInputViewId)
        if (mInputView == null)
            mInputView = getChildAt(childCount - 2)
        mInputView?.visibility = View.GONE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!mHasInit)
            return
        if (oldh > 0 && h > 0) {
            if (oldh - h > mMinKeyboardHeight) {
                mKeyboardHeight = oldh - h
            }
            if (h - oldh > mMinKeyboardHeight) {
                if (mHasKeyBoard) {
                    mHasKeyBoard = false
                    if (mOnVisibilityChangeListener != null)
                        mOnVisibilityChangeListener!!.onHide()
                }
            }
        }
    }

    private var mOnVisibilityChangeListener: OnVisibilityChangeListener? = null

    fun setOnVisibilityChangeListener(l: OnVisibilityChangeListener) {
        mOnVisibilityChangeListener = l
    }

    interface OnVisibilityChangeListener {
        fun onShow()
        fun onHide()
    }

    fun showKeyboard() {
        if (mInputView != null && mInputView!!.visibility == View.VISIBLE)
            showInputPane(false)
        imm.showSoftInput(getChildAt(0), InputMethodManager.SHOW_FORCED)
        mHasKeyBoard = true
    }

    fun closeKeyboard(hasFocus: Boolean) {
        imm.hideSoftInputFromWindow(windowToken, 0)
        mHasKeyBoard = false
    }

    fun showInputPane(hasEmoji: Boolean) {
        mInputView?.visibility = View.VISIBLE
        mKeyboardHeight = if (mKeyboardHeight < 233.dp) 233.dp else mKeyboardHeight
        val lp = mInputView?.layoutParams
        lp?.height = mKeyboardHeight
        mInputView?.layoutParams = lp
        closeKeyboard(false)
    }

    fun closeInputPane() {
        mInputView?.visibility = View.GONE
    }

}