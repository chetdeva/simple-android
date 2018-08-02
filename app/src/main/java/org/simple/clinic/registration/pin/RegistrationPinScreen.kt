package org.simple.clinic.registration.pin

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotterknife.bindView
import org.simple.clinic.R
import org.simple.clinic.activity.TheActivity
import org.simple.clinic.registration.confirmpin.RegistrationConfirmPinScreen
import org.simple.clinic.router.screen.ScreenRouter
import javax.inject.Inject

class RegistrationPinScreen(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  @Inject
  lateinit var screenRouter: ScreenRouter

  @Inject
  lateinit var controller: RegistrationPinScreenController

  private val backButton by bindView<ImageButton>(R.id.registrationpin_back)
  private val pinEditText by bindView<EditText>(R.id.registrationpin_pin)
  private val nextButton by bindView<Button>(R.id.registrationpin_next)

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) {
      return
    }
    TheActivity.component.inject(this)

    backButton.setOnClickListener {
      screenRouter.pop()
    }

    Observable.merge(pinTextChanges(), nextClicks())
        .observeOn(io())
        .compose(controller)
        .observeOn(mainThread())
        .takeUntil(RxView.detaches(this))
        .subscribe { uiChange -> uiChange(this) }
  }

  private fun pinTextChanges() =
      RxTextView.textChanges(pinEditText)
          .map(CharSequence::toString)
          .map(::RegistrationPinTextChanged)

  private fun nextClicks() =
      RxView.clicks(nextButton)
          .map { RegistrationPinNextClicked() }

  fun openRegistrationConfirmPinScreen() {
    screenRouter.push(RegistrationConfirmPinScreen.KEY)
  }

  fun setNextButtonEnabled(enabled: Boolean) {
    nextButton.isEnabled = enabled
  }

  companion object {
    val KEY = RegistrationPinScreenKey()
  }
}