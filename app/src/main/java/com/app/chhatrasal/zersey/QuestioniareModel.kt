package com.app.chhatrasal.zersey

import java.util.*

data class QuestioniareModel(val question: String, val options: List<String>, val answer: String) {

    constructor() : this("", listOf<String>(), "")

}