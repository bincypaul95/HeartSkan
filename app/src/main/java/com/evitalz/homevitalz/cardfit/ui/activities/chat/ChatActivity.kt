package com.evitalz.homevitalz.cardfit.ui.activities.chat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evitalz.homevitalz.cardfit.BR
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.databinding.ActivityChatBinding
import com.evitalz.homevitalz.cardfit.databinding.AnswerRowBinding
import com.evitalz.homevitalz.cardfit.databinding.ChatRowBinding
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_chat.*


@BindingAdapter("inflateData")
fun inflateData(layout: RadioGroup, data: List<String>) {
//        val inflater = LayoutInflater.from(layout.context)
    for (entry in data) {
        val myItem  = RadioButton(layout.context)
        myItem.text = entry
        layout.addView(myItem)
    }
}

class ChatActivity : AppCompatActivity() {
    val viewModel : ViewModelChat by lazy {
        ViewModelProvider(this).get(ViewModelChat::class.java)
    }
    lateinit var adapter : MyAdapter
    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyAdapter(this)
        binding.let {
            rvChat.layoutManager = LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.VERTICAL }
            rvChat.adapter = adapter
            rvChat.itemAnimator = DefaultItemAnimator().apply {
                addDuration = 1000

            }
        }

        viewModel.liveQuestionAndAnswer.observe(this, Observer {
            adapter.setData(it)
        })

        viewModel.liveEnd.observe(this, Observer {
            Snackbar.make(binding.root,"You have completed the Survey" , Snackbar.LENGTH_LONG).show()
        })

    }

    inner class MyAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var list = mutableListOf<QuestionAndAnswer>()
        inner class QuestionViewHolder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root)

        inner class AnswerViewHolder(val binding: AnswerRowBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if(viewType == 1) {
                val binding: ChatRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.chat_row, parent, false)
                QuestionViewHolder(binding)
            }else {
                val binding: AnswerRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.answer_row, parent, false)
                AnswerViewHolder(binding)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder.itemViewType == 1){
                val questionHolder = holder as QuestionViewHolder
                questionHolder.binding.let{
                    val question = list[position].question
                    it.rg.removeAllViews()
                    it.rg.setOnCheckedChangeListener(null)
                    it.setVariable(BR.question, question)
                    it.rg.setOnCheckedChangeListener { _, _ ->
                        val radioButtonID: Int = it.rg.checkedRadioButtonId
                        val radioButton: View = it.rg.findViewById(radioButtonID)
                        val idx: Int = it.rg.indexOfChild(radioButton)

                        val clickedItem = list[position].question!!.answers[idx]
                        viewModel.addAnswer(clickedItem)
                        it.rg.setOnCheckedChangeListener(null)
                    }
                    it.executePendingBindings()
                }
            }else if(holder.itemViewType == 2){
                val answerViewHolder = holder as AnswerViewHolder
                answerViewHolder.binding.let{
                    val answer = list[position].answer
                    it.setVariable(BR.answer, answer)
                    it.executePendingBindings()
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun setData(qa: QuestionAndAnswer){
//            with(this.list){
//                clear()
//                addAll(list)
//            }
            this.list.add(qa)
            notifyItemInserted(list.size - 1)
        }

        override fun getItemViewType(position: Int): Int {
            val type = list[position].type
            return if(type == QAType.QUESTION){
                1
            }else{
                2
            }
        }


    }

    class Question(val question: String, val answers: List<String>)

    enum class QAType{
        QUESTION , ANSWER
    }

    class QuestionAndAnswer(val question: Question? = null, val answer: String = "", val type: QAType)

}