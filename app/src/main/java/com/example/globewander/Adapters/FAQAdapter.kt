package com.example.globewander.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.globewander.models.FAQItem
import com.example.globewander.R
import com.example.globewander.models.FAQFetch

class FAQAdapter(private val context: Context, private val faqList: List<FAQFetch>) : BaseAdapter() {

    override fun getCount(): Int {
        return faqList.size
    }

    override fun getItem(position: Int): Any {
        return faqList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_faq, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val faqFetch = faqList[position]
        holder.questionTextView.text = "Question: ${faqFetch.question}"
        holder.answerTextView.text = "Answer: ${faqFetch.answer}"

        return view
    }

    private class ViewHolder(view: View) {
        val questionTextView: TextView = view.findViewById(R.id.questionTextView)
        val answerTextView: TextView = view.findViewById(R.id.answerTextView)
    }
}
