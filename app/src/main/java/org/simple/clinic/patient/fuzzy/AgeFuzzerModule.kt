package org.simple.clinic.patient.fuzzy

import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock

@Module
open class AgeFuzzerModule {

  @Provides
  open fun provideAgeFuzzer(clock: Clock): AgeFuzzer = PercentageFuzzer(clock, 0.1F)
}
