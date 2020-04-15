package com.droid47.petfriend.base.extensions


//fun CircularRevealFrameLayout.runRevealAnim() {
//    val color = ColorStateList.valueOf(
//        context.themeColor(R.attr.colorSurface)
//    ).defaultColor
//
//    val viewDiagonal = sqrt((width * width + height * height).toDouble())
//        .toInt()
//
//    val animatorSet = AnimatorSet()
//    val animator = ObjectAnimator.ofInt(
//        this,
//        CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
//        color,
//        Color.TRANSPARENT
//    )
//    animator.setEvaluator(ArgbEvaluatorCompat.getInstance())
//    animatorSet.duration = 600
//    val startRadius = 10f
//    val endRadius = viewDiagonal / 2f
//    val centerX = width / 2f
//    val centerY = height / 2f
//
//    post {
//        animatorSet.playTogether(
//            CircularRevealCompat.createCircularReveal(
//                this,
//                centerX,
//                centerY,
//                startRadius,
//                endRadius
//            ),
//            animator
//        )
//        visible()
//        animatorSet.start()
//    }
//
//}
//
