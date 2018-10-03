package com.app.chhatrasal.zersey


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import android.os.CountDownTimer
import java.lang.Exception


class QuestionniareFragment : Fragment() {

    private lateinit var questionLayout: LinearLayout

    private lateinit var questionTextView: TextView
    private lateinit var quizResultTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var nextQuestionButton: Button

    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var optionARadioButton: RadioButton
    private lateinit var optionBRadioButton: RadioButton
    private lateinit var optionCRadioButton: RadioButton
    private lateinit var optionDRadioButton: RadioButton

    private lateinit var floatingActionButton: FloatingActionButton
    private val timer: Timer = Timer()

    private var questionNumber = 0
    private var correctAnswers = 0

    private lateinit var questionList: MutableList<QuestioniareModel>

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var helperInterface: HelperInterface

    private val onNextClickListener: View.OnClickListener = View.OnClickListener { _ ->
        updateQuestion(questionNumber)

    }

    private val onFloatingActionButtonClickListener: View.OnClickListener = View.OnClickListener { _ ->
        activity!!
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_layout, AddQuestionFragment(), null)
                .addToBackStack("backStack")
                .commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_questionniare, container, false)
        init(view)
        helperInterface.updateTitle("Take Quiz")
        return view
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val activity: Activity = context as Activity

        try {
            helperInterface = activity as HelperInterface
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun init(rootView: View) {
        questionLayout = rootView.findViewById(R.id.question_layout)

        questionTextView = rootView.findViewById(R.id.question_text_view)
        quizResultTextView = rootView.findViewById(R.id.result_text_view)
        timerTextView = rootView.findViewById(R.id.timer_text_view)

        optionsRadioGroup = rootView.findViewById(R.id.option_radio_group)
        optionARadioButton = rootView.findViewById(R.id.option_a_radio_button)
        optionBRadioButton = rootView.findViewById(R.id.option_b_radio_button)
        optionCRadioButton = rootView.findViewById(R.id.option_c_radio_button)
        optionDRadioButton = rootView.findViewById(R.id.option_d_radio_button)

        nextQuestionButton = rootView.findViewById(R.id.next_question_button)
        floatingActionButton = rootView.findViewById(R.id.floating_action_button)

        nextQuestionButton.setOnClickListener(onNextClickListener)
        floatingActionButton.setOnClickListener(onFloatingActionButtonClickListener)

        questionList = mutableListOf()

        firebaseDB
                .collection(getString(R.string.collection_name))
                .get()
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        questionList.clear()
                        for (document: DocumentSnapshot in task.result) {
                            questionList.add(document.toObject(QuestioniareModel::class.java)!!)
                        }
                    } else {
                        Log.v("#####", "Error getting documents. => " + task.exception)
                    }
                    if (questionList.size > 0) {
                        questionTextView.text = questionList[0].question
                        questionTextView.text = questionList[0].question
                        optionARadioButton.text = questionList[0].options[0]
                        optionBRadioButton.text = questionList[0].options[1]
                        optionCRadioButton.text = questionList[0].options[2]
                        optionDRadioButton.text = questionList[0].options[3]
                        questionNumber = 1
                        startTimer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                activity!!.runOnUiThread {
                                    if (questionList.size > 0) {
                                        startTimer()
                                        updateQuestion(questionNumber)
                                    }
                                }
                            }
                        }, 30000, 30000)
                    }
                }
                .addOnFailureListener(activity!!) { exception ->
                    exception.printStackTrace()
                }
    }

    fun startTimer() {
        var time = 30
        object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "0:" + checkDigit(time)
                time--
            }

            override fun onFinish() {
                timerTextView.text = "0:10"
            }

        }.start()
    }

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun checkAnswer(option: String): Boolean {
        if (option == questionList[questionNumber - 1].answer) {
            return true
        }
        return false
    }

    private fun updateQuestion(index: Int) {
        var answer = false
        when {
            optionARadioButton.isChecked -> answer = checkAnswer(optionARadioButton.text.toString())
            optionBRadioButton.isChecked -> answer = checkAnswer(optionBRadioButton.text.toString())
            optionCRadioButton.isChecked -> answer = checkAnswer(optionCRadioButton.text.toString())
            optionDRadioButton.isChecked -> answer = checkAnswer(optionDRadioButton.text.toString())
        }

        if (answer) {
            correctAnswers++
        }

        if (questionNumber < questionList.size) {
            questionTextView.text = questionList[index].question
            optionARadioButton.text = questionList[index].options[0]
            optionBRadioButton.text = questionList[index].options[1]
            optionCRadioButton.text = questionList[index].options[2]
            optionDRadioButton.text = questionList[index].options[3]
            optionsRadioGroup.clearCheck()
            questionNumber++
        } else {
            questionLayout.visibility = View.GONE
            quizResultTextView.visibility = View.VISIBLE
            quizResultTextView.text = "Your Score is : $correctAnswers"
            Toast.makeText(context, "Your Score is : $correctAnswers", Toast.LENGTH_LONG).show()
            timer.cancel()
        }
    }

}
