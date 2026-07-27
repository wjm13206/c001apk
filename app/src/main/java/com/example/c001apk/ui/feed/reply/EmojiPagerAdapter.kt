package com.example.c001apk.ui.feed.reply

import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.libraries.utils.extensions.dp

class EmojiPagerAdapter(
    private val emojiList: List<List<Pair<String, Int>>>,
    private val onClickEmoji: (String) -> Unit
) : RecyclerView.Adapter<EmojiPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = GridView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            numColumns = 7
            verticalSpacing = 4.dp
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gridView = holder.gridView
        val data = emojiList[position]
        val adapter = EmojiGridAdapter(data) {
            onClickEmoji(it)
        }
        gridView.adapter = adapter
    }

    override fun getItemCount() = emojiList.size

    class ViewHolder(val gridView: GridView) : RecyclerView.ViewHolder(gridView)

}