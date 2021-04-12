package com.evitalz.homevitalz.heartskan.ui.activities.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModelChat :ViewModel() {
    private val listOfQuestions: ArrayList<ChatActivity.Question> = ArrayList()
    val liveQuestionAndAnswer : MutableLiveData<ChatActivity.QuestionAndAnswer>  =  MutableLiveData()
    val liveEnd : MutableLiveData<Boolean>  =  MutableLiveData()
    var count = 0
    init {
        listOfQuestions.let {
            it.add(ChatActivity.Question("How are you?", listOf("fine", "not good", "okay")))
            it.add(ChatActivity.Question("Are you feeling sick?", listOf("Yes", "kind of", "Not at all")))
            it.add(
                ChatActivity.Question(
                    "Have you traveled outstation lately",
                    listOf("Yes", "little bit", "Not at all")
                )
            )
        }
        liveQuestionAndAnswer.value = ChatActivity.QuestionAndAnswer(question = listOfQuestions[0] , type = ChatActivity.QAType.QUESTION)
    }

    fun addAnswer( answer : String) = viewModelScope.launch(Dispatchers.IO){
        liveQuestionAndAnswer.postValue(ChatActivity.QuestionAndAnswer(type = ChatActivity.QAType.ANSWER , answer = answer))
        delay(1000)
        if(listOfQuestions.size > ++count){
            liveQuestionAndAnswer.postValue(ChatActivity.QuestionAndAnswer(type = ChatActivity.QAType.QUESTION, question = listOfQuestions[count]))
        }else{
            liveEnd.postValue(true)
        }

    }

}