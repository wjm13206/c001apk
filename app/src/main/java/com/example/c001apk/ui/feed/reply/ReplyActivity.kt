package com.example.c001apk.ui.feed.reply

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.graphics.ColorUtils
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.absinthe.libraries.utils.extensions.dp
import com.example.c001apk.R
import com.example.c001apk.databinding.ActivityReplyBinding
import com.example.c001apk.databinding.ItemCaptchaBinding
import com.example.c001apk.ui.base.BaseActivity
import com.example.c001apk.util.EmojiUtils
import com.example.c001apk.util.PrefManager
import com.example.c001apk.util.makeToast
import com.example.c001apk.view.SmoothInputLayout
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplyActivity : BaseActivity<ActivityReplyBinding>(),
    View.OnClickListener,
    SmoothInputLayout.OnVisibilityChangeListener,
    View.OnTouchListener {

    companion object {
        const val TYPE_REPLY = "reply"
        const val TYPE_CREATE_FEED = "createFeed"
    }

    private val viewModel by viewModels<ReplyViewModel>()
    private val type: String? by lazy { intent.getStringExtra("type") }
    private val rid: String? by lazy { intent.getStringExtra("rid") }
    private val username: String? by lazy { intent.getStringExtra("username") }
    private val imm by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val color by lazy {
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.type = type
        viewModel.rid = rid

        binding.emojiBtn?.setOnClickListener(this)
        binding.checkBox.setOnClickListener(this)
        binding.editText.setOnTouchListener(this)
        binding.out.setOnTouchListener(this)
        if (binding.main is SmoothInputLayout)
            (binding.main as SmoothInputLayout).setOnVisibilityChangeListener(this)
        val radius = listOf(16.dp.toFloat(), 16.dp.toFloat(), 0f, 0f)
        val radiusBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(this@ReplyActivity.color)
            cornerRadii = floatArrayOf(
                radius[0], radius[0],
                radius[1], radius[1],
                radius[2], radius[2],
                radius[3], radius[3]
            )
        }
        if (binding.main is SmoothInputLayout) {
            binding.inputLayout.background = radiusBg
            binding.emojiLayout.setBackgroundColor(color)
        } else
            binding.bottomLayout?.background = radiusBg

        initPage()
        initEditText()
        initEmojiPanel()
        initObserve()
        showInput()

    }

    private fun initObserve() {
        viewModel.toastText.observe(this) { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                makeToast(it)
            }
        }

        viewModel.closePage.observe(this) { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                if (it) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        viewModel.showCaptcha.observe(this) { event ->
            event.getContentIfNotHandledOrReturnNull()?.let {
                val binding = ItemCaptchaBinding.inflate(
                    LayoutInflater.from(this), null, false
                )
                binding.captchaImg.setImageBitmap(it)
                binding.captchaText.highlightColor = ColorUtils.setAlphaComponent(
                    MaterialColors.getColor(
                        this,
                        android.R.attr.colorPrimaryDark,
                        0
                    ), 128
                )
                MaterialAlertDialogBuilder(this).apply {
                    setView(binding.root)
                    setTitle("captcha")
                    setNegativeButton(android.R.string.cancel, null)
                    setPositiveButton("验证并继续") { _, _ ->
                        viewModel.requestValidateData = HashMap()
                        viewModel.requestValidateData["type"] = "err_request_captcha"
                        viewModel.requestValidateData["code"] = binding.captchaText.text.toString()
                        viewModel.requestValidateData["mobile"] = ""
                        viewModel.requestValidateData["idcard"] = ""
                        viewModel.requestValidateData["name"] = ""
                        viewModel.onPostRequestValidate()
                    }
                    show()
                }
            }
        }
    }

    private fun initEditText() {
        binding.editText.highlightColor = ColorUtils.setAlphaComponent(
            MaterialColors.getColor(
                this,
                android.R.attr.colorPrimaryDark,
                0
            ), 128
        )
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    if (p0.toString().replace("\n", "").isEmpty()) {
                        binding.publish.isClickable = false
                        binding.publish.setTextColor(
                            MaterialColors.getColor(
                                this@ReplyActivity,
                                android.R.attr.colorPrimaryDark,
                                0
                            )
                        )
                    } else {
                        binding.publish.isClickable = true
                        binding.publish.setTextColor(
                            MaterialColors.getColor(
                                this@ReplyActivity,
                                android.R.attr.colorPrimary,
                                0
                            )
                        )
                    }
                }
            }
        })
    }

    private fun initPage() {
        binding.checkBox.text = if (type == "createFeed") "仅自己可见"
        else "回复并转发"
        binding.title.text = if (type == "createFeed") "发布动态"
        else "回复"
        if (type != "createFeed" && !username.isNullOrEmpty())
            binding.editText.hint = "回复: $username"

        binding.publish.setOnClickListener {
            binding.publish.isClickable = false
            if (type == "createFeed") {
                viewModel.createFeedData["message"] = binding.editText.text.toString()
                viewModel.createFeedData["type"] = "feed"
                viewModel.createFeedData["pic"] = ""
                viewModel.createFeedData["status"] = if (binding.checkBox.isChecked) "-1" else "1"
                viewModel.onPostCreateFeed()
            } else {
                viewModel.replyData["message"] = binding.editText.text.toString()
                viewModel.replyData["replyAndForward"] = if (binding.checkBox.isChecked) "1" else "0"
                viewModel.onPostReply()
            }
        }
        binding.publish.isClickable = false
    }

    private fun initEmojiPanel() {
        val data = EmojiUtils.emojiMap.toList()
        val list = ArrayList<List<Pair<String, Int>>>()
        for (i in 0..4) {
            list.add(data.subList(i * 27 + 4, (i + 1) * 27 + 4))
        }
        list.add(data.subList(139, 155))
        binding.emojiPanel.adapter = EmojiPagerAdapter(list) {
            with(binding.editText) {
                if (it == "[c001apk]") {
                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    ViewCompat.performHapticFeedback(this, HapticFeedbackConstantsCompat.CONFIRM)
                } else
                    editableText.replace(selectionStart, selectionEnd, it)
            }
        }
        binding.indicator.setViewPager(binding.emojiPanel)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.navigationBarColor = MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorSurface,
            0
        )
        window.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }

    private fun showInput() {
        binding.emojiBtn?.setImageResource(R.drawable.ic_emoji)
        binding.editText.let {
            it.requestFocus()
            it.requestFocusFromTouch()
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun showEmoji() {
        binding.emojiBtn?.setImageResource(R.drawable.ic_keyboard)
        if (binding.main is SmoothInputLayout)
            (binding.main as SmoothInputLayout).showInputPane(true)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.emojiBtn -> {
                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.CONFIRM)
                if (binding.emojiBtn?.isSelected == true) {
                    binding.emojiBtn?.isSelected = false
                    showInput()
                } else {
                    binding.emojiBtn?.isSelected = true
                    showEmoji()
                }
            }

            R.id.checkBox ->
                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.CONFIRM)
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (view.id) {
            R.id.out -> {
                if (binding.main is SmoothInputLayout && (binding.main as SmoothInputLayout).isKeyBoardOpen) {
                    (binding.main as SmoothInputLayout).closeKeyboard(false)
                } else if (binding.emojiBtn?.isSelected == true) {
                    if (binding.main is SmoothInputLayout)
                        (binding.main as SmoothInputLayout).closeInputPane()
                } else {
                    finish()
                }
            }

            R.id.editText -> {
                binding.emojiBtn?.isSelected = false
            }
        }
        return false
    }

    override fun onShow() {}

    override fun onHide() {}

}