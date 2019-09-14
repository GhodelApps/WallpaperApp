package com.georgcantor.wallpaperapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.Hit
import com.georgcantor.wallpaperapp.model.local.db.DatabaseHelper
import com.georgcantor.wallpaperapp.model.local.db.Favorite
import com.georgcantor.wallpaperapp.ui.FavoriteActivity
import com.georgcantor.wallpaperapp.ui.PicDetailActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.favorite_list_row.view.*
import java.util.*

class FavoriteAdapter(private val context: Context,
                      private val layout: Int,
                      private val favoriteArrayList: ArrayList<Favorite>) : BaseAdapter() {

    private var db: DatabaseHelper? = null

    override fun getCount(): Int = favoriteArrayList.size

    override fun getItem(position: Int): Any = favoriteArrayList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private inner class ViewHolder {
        internal var imageView: ImageView? = null
        internal var textView: TextView? = null
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {
        var row: View? = view
        var holder = ViewHolder()

        if (row == null) {
            val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(layout, null)

            if (row != null) {
                holder.textView = row.timestampTextView
                holder.imageView = row.imgFavorite
                row.tag = holder
            }
        } else {
            holder = row.tag as ViewHolder
        }

        val favorite = favoriteArrayList[position]

        Picasso.with(context)
                .load(favorite.imageUrl)
                .placeholder(R.drawable.plh)
                .into(holder.imageView)

        holder.imageView?.setOnClickListener {
            val activity = context as Activity
            val photo = favoriteArrayList[position]
            val hitJson = photo.hit

            val gson = Gson()
            val hit = gson.fromJson(hitJson, Hit::class.java)

            val intent = Intent(context, PicDetailActivity::class.java)
            try {
                intent.putExtra(PicDetailActivity.EXTRA_PIC, hit)
            } catch (e: ArrayIndexOutOfBoundsException) {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            }
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
        }

        holder.imageView?.setOnLongClickListener {
            val photo = favoriteArrayList[position]
            val url = photo.imageUrl
            db = DatabaseHelper(context)

            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.del_from_fav_dialog)

            builder.setPositiveButton(R.string.yes) { _, _ ->
                if (url != null) {
                    db?.deleteFromFavorites(url)
                }
                val intent = Intent(context, FavoriteActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }

            builder.setNeutralButton(R.string.cancel_dialog) { _, _ -> }

            builder.setNegativeButton(R.string.no) { _, _ -> }
            builder.create().show()
            false
        }

        return row
    }
}