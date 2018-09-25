package org.simple.clinic.medicalhistory

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import org.simple.clinic.R
import org.simple.clinic.widgets.UiEvent

data class MedicalHistoryAnswerToggled(val question: MedicalHistoryQuestion, val selected: Boolean) : UiEvent {
  override val analyticsName = "New Medical History:Answer Toggled"
}

@SuppressLint("ClickableViewAccessibility")
class MedicalHistoryQuestionView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

  private val toggleSubject = PublishSubject.create<UiEvent>()!!
  private val labelTextView by bindView<TextView>(R.id.newmedicalhistory_item_label)
  private val containerFrame by bindView<ViewGroup>(R.id.newmedicalhistory_item_frame)

  private lateinit var question: MedicalHistoryQuestion

  val checkBox by bindView<CheckBox>(R.id.newmedicalhistory_item_checkbox)
  val divider by bindView<View>(R.id.newmedicalhistory_item_divider)
  val toggles = toggleSubject.hide()!!

  init {
    LayoutInflater.from(context).inflate(R.layout.list_medical_history_question, this, true)

    checkBox.setOnCheckedChangeListener { _, isChecked ->
      toggleSubject.onNext(MedicalHistoryAnswerToggled(question, isChecked))
    }

    // The entire View should show a touch feedback instead of just the CheckBox.
    setOnClickListener {
      checkBox.isChecked = checkBox.isChecked.not()
    }
    checkBox.setOnTouchListener { _, event -> this.onTouchEvent(event) }

    // Transferring padding so that external usages can set a padding on the internal frame
    // directly that contains a touch selector. <merge> would have been a better option, but
    // attributes on <merge> tag don't get merged.
    containerFrame.setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)
    setPaddingRelative(0, 0, 0, 0)
  }

  fun render(question: MedicalHistoryQuestion) {
    this.question = question
    labelTextView.setText(question.questionRes)
  }
}
