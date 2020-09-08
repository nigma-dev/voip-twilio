package com.nigma.module_twilio.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


fun AppCompatActivity.pushFragment(fragment: Fragment) {
    popFragment(fragment)
    /**
     * need to return without taking any action when { @see SupportFragmentManager.isDestroyed }
     * is already destroyed, otherwise it will throw state exception
     */
    if (supportFragmentManager.isDestroyed) return
    supportFragmentManager
        .beginTransaction()
        .add(android.R.id.content, fragment)
        .addToBackStack(null)
        .commit()
}


fun AppCompatActivity.popFragment(fragment: Fragment) {
    /**
     * need to return without taking any action when { @see SupportFragmentManager.isDestroyed }
     * is already destroyed, otherwise it will throw state exception
     */
    if (supportFragmentManager.isDestroyed) return

    with(supportFragmentManager) {
        val result = findFragmentById(fragment.id) ?: findFragmentByTag(fragment.tag) ?: return
        beginTransaction()
            .remove(result)
            .addToBackStack(null)
            .commit()
    }
}


fun AppCompatActivity.removeFragmentStacks() {
    for (fg in supportFragmentManager.fragments) {
        popFragment(fg)
    }
}

