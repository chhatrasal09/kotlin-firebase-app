package com.app.chhatrasal.zersey


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class AddQuestionFragment : Fragment() {

    private lateinit var questionTextInputEditText: TextInputEditText
    private lateinit var optionATextInputEditText: TextInputEditText
    private lateinit var optionBTextInputEditText: TextInputEditText
    private lateinit var optionCTextInputEditText: TextInputEditText
    private lateinit var optionDTextInputEditText: TextInputEditText
    private lateinit var answerTextInputEditText: TextInputEditText
    private lateinit var submitButton: Button

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var questionNumber: Int = 0

    private val onSubmitButtonClickListener: View.OnClickListener = View.OnClickListener { _ ->
        if (!questionTextInputEditText.text!!.isEmpty() && !optionATextInputEditText.text!!.isEmpty() &&
                !optionBTextInputEditText.text!!.isEmpty() && !optionCTextInputEditText.text!!.isEmpty() &&
                !optionDTextInputEditText.text!!.isEmpty() && !answerTextInputEditText.text!!.isEmpty()) {
            val questioniareModel = QuestioniareModel(
                    questionTextInputEditText.text!!.toString(),
                    listOf(optionATextInputEditText.text!!.toString(),
                            optionBTextInputEditText.text!!.toString(),
                            optionCTextInputEditText.text!!.toString(),
                            optionDTextInputEditText.text!!.toString()),
                    answerTextInputEditText.text!!.toString())
            if (questionNumber == 0) {
                firebaseDB
                        .collection(getString(R.string.collection_name))
                        .get()
                        .addOnCompleteListener(activity!!) { task1 ->
                            if (task1.isSuccessful) {
                                questionNumber = task1.result.size()
                                addQuestion(questioniareModel, questionNumber + 1)
                            } else {
                                Toast.makeText(context, "${task1.exception!!.message}", Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener(activity!!) { exception ->
                            Toast.makeText(context, "${exception.message}", Toast.LENGTH_LONG).show()
                        }
            } else {
                addQuestion(questioniareModel, questionNumber + 1)
            }
        } else {
            var message = "Please enter "
            if (questionTextInputEditText.text!!.isEmpty()) {
                message += "Question, "
            }
            if (optionATextInputEditText.text!!.isEmpty()) {
                message += "Option A, "
            }
            if (optionBTextInputEditText.text!!.isEmpty()) {
                message += "Option B, "
            }
            if (optionCTextInputEditText.text!!.isEmpty()) {
                message += "Option C, "
            }
            if (optionDTextInputEditText.text!!.isEmpty()) {
                message += "Option D, "
            }
            if (answerTextInputEditText.text!!.isEmpty()) {
                message += "Answer."
            } else {
                message = message.substring(0, message.length - 2)
                message += "."
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

    }

    private fun addQuestion(questioniareModel: QuestioniareModel, number: Int) {
        firebaseDB
                .collection(getString(R.string.collection_name))
                .document("question$number")
                .set(questioniareModel).addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Question Added to Database Successfully.", Toast.LENGTH_LONG).show()
                        questionNumber++
                    } else {
                        Toast.makeText(context, "${task.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener(activity!!) { exception ->
                    Toast.makeText(context, "${exception.message}", Toast.LENGTH_LONG).show()
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_add_question, container, false)
        initViews(rootView)
        helperInterface.updateTitle("Create Quiz")
        clickEvents()
        return rootView
    }

    private lateinit var helperInterface: HelperInterface
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val activity: Activity = context as Activity

        try {
            helperInterface = activity as HelperInterface
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun initViews(rootView: View) {
        questionTextInputEditText = rootView.findViewById(R.id.question_edit_text)
        optionATextInputEditText = rootView.findViewById(R.id.option_a_edit_text)
        optionBTextInputEditText = rootView.findViewById(R.id.option_b_edit_text)
        optionCTextInputEditText = rootView.findViewById(R.id.option_c_edit_text)
        optionDTextInputEditText = rootView.findViewById(R.id.option_d_edit_text)
        answerTextInputEditText = rootView.findViewById(R.id.answer_edit_text)

        submitButton = rootView.findViewById(R.id.submit_button)
    }

    private fun clickEvents() {
        submitButton.setOnClickListener(onSubmitButtonClickListener)
    }

}
