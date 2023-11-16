package com.example.tangstory.ui.story.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tangstory.data.model.ListStoryItem
import com.example.tangstory.databinding.StoryItemBinding
import com.example.tangstory.helper.formatDate
import com.example.tangstory.ui.story.detail.DetailStroyActivity
import java.util.TimeZone

class ListStoryAdapter: PagingDataAdapter<ListStoryItem, ListStoryAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null){
            holder.bind(story)
        }
    }

    class MyViewHolder(private val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.tvItemName.text = story.name
            binding.tvDescription.text = story.description
            binding.tvDate.text = formatDate(story.createdAt.toString(),TimeZone.getDefault().id)

            itemView.setOnClickListener{
                val intent = Intent(itemView.context,DetailStroyActivity::class.java)
                intent.putExtra(DetailStroyActivity.STORY,story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto,"photo_url"),
                        Pair(binding.tvItemName,"name"),
                        Pair(binding.tvDescription,"description"),
                        Pair(binding.tvDate,"created_at"),
                    )

                itemView.context.startActivity(intent,optionsCompat.toBundle())
            }
        }
    }

    companion object{
        val DIFF_CALLBACK= object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}