package com.app.chhatrasal.zersey

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var userNameTextInputEditText: TextInputEditText
    private lateinit var passwordTextInputEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var forgotPasswordTextView: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()

        clickEvents()
    }

    private val loginButtonClickListener: View.OnClickListener = View.OnClickListener { _ ->
        if (!userNameTextInputEditText.text!!.isEmpty() && !passwordTextInputEditText.text!!.isEmpty()) {
            if (passwordTextInputEditText.text!!.length > 6) {
                auth.signInWithEmailAndPassword(userNameTextInputEditText.text!!.toString(),
                        passwordTextInputEditText.text!!.toString())
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                loginUserToHomePage()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Failed.", Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener(this@LoginActivity) { exception ->
                            exception.printStackTrace()
                        }
            } else {
                Toast.makeText(this@LoginActivity, "The password lenght is less than 6", Toast.LENGTH_LONG).show()
            }
        } else {
            var toastMessage = "Please enter "
            if (userNameTextInputEditText.text!!.isEmpty()) {
                toastMessage += "username"
                if (passwordTextInputEditText.text!!.isEmpty()) {
                    toastMessage += "and password"
                }
            }
            if (passwordTextInputEditText.text!!.isEmpty()) {
                toastMessage += "password"
            }
            Toast.makeText(this@LoginActivity, toastMessage, Toast.LENGTH_LONG).show()
        }
    }

    private val signUpButtonClickListener: View.OnClickListener = View.OnClickListener {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    private val forgotPasswordClickListener: View.OnClickListener = View.OnClickListener {
        val intent = Intent()
        startActivity(intent)
    }

    private fun initViews() {
        userNameTextInputEditText = user_name_input_edit_text
        passwordTextInputEditText = password_input_edit_text
        loginButton = login_button
        signUpButton = sign_up_button
        forgotPasswordTextView = forgot_password_text_view
    }

    private fun clickEvents() {
        loginButton.setOnClickListener(loginButtonClickListener)
        signUpButton.setOnClickListener(signUpButtonClickListener)
        forgotPasswordTextView.setOnClickListener(forgotPasswordClickListener)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            loginUserToHomePage()
        }
    }

    private fun loginUserToHomePage() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("email", auth.currentUser!!.email)
        intent.putExtra("uid", auth.currentUser!!.uid)
        Toast.makeText(this@LoginActivity, "Login Successful.", Toast.LENGTH_LONG).show()
        startActivity(intent)
        finish()
    }
}
